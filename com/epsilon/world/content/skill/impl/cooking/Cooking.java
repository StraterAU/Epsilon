package com.epsilon.world.content.skill.impl.cooking;

import com.epsilon.engine.task.Task;
import com.epsilon.engine.task.TaskManager;
import com.epsilon.model.Animation;
import com.epsilon.model.GameMode;
import com.epsilon.model.Skill;
import com.epsilon.model.definitions.ItemDefinition;
import com.epsilon.model.input.impl.EnterAmountToCook;
import com.epsilon.world.content.Achievements;
import com.epsilon.world.content.Achievements.AchievementData;
import com.epsilon.world.content.ExperienceModes;
import com.epsilon.world.entity.impl.player.Player;

public class Cooking {
	
	public static void selectionInterface(Player player, CookingData cookingData) {
		if(cookingData == null)
			return;
		player.setSelectedSkillingItem(cookingData.getRawItem());
		player.setInputHandling(new EnterAmountToCook());
		player.getPacketSender().sendString(2799, ItemDefinition.forId(cookingData.getCookedItem()).getName()).sendInterfaceModel(1746, cookingData.getCookedItem(), 150).sendChatboxInterface(4429);
		player.getPacketSender().sendString(2800, "How many would you like to cook?");
	}
	
	public static void cook(final Player player, final int rawFish, final int amount) {
		final CookingData fish = CookingData.forFish(rawFish);
		if(fish == null)
			return;
		player.getSkillManager().stopSkilling();
		player.getPacketSender().sendInterfaceRemoval();
		if(!CookingData.canCook(player, rawFish))
			return;
		player.performAnimation(new Animation(896));
		player.setCurrentTask(new Task(2, player, false) {
			int amountCooked = 0;
			@Override
			public void execute() {
				if(!CookingData.canCook(player, rawFish)) {
					stop();
					return;
				}
				player.performAnimation(new Animation(896));
				player.getInventory().delete(rawFish, 1);
				if(!CookingData.success(player, 3, fish.getLevelReq(), fish.getStopBurn())) {
					player.getInventory().add(fish.getBurntItem(), 1);
					player.getPacketSender().sendMessage("You accidently burn the "+fish.getName()+".");
				} else {
					player.getInventory().add(fish.getCookedItem(), 1);
						
						if (player.getExperienceMode() == ExperienceModes.EXTREME)
							player.getSkillManager().addExperience(Skill.COOKING, fish.getXp() * 100);
							if (player.getExperienceMode() == ExperienceModes.LEGEND)
								player.getSkillManager().addExperience(Skill.COOKING, fish.getXp() * 50);
							if (player.getExperienceMode() == ExperienceModes.IMMORTAL)
								player.getSkillManager().addExperience(Skill.COOKING, fish.getXp() * 10);
							if (player.getExperienceMode() == ExperienceModes.GRANDMASTER)
								player.getSkillManager().addExperience(Skill.COOKING, fish.getXp() * 2);
							if (player.getExperienceMode() == ExperienceModes.IRONMAN)
								player.getSkillManager().addExperience(Skill.COOKING, fish.getXp() * 25);
							if (player.getExperienceMode() == ExperienceModes.HARDCOREIRONMAN)
								player.getSkillManager().addExperience(Skill.COOKING, fish.getXp() * 5);
					if(fish == CookingData.SALMON) {
						Achievements.finishAchievement(player, AchievementData.COOK_A_SALMON);
					} else if(fish == CookingData.ROCKTAIL) {
						Achievements.doProgress(player, AchievementData.COOK_25_ROCKTAILS);
						Achievements.doProgress(player, AchievementData.COOK_1000_ROCKTAILS);
					}
				}
				amountCooked++;
				if(amountCooked >= amount)
					stop();
			}
			@Override
			public void stop() {
				setEventRunning(false);
				player.setSelectedSkillingItem(-1);
				player.performAnimation(new Animation(65535));
			}		
		});
		TaskManager.submit(player.getCurrentTask());
	}
}
