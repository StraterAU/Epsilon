package com.epsilon.world.content.skill.impl.firemaking;

import com.epsilon.engine.task.Task;
import com.epsilon.engine.task.TaskManager;
import com.epsilon.model.Animation;
import com.epsilon.model.GameMode;
import com.epsilon.model.GameObject;
import com.epsilon.model.Skill;
import com.epsilon.model.container.impl.Equipment;
import com.epsilon.model.movement.MovementQueue;
import com.epsilon.util.Misc;
import com.epsilon.world.content.Achievements;
import com.epsilon.world.content.CustomObjects;
import com.epsilon.world.content.Sounds;
import com.epsilon.world.content.Achievements.AchievementData;
import com.epsilon.world.content.Sounds.Sound;
import com.epsilon.world.content.skill.impl.dungeoneering.Dungeoneering;
import com.epsilon.world.entity.impl.player.Player;

/**
 * The Firemaking skill
 * @author Gabriel Hannason
 */

public class Firemaking {
	
	public static final int[] ITEMS = { 13659, 13660 };

	public static void lightFire(final Player player, int log, final boolean addingToFire, final int amount) {
		if (!player.getClickDelay().elapsed(2000) || player.getMovementQueue().isLockMovement())
			return;
		if(!player.getLocation().isFiremakingAllowed()) {
			player.getPacketSender().sendMessage("You can not light a fire in this area.");
			return;
		}
		boolean objectExists = CustomObjects.objectExists(player.getPosition().copy());
		if(!Dungeoneering.doingDungeoneering(player)) {
			if(objectExists && !addingToFire || player.getPosition().getZ() > 0 || !player.getMovementQueue().canWalk(1, 0) && !player.getMovementQueue().canWalk(-1, 0) && !player.getMovementQueue().canWalk(0, 1) && !player.getMovementQueue().canWalk(0, -1)) {
				player.getPacketSender().sendMessage("You can not light a fire here.");
				return;
			}
		}
		final Logdata.logData logData = Logdata.getLogData(player, log);
		if(logData == null)
			return;
		player.getMovementQueue().reset();
		if(objectExists && addingToFire)
			MovementQueue.stepAway(player);
		player.getPacketSender().sendInterfaceRemoval();
		player.setEntityInteraction(null);
		player.getSkillManager().stopSkilling();
		int cycle = 2 + Misc.getRandom(3);
		if (player.getSkillManager().getMaxLevel(Skill.FIREMAKING) < logData.getLevel()) {
			player.getPacketSender().sendMessage("You need a Firemaking level of atleast "+logData.getLevel()+" to light this.");
			return;
		}
		if(!addingToFire) {
			if(player.getEquipment().getItems()[Equipment.HANDS_SLOT].getId() == 13660 && player.getEquipment().getItems()[Equipment.RING_SLOT].getId() == 13659 && Misc.getRandom(100) <= 10) {//here
				player.getPacketSender().sendMessage("The effects from your Flame Gloves allowed you to burn 3 more logs.");
				player.getInventory().delete(logData.getLogId(), 3);
				if(player.getGameMode() == GameMode.EXTREME) {
					player.getSkillManager().addExperience(Skill.FIREMAKING, (int) ((logData.getXp() * 3)*0.25));
				} else {
					player.getSkillManager().addExperience(Skill.FIREMAKING, logData.getXp() * 3);
				}
				
			}
				
			player.getPacketSender().sendMessage("You attempt to light a fire..");
			player.performAnimation(new Animation(733));
			player.getMovementQueue().setLockMovement(true);
			if (Misc.getRandom(100) <= 3) {
			player.getInventory().add(13660, 1);
			player.getPacketSender().sendMessage("You Recieved Flame Gloves!");
			}
			if (Misc.getRandom(100) <= 3) {
			player.getInventory().add(13659, 1);
			player.getPacketSender().sendMessage("You Recieved a Ring of fire!");
			}
			int random = Misc.getRandom(100);
			if(random >= 1 && random <= 3) {
				Misc.giveReward(player, ITEMS);
			}
		}
		player.setCurrentTask(new Task(addingToFire ? 2 : cycle, player, addingToFire ? true : false) {
			int added = 0;
			@Override
			public void execute() {
				player.getPacketSender().sendInterfaceRemoval();
				if(addingToFire && player.getInteractingObject() == null) { //fire has died
					player.getSkillManager().stopSkilling();
					player.getPacketSender().sendMessage("The fire has died out.");
					return;
				}
				player.getInventory().delete(logData.getLogId(), 1);
				if(addingToFire) {
					player.performAnimation(new Animation(827));
					player.getPacketSender().sendMessage("You add some logs to the fire..");
				} else {
					if(!player.getMovementQueue().isMoving()) {
						player.getMovementQueue().setLockMovement(false);
						player.performAnimation(new Animation(65535));
						MovementQueue.stepAway(player);
					}
					CustomObjects.globalFiremakingTask(new GameObject(2732, player.getPosition().copy()), player, logData.getBurnTime());
					player.getPacketSender().sendMessage("The fire catches and the logs begin to burn.");
					stop();
				}
				if(logData == Logdata.logData.OAK) {
					Achievements.finishAchievement(player, AchievementData.BURN_AN_OAK_LOG);
				} else if(logData == Logdata.logData.MAGIC) {
					Achievements.doProgress(player, AchievementData.BURN_100_MAGIC_LOGS);
					Achievements.doProgress(player, AchievementData.BURN_2500_MAGIC_LOGS);
				}
				int random = Misc.getRandom(100);
				if(random >= 1 && random <= 3) {
					Misc.giveReward(player, ITEMS);
				}
				Sounds.sendSound(player, Sound.LIGHT_FIRE);
				
				if(player.getGameMode() == GameMode.EXTREME) {
					player.getSkillManager().addExperience(Skill.FIREMAKING, (int) (logData.getXp()*0.25));
				} else {
					player.getSkillManager().addExperience(Skill.FIREMAKING, logData.getXp());
				}
				added++;
				if(added >= amount || !player.getInventory().contains(logData.getLogId())) {
					stop();
					if(added < amount && addingToFire && Logdata.getLogData(player, -1) != null && Logdata.getLogData(player, -1).getLogId() != log) {
						player.getClickDelay().reset(0);
						Firemaking.lightFire(player, -1, true, (amount-added));
					}
					return;
				}
			}

			@Override
			public void stop() {
				setEventRunning(false);
				player.performAnimation(new Animation(65535));
				player.getMovementQueue().setLockMovement(false);
			}
		});
		TaskManager.submit(player.getCurrentTask());
		player.getClickDelay().reset(System.currentTimeMillis() + 500);
	}

}