package com.epsilon.model.input.impl;

import java.sql.PreparedStatement;

import com.epsilon.GameSettings;
import com.epsilon.model.input.Input;
import com.epsilon.world.content.Achievements;
import com.epsilon.world.content.PlayerPanel;
import com.epsilon.world.content.Achievements.AchievementData;
import com.epsilon.world.entity.impl.player.Player;

import mysql.MySQLController;
import mysql.MySQLDatabase;
import mysql.MySQLController.Database;

public class SetEmail extends Input {

	@Override
	public void handleSyntax(Player player, String syntax) {
		player.getPacketSender().sendInterfaceRemoval();
		if(!GameSettings.MYSQL_ENABLED) {
			player.getPacketSender().sendMessage("This service is currently unavailable.");
			return;
		}
		if(syntax.length() <= 3 || !syntax.contains("@") || syntax.endsWith("@")) {
			player.getPacketSender().sendMessage("Invalid syntax, please enter a valid one.");
			return;
		}
		if(player.getBankPinAttributes().hasBankPin() && !player.getBankPinAttributes().hasEnteredBankPin()) {
			player.getPacketSender().sendMessage("Please visit the nearest bank and enter your pin before doing this.");
			return;
		}
/*		MySQLDatabase recovery = MySQLController.getController().getDatabase(Database.RECOVERY);
		if(!recovery.active || recovery.getConnection() == null) {
			player.getPacketSender().sendMessage("This service is currently unavailable.");
			return;
		}
		if(player.getEmailAddress() != null && syntax.equalsIgnoreCase(player.getEmailAddress())) {
			player.getPacketSender().sendMessage("This is already your email-address!");
			return;
		}
		syntax = syntax.toLowerCase();
		syntax = syntax.substring(0, 1).toUpperCase() + syntax.substring(1);
		
		try {
			PreparedStatement preparedStatement = recovery.getConnection().prepareStatement("DELETE FROM users WHERE USERNAME = ?");
			preparedStatement.setString(1, player.getUsername());
			preparedStatement.executeUpdate();
			preparedStatement = recovery.getConnection().prepareStatement("INSERT INTO users (username,password,email) VALUES (?, ?, ?)");
			preparedStatement.setString(1, player.getUsername());
			preparedStatement.setString(2, player.getPassword());
			preparedStatement.setString(3, syntax);
			preparedStatement.executeUpdate();
			success = true;
		} catch(Exception e) {
			e.printStackTrace();
			success = false;
		}*/
		boolean success = false;
		if(success) {
			player.setEmailAddress(syntax);
			player.getPacketSender().sendMessage("Your account's email-adress is now: "+syntax);
			Achievements.finishAchievement(player, AchievementData.SET_AN_EMAIL_ADDRESS);
			PlayerPanel.refreshPanel(player);
		} else {
			player.getPacketSender().sendMessage("An error occured. Please try again.");
		}
	}
}
