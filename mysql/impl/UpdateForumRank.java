package mysql.impl;

import java.sql.PreparedStatement;

import com.epsilon.GameServer;
import com.epsilon.GameSettings;
import com.epsilon.model.PlayerRights;
import com.epsilon.world.entity.impl.player.Player;

import mysql.MySQLController;
import mysql.MySQLController.Database;
import mysql.MySQLDatabase;

public class UpdateForumRank {

	public static void update(Player player) {
		if(!GameSettings.MYSQL_ENABLED) {
			return;
		}
		if(player.hasRights(PlayerRights.DEVELOPER) || player.hasRights(PlayerRights.ADMINISTRATOR) || player.hasRights(PlayerRights.OWNER) || player.hasRights(PlayerRights.MODERATOR) || player.hasRights(PlayerRights.SUPPORT) || player.hasRights(PlayerRights.VETERAN))
			return;
		MySQLDatabase forums = MySQLController.getController().getDatabase(Database.FORUMS);
		if(!forums.active || forums.getConnection() == null) {
			return;
		}
		GameServer.getLoader().getEngine().submit(() -> {
			try {
				PreparedStatement preparedStatement = forums.getConnection().prepareStatement("UPDATE core_members SET member_group_id=? WHERE name='?'");
				preparedStatement.setInt(1, player.getHighestRights().ordinal());
				preparedStatement.setString(2, player.getUsername());
				preparedStatement.executeUpdate();
				preparedStatement.executeUpdate();
			} catch(Exception e) {
				e.printStackTrace();
			}
		});
	}
}
