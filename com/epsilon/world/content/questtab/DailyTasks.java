package com.epsilon.world.content.questtab;

import com.epsilon.util.Misc;
import com.epsilon.world.content.minigames.impl.Nomad;
import com.epsilon.world.content.minigames.impl.RecipeForDisaster;
import com.epsilon.world.content.skill.impl.slayer.SlayerTasks;
import com.epsilon.world.entity.impl.player.Player;

public class DailyTasks {
	
	public static void display(Player player) {
		player.getPacketSender().sendString(50007, "Adventures");
		boolean expLock = player.experienceLocked();
		Player.serverInformation = false;
		Player.userStatistics = false;
		Player.dailyTasks = true;
		Player.links = false;
		for (int i = 50051; i <= 50071; i++) {
			player.getPacketSender().sendString(i, "");
		}
		player.getPacketSender().sendString(50051, "@or1@ - @whi@ Quests");
		player.getPacketSender().sendString(50052, RecipeForDisaster.getQuestTabPrefix(player) + "Recipe For Disaster");
		player.getPacketSender().sendString(50053, Nomad.getQuestTabPrefix(player) + "Nomad's Requeim");
	}

}
