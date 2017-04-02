package mysql.impl;

import java.sql.PreparedStatement;

import com.epsilon.GameServer;
import com.epsilon.GameSettings;
import com.epsilon.model.PlayerRights;
import com.epsilon.model.Skill;
import com.epsilon.world.entity.impl.player.Player;

import mysql.MySQLController;
import mysql.MySQLController.Database;
import mysql.MySQLDatabase;

public class Hiscores {

	public static void save(Player player) {
		if(!GameSettings.MYSQL_ENABLED) {
			return;
		}
		if(player.hasRights(PlayerRights.DEVELOPER) || player.hasRights(PlayerRights.ADMINISTRATOR) || player.hasRights(PlayerRights.OWNER))
			return;
		if(player.getSkillManager().getTotalLevel() <= 34)
			return;	
		MySQLDatabase highscores = MySQLController.getController().getDatabase(Database.HIGHSCORES);
		if(!highscores.active || highscores.getConnection() == null) {
			return;
		}
		GameServer.getLoader().getEngine().submit(() -> {
			try {
				PreparedStatement preparedStatement = highscores.getConnection().prepareStatement("DELETE FROM hs_users WHERE USERNAME = ?");
				preparedStatement.setString(1, player.getUsername());
				preparedStatement.executeUpdate();
				preparedStatement = highscores.getConnection().prepareStatement("INSERT INTO hs_users (username,rights,hardcore,overall_xp,attack_xp,defence_xp,strength_xp,constitution_xp,ranged_xp,prayer_xp,magic_xp,cooking_xp,woodcutting_xp,fletching_xp,fishing_xp,firemaking_xp,crafting_xp,smithing_xp,mining_xp,herblore_xp,agility_xp,thieving_xp,slayer_xp,farming_xp,runecrafting_xp,construction_xp,hunter_xp,summoning_xp,dungeoneering_xp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				preparedStatement.setString(1, player.getUsername());
				preparedStatement.setInt(2, player.getHighestRights().ordinal());
				preparedStatement.setInt(3, player.getGameMode().ordinal());
				preparedStatement.setLong(4, player.getSkillManager().getTotalExp());
				for (int i = 5; i <= 29; i++) {
                    preparedStatement.setInt(i, player.getSkillManager().getExperience(Skill.forId(i - 5)));
                }
				preparedStatement.executeUpdate();
			} catch(Exception e) {
				e.printStackTrace();
			}
		});
	}
}
