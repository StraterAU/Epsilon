package mysql.impl.playersonline;

import java.sql.PreparedStatement;

import com.epsilon.GameServer;
import com.epsilon.GameSettings;
import com.epsilon.model.PlayerRights;
import com.epsilon.util.Misc;
import com.epsilon.world.entity.impl.player.Player;

import mysql.MySQLController;
import mysql.MySQLDatabase;
import mysql.MySQLController.Database;

public class RegularPlayer {
	
	public static void login(Player p) {
		if(!GameSettings.MYSQL_ENABLED) {
			return;
		}
		if (p.getHighestRights().isStaff()) {
			return;
		}
		
		MySQLDatabase login = MySQLController.getController().getDatabase(Database.USERS_ONLINE);
		GameServer.getLoader().getEngine().submit(() -> {
			try {
				PreparedStatement preparedStatement = login.getConnection().prepareStatement("INSERT INTO player (username) VALUES (?)");
				preparedStatement.setString(1, p.getUsername());
				preparedStatement.executeUpdate();
			} catch(Exception e) {
				e.printStackTrace();
			}
		});

	}
	
	public static void logout(Player p) {
		if(!GameSettings.MYSQL_ENABLED) {
			return;
		}
		if (p.getHighestRights().isStaff()) {
			return;
		}
		
		MySQLDatabase login = MySQLController.getController().getDatabase(Database.USERS_ONLINE);
		GameServer.getLoader().getEngine().submit(() -> {
			try {
				PreparedStatement preparedStatement = login.getConnection().prepareStatement("DELETE FROM player WHERE username = ?");
				preparedStatement.setString(1, Misc.formatPlayerName(p.getUsername()));
				preparedStatement.executeUpdate();
			} catch(Exception e) {
				e.printStackTrace();
			}
		});
	}

}
