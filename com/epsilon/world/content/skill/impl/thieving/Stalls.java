package com.epsilon.world.content.skill.impl.thieving;

import com.epsilon.model.Animation;
import com.epsilon.model.GameMode;
import com.epsilon.model.Skill;
import com.epsilon.util.Misc;
import com.epsilon.world.content.Achievements;
import com.epsilon.world.content.Achievements.AchievementData;
import com.epsilon.world.content.random.impl.SandwichLady;
import com.epsilon.world.entity.impl.player.Player;

public class Stalls {
	
	public static final int GLOVES_OF_SILENCE = 10075;
	
	public static final int[] ITEMS = { GLOVES_OF_SILENCE };

	public static void stealFromStall(Player player, int lvlreq, int xp, int reward, String message) {
		if(player.getInventory().getFreeSlots() < 1) {
			player.getPacketSender().sendMessage("You need some more inventory space to do this.");
			return;
		}
		if (player.getCombatBuilder().isBeingAttacked()) {
			player.getPacketSender().sendMessage("You must wait a few seconds after being out of combat before doing this.");
			return;
		}
		if(!player.getClickDelay().elapsed(2000))
			return;
		if(player.getSkillManager().getMaxLevel(Skill.THIEVING) < lvlreq) {
			player.getPacketSender().sendMessage("You need a Thieving level of at least " + lvlreq + " to steal from this stall.");
			return;
		}
		player.performAnimation(new Animation(881));
		player.getPacketSender().sendMessage(message);
		player.getPacketSender().sendInterfaceRemoval();
		if(player.getGameMode() == GameMode.EXTREME) {
			player.getSkillManager().addExperience(Skill.THIEVING, (int) (xp*0.25));
		} else {
			player.getSkillManager().addExperience(Skill.THIEVING, xp);
		}
		
		player.getClickDelay().reset();
		player.getInventory().add(reward, 1);
		int random = Misc.getRandom(400);
		if(random <= 2 && player.getSandwichLady() == null) {
			player.setSandwichLady(new SandwichLady(player));
			player.getSandwichLady().spawn();
		}
		random = Misc.getRandom(100);
		if(random  <= 10 && player.getEquipment().contains(GLOVES_OF_SILENCE)) {
			int free = player.getInventory().getFreeSlots();
			random = Misc.getRandom(4);
			
			if(random > free) {
				random = free;
			}
			player.getInventory().add(reward, random);
		}
		random = Misc.getRandom(100);
		if(random >= 1 && random <= 3) {
			Misc.giveReward(player, ITEMS);
		}
		player.getSkillManager().stopSkilling();
		if(reward == 15009)
			Achievements.finishAchievement(player, AchievementData.STEAL_A_RING);
		else if(reward == 11998) {
			Achievements.doProgress(player, AchievementData.STEAL_140_SCIMITARS);
			Achievements.doProgress(player, AchievementData.STEAL_5000_SCIMITARS);
		}
	}

}
