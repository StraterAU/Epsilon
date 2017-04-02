package mysql.impl.playersonline;

import java.sql.PreparedStatement;

import com.epsilon.GameServer;
import com.epsilon.GameSettings;
import com.epsilon.world.World;

import mysql.MySQLController;
import mysql.MySQLDatabase;
import mysql.MySQLController.Database;

public class TotalPlayers {
	
	public static void update() {
		if(!GameSettings.MYSQL_ENABLED) {
			return;
		}

		MySQLDatabase login = MySQLController.getController().getDatabase(Database.USERS_ONLINE);
		GameServer.getLoader().getEngine().submit(() -> {
			try {
				PreparedStatement preparedStatement = login.getConnection().prepareStatement("INSERT INTO totalplayers (total) VALUES (?)");
				preparedStatement.setInt(1, World.getPlayers().size());
				preparedStatement.executeUpdate();
			} catch(Exception e) {
				e.printStackTrace();
			}
		});

	}

}
