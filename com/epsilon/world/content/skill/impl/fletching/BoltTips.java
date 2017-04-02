package com.epsilon.world.content.skill.impl.fletching;

import com.epsilon.engine.task.Task;
import com.epsilon.engine.task.TaskManager;
import com.epsilon.model.Animation;
import com.epsilon.model.GameMode;
import com.epsilon.model.Skill;
import com.epsilon.model.definitions.ItemDefinition;
import com.epsilon.model.input.impl.EnterAmountOfGemsToBolt;
import com.epsilon.model.input.impl.EnterAmountOfGemsToCut;
import com.epsilon.world.content.Achievements;
import com.epsilon.world.content.Achievements.AchievementData;
import com.epsilon.world.entity.impl.player.Player;

public class BoltTips {
	enum GEM_DATA {
		
		JADE(1611, 9187, 8, 347, new Animation(886)),
		RED_TOPAZ(1613, 9188, 16, 790, new Animation(887)),
		SAPPHIRE(1607, 9189, 20, 1200, new Animation(888)),
		EMERALD(1605, 9190, 27, 1800, new Animation(889)),
		RUBY(1603, 9191, 34, 2600, new Animation(892)),
		DIAMOND(1601, 9192, 43, 3500, new Animation(886)),
		DRAGONSTONE(1615, 9193, 55, 4400, new Animation(885)),
		ONYX(6573, 9194, 71, 8513, new Animation(885));
		
		
		

		
		
		GEM_DATA(int cutGem, int boltTip, int levelReq, int xpReward, Animation animation) {
			this.cutGem = cutGem;
			this.boltTip = boltTip;
			this.levelReq = levelReq;
			this.xpReward = xpReward;
			this.animation = animation;
		}
		
		private int  cutGem, boltTip, levelReq, xpReward;
		private Animation animation;

		public int getBoltTip() {
			return boltTip;
		}

		public int getCutGem() {
			return cutGem;
		}

		public int getLevelReq() {
			return levelReq;
		}

		public int getXpReward() {
			return xpReward;
		}

		public Animation getAnimation() {
			return animation;
		}
		
		public static GEM_DATA forcutGem(int cutGem) {
			for(GEM_DATA data : GEM_DATA.values()) {
				if(data.getCutGem() == cutGem)
					return data;
			}
			return null;
		}
	}
	
	public static boolean selectionInterface(Player player, int gem) {
		player.getPacketSender().sendInterfaceRemoval();
		GEM_DATA data = GEM_DATA.forcutGem(gem);
		if(data == null)
			return false;
		if (player.getSkillManager().getMaxLevel(Skill.FLETCHING) < data.getLevelReq()) {
			player.getPacketSender().sendMessage("You need a Fletching level of atleast "+ data.getLevelReq() +" to craft this bolt tip.");
			return true;
		}
		player.setSelectedSkillingItem(gem);
		player.setInputHandling(new EnterAmountOfGemsToBolt());
		player.getPacketSender().sendString(2799, ItemDefinition.forId(gem).getName()).sendInterfaceModel(1746, gem, 150).sendChatboxInterface(4429);
		player.getPacketSender().sendString(2800, "How many would you like to craft?");
		return true;
	}

	public static void cutGem(final Player player, final int amount, final int cutGem) {
		player.getPacketSender().sendInterfaceRemoval();
		player.getSkillManager().stopSkilling();
		final GEM_DATA data = GEM_DATA.forcutGem(cutGem);
		if(data == null)
			return;
		player.setCurrentTask(new Task(2, player, true) {
			int amountCut = 0;
			@Override
			public void execute() {
				if(!player.getInventory().contains(cutGem)) {
					stop();
					return;
				}
				player.performAnimation(data.getAnimation());
				player.getInventory().delete(cutGem, 1);
				player.getInventory().add(data.getBoltTip(), 36);
			//	if(data == GEM_DATA.) {
					//Achievements.doProgress(player, AchievementData.MAKE_10000_BOLT_TIPS);
			//	} else if(data == GEM_DATA.ONYX) {
				//	Achievements.finishAchievement(player, AchievementData.CUT_AN_ONYX_STONE);
				//}
				if(player.getGameMode() == GameMode.EXTREME) {
					player.getSkillManager().addExperience(Skill.FLETCHING, (int) (data.getXpReward()*0.25));
				} else {
					player.getSkillManager().addExperience(Skill.FLETCHING, data.getXpReward());
				}
				
				amountCut++;
				if(amountCut >= amount)
					stop();
			}
		});
		TaskManager.submit(player.getCurrentTask());
	}
}
