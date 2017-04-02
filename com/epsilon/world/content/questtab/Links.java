package com.epsilon.world.content.questtab;

import com.epsilon.world.content.minigames.impl.Nomad;
import com.epsilon.world.content.minigames.impl.RecipeForDisaster;
import com.epsilon.world.entity.impl.player.Player;

public class Links {
	
	public static void display(Player player) {
		player.getPacketSender().sendString(50007, "Links");
		boolean expLock = player.experienceLocked();
		Player.serverInformation = false;
		Player.userStatistics = false;
		Player.dailyTasks = false;
		Player.links = true;
		for (int i = 50051; i <= 50071; i++) {
			player.getPacketSender().sendString(i, "");
		}
		player.getPacketSender().sendString(50051, "@or1@Website");
		player.getPacketSender().sendString(50052, "@or1@Forum");
		player.getPacketSender().sendString(50053, "@or1@Hiscores");
		player.getPacketSender().sendString(50054, "@or1@Vote");
		player.getPacketSender().sendString(50055, "@or1@Store");
		player.getPacketSender().sendString(50056, "@or1@Recovery");
		player.getPacketSender().sendString(50057, "@or1@Support");
	}

}
