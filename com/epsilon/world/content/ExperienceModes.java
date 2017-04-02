package com.epsilon.world.content;

import com.epsilon.model.Flag;
import com.epsilon.model.GameMode;
import com.epsilon.model.container.impl.Bank;
import com.epsilon.world.content.skill.impl.slayer.SlayerMaster;
import com.epsilon.world.content.skill.impl.slayer.SlayerTasks;
import com.epsilon.world.entity.impl.player.Player;

public enum ExperienceModes {
	
	EXTREME, //x100
	LEGEND, //x50
	IMMORTAL, //x10
	GRANDMASTER, //x2
	IRONMAN, //x25
	HARDCOREIRONMAN; //x5
	
		public static void assignXpMode (Player player, ExperienceModes mode) {
			if (player.getExperienceMode() == mode) {
				player.getPacketSender().sendMessage("You cannot change your game mode to your current game mode.");
				return;
			}
			if (mode != ExperienceModes.GRANDMASTER) {
				player.getInventory().delete(7806, 1);
				for(Bank b : player.getBanks()) {
					b.delete(7806, 500);
				}
			} else {
				player.getInventory().add(7806, 1);
			}
			player.getSlayer().resetSlayerTask();
			player.getSlayer().setSlayerTask(SlayerTasks.NO_TASK).setAmountToSlay(0).setSlayerMaster(SlayerMaster.VANNAKA);
			PlayerPanel.refreshPanel(player);
			player.getUpdateFlag().flag(Flag.APPEARANCE);				
			if(player.didReceiveStarter()) {
				if(mode != ExperienceModes.EXTREME && mode != ExperienceModes.LEGEND && mode != ExperienceModes.IMMORTAL && mode != ExperienceModes.GRANDMASTER) {
					player.getInventory().add(995, 10000).add(1153, 1).add(1115, 1).add(1067, 1).add(1323, 1).add(1191, 1).add(841, 1).add(882, 50).add(1167, 1).add(1129, 1).add(1095, 1).add(1063, 1).add(579, 1).add(577, 1).add(1011, 1).add(1379, 1).add(556, 50).add(558, 50).add(557, 50).add(555, 50).add(1351, 1).add(1265, 1).add(1712, 1).add(11118, 1).add(1007, 1).add(1061, 1).add(330, 100).add(1419, 1);
				} else {
					player.getInventory().add(995, 5000000).add(1153, 1).add(1115, 1).add(1067, 1).add(1323, 1).add(1191, 1).add(841, 1).add(882, 1000).add(1167, 1).add(1129, 1).add(1095, 1).add(1063, 1).add(579, 1).add(577, 1).add(1011, 1).add(1379, 1).add(556, 1000).add(558, 1000).add(557, 1000).add(555, 1000).add(1351, 1).add(1265, 1).add(1712, 1).add(11118, 1).add(1007, 1).add(1061, 1).add(386, 100).add(1419, 1);
				}
			} else {
				player.getPacketSender().sendMessage("Your connection has received enough starting items.");
			}

		}
}
