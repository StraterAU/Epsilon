package com.epsilon.world.content.skill.impl.agility;

import com.epsilon.model.GameObject;
import com.epsilon.model.Skill;
import com.epsilon.model.container.impl.Equipment;
import com.epsilon.util.Misc;
import com.epsilon.world.content.Achievements;
import com.epsilon.world.content.Achievements.AchievementData;
import com.epsilon.world.content.ExperienceModes;
import com.epsilon.world.entity.impl.player.Player;

public class Agility {

	public static boolean handleObject(Player p, GameObject object) {
		if(object.getId() == 2309) {
			if(p.getSkillManager().getMaxLevel(Skill.AGILITY) < 55) {
				p.getPacketSender().sendMessage("You need an Agility level of at least 55 to enter this course.");
				return true;
			}
		}
		ObstacleData agilityObject = ObstacleData.forId(object.getId());
		if(agilityObject != null) {
			if(p.isCrossingObstacle())
				return true;
			p.setPositionToFace(object.getPosition());
			p.setResetPosition(p.getPosition());
			p.setCrossingObstacle(true);
			//boolean wasRunning = p.getAttributes().isRunning();
			//if(agilityObject.mustWalk()) {
				//p.getAttributes().setRunning(false);
			//	p.getPacketSender().sendRunStatus();
			//}
			agilityObject.cross(p);
			Achievements.finishAchievement(p, AchievementData.CLIMB_AN_AGILITY_OBSTACLE);
			Achievements.doProgress(p, AchievementData.CLIMB_50_AGILITY_OBSTACLES);
		}
		return false;
	}

	public static boolean passedAllObstacles(Player player) {
		for(boolean crossedObstacle : player.getCrossedObstacles()) {
			if(!crossedObstacle)
				return false;
		}
		return true;
	}

	public static void resetProgress(Player player) {
		for(int i = 0; i < player.getCrossedObstacles().length; i++)
			player.setCrossedObstacle(i, false);
	}
	
	public static boolean isSucessive(Player player) {
		return Misc.getRandom(player.getSkillManager().getCurrentLevel(Skill.AGILITY) / 2) > 1;
	}
	
	public static void addExperience(Player player, int experience) {
		boolean agile = player.getEquipment().get(Equipment.BODY_SLOT).getId() == 14936 && player.getEquipment().get(Equipment.LEG_SLOT).getId() == 14938;
		if (player.getExperienceMode() == ExperienceModes.EXTREME)
			player.getSkillManager().addExperience(Skill.AGILITY, agile ? ((experience * 100) * 2) : experience * 100);
			if (player.getExperienceMode() == ExperienceModes.LEGEND)
				player.getSkillManager().addExperience(Skill.AGILITY, agile ? ((experience * 50) * 2) : experience * 50);
			if (player.getExperienceMode() == ExperienceModes.IMMORTAL)
				player.getSkillManager().addExperience(Skill.AGILITY, agile ? ((experience * 10) * 2) : experience * 10);
			if (player.getExperienceMode() == ExperienceModes.GRANDMASTER)
				player.getSkillManager().addExperience(Skill.AGILITY, agile ? ((experience * 2) * 2) : experience * 2);
			if (player.getExperienceMode() == ExperienceModes.IRONMAN)
				player.getSkillManager().addExperience(Skill.AGILITY, agile ? ((experience * 25) * 2) : experience * 25);
			if (player.getExperienceMode() == ExperienceModes.HARDCOREIRONMAN)
				player.getSkillManager().addExperience(Skill.AGILITY, agile ? ((experience * 5) * 2) : experience * 5);
		
	}
}
