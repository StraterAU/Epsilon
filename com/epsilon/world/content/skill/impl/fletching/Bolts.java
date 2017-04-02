package com.epsilon.world.content.skill.impl.fletching;

import com.epsilon.engine.task.Task;
import com.epsilon.engine.task.TaskManager;
import com.epsilon.model.Animation;
import com.epsilon.model.Skill;
import com.epsilon.model.definitions.ItemDefinition;
import com.epsilon.model.input.impl.EnterAmountOfGemsToBolt;
import com.epsilon.model.input.impl.EnterAmountOfTipsToBolt;
import com.epsilon.world.entity.impl.player.Player;

public class Bolts {
	public static int jade_BoltTip = 9187;
	public static int red_Topaz_BoltTip = 9188;
	public static int sapphire_BoltTip = 9189;
	public static int emerald_BoltTip = 9190;
	public static int ruby_BoltTip = 9191;
	public static int diamond_BoltTip = 9192;
	public static int dragonstone_BoltTip = 9193;
	public static int onyx_BoltTip = 9194;
	enum TIP_DATA {
		
		JADE(9187, 9139, 9335, 8, 1, new Animation(886)),
		RED_TOPAZ(9188, 9141, 9336, 15, 1, new Animation(887)),
		SAPPHIRE(9189, 9142, 9337, 20, 1, new Animation(888)),
		EMERALD(9190, 9142, 9338, 27, 1, new Animation(889)),
		RUBY(9191, 9143, 9339, 34, 1, new Animation(892)),
		DIAMOND(9192, 9143, 9340, 43, 1, new Animation(886)),
		DRAGONSTONE(9193, 9144, 9341, 55, 1, new Animation(885)),
		ONYX(9194, 9144, 9342, 71, 1, new Animation(885));
		
		
		

		
		
		TIP_DATA(int boltTip, int bolt,int boltFinal, int levelReq, int xpReward, Animation animation) {
			this.boltTip = boltTip;
			this.bolt = bolt;
			this.boltFinal = boltFinal;
			this.levelReq = levelReq;
			this.xpReward = xpReward;
			this.animation = animation;
		}
		
		private int  boltTip, bolt, boltFinal, levelReq, xpReward;
		public int getBolt() {
			return bolt;
		}

		public int getBoltFinal() {
			return boltFinal;
		}

		private Animation animation;

		public int getBoltTip() {
			return boltTip;
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
		
		public static TIP_DATA forboltTip(int boltTip) {
			for(TIP_DATA data : TIP_DATA.values()) {
				if(data.getBoltTip() == boltTip)
					return data;
			}
			return null;
		}
	}
	
	public static void selectionInterface(Player player, int boltTip) {
		player.getPacketSender().sendInterfaceRemoval();
		TIP_DATA data = TIP_DATA.forboltTip(boltTip);
		if(data == null)
			return;
		if (player.getSkillManager().getMaxLevel(Skill.FLETCHING) < data.getLevelReq()) {
			player.getPacketSender().sendMessage("You need a Fletching level of atleast "+ data.getLevelReq() +" to craft these bolts.");
			return;
		}
		player.setSelectedSkillingItem(boltTip);
		player.setInputHandling(new EnterAmountOfTipsToBolt());
		player.getPacketSender().sendString(2799, ItemDefinition.forId(boltTip).getName()).sendInterfaceModel(1746, boltTip, 150).sendChatboxInterface(4429);
		player.getPacketSender().sendString(2800, "How many would you like to make?");
	}

	public static void cutGem(final Player player, int amount, final int boltTip) {
		player.getPacketSender().sendInterfaceRemoval();
		player.getSkillManager().stopSkilling();
		final TIP_DATA data = TIP_DATA.forboltTip(boltTip);
		if(data == null)
			return;
				if(!player.getInventory().contains(boltTip) || !player.getInventory().contains(data.getBolt())) {
					player.getPacketSender().sendMessage("You need "+ItemDefinition.forId(data.getBolt()).getName()+" and "+ItemDefinition.forId(data.getBoltTip()).getName()+" to fletch "+ItemDefinition.forId(data.getBoltFinal()).getName()+"."  );
					return;
				}
				//player.performAnimation(data.getAnimation());
				//if(data.getBolt() < player.getInventory().getAmount(id))
					if(player.getInventory().getAmount(data.getBoltTip()) > player.getInventory().getAmount(data.getBolt())){
						amount = player.getInventory().getAmount(data.getBolt());
					}else if(player.getInventory().getAmount(data.getBoltTip()) < player.getInventory().getAmount(data.getBolt())){
						amount = player.getInventory().getAmount(data.getBoltTip());
					}
					player.getInventory().delete(boltTip, amount);
					player.getInventory().delete(data.getBolt(), amount);
					player.getInventory().add(data.getBoltFinal(), amount);
					
					//player.getPacketSender().sendMessage("AmountCut:"+amountCut+" Amount:"+amount);
				//if(data == GEM_DATA.DIAMOND) {
				//	Achievements.doProgress(player, AchievementData.CRAFT_1000_DIAMOND_GEMS);
				//} else if(data == GEM_DATA.ONYX) {
				//	Achievements.finishAchievement(player, AchievementData.CUT_AN_ONYX_STONE);
				//}
				//player.getSkillManager().addExperience(Skill.FLETCHING, data.getXpReward());
				//if(amountCut >= amount){
					//stop();
				//}
			
	}
}
