package com.epsilon.world.entity.impl.player;

import java.util.Date;

import com.epsilon.GameServer;
import com.epsilon.GameSettings;
import com.epsilon.engine.task.TaskManager;
import com.epsilon.engine.task.impl.BonusExperienceTask;
import com.epsilon.engine.task.impl.CombatSkullEffect;
import com.epsilon.engine.task.impl.FireImmunityTask;
import com.epsilon.engine.task.impl.OverloadPotionTask;
import com.epsilon.engine.task.impl.PlayerSkillsTask;
import com.epsilon.engine.task.impl.PlayerSpecialAmountTask;
import com.epsilon.engine.task.impl.PrayerRenewalPotionTask;
import com.epsilon.engine.task.impl.StaffOfLightSpecialAttackTask;
import com.epsilon.model.Flag;
import com.epsilon.model.Locations;
import com.epsilon.model.PlayerRights;
import com.epsilon.model.Skill;
import com.epsilon.model.container.impl.Bank;
import com.epsilon.model.container.impl.Equipment;
import com.epsilon.model.definitions.ItemDefinition;
import com.epsilon.model.definitions.WeaponAnimations;
import com.epsilon.model.definitions.WeaponInterfaces;
import com.epsilon.net.PlayerSession;
import com.epsilon.net.SessionState;
import com.epsilon.net.security.ConnectionHandler;
import com.epsilon.util.Misc;
import com.epsilon.world.World;
import com.epsilon.world.content.Achievements;
import com.epsilon.world.content.BonusManager;
import com.epsilon.world.content.Lottery;
import com.epsilon.world.content.NPCDropTableChecker;
import com.epsilon.world.content.PlayerLogs;
import com.epsilon.world.content.PlayerPanel;
import com.epsilon.world.content.PlayersOnlineInterface;
import com.epsilon.world.content.WellOfGoodwill;
import com.epsilon.world.content.clan.ClanChatManager;
import com.epsilon.world.content.combat.effect.CombatPoisonEffect;
import com.epsilon.world.content.combat.effect.CombatTeleblockEffect;
import com.epsilon.world.content.combat.magic.Autocasting;
import com.epsilon.world.content.combat.prayer.CurseHandler;
import com.epsilon.world.content.combat.prayer.PrayerHandler;
import com.epsilon.world.content.combat.pvp.BountyHunter;
import com.epsilon.world.content.combat.range.DwarfMultiCannon;
import com.epsilon.world.content.combat.weapon.CombatSpecial;
import com.epsilon.world.content.dialogue.DialogueManager;
import com.epsilon.world.content.grandexchange.GrandExchange;
import com.epsilon.world.content.minigames.impl.Barrows;
import com.epsilon.world.content.questtab.ServerInformation;
import com.epsilon.world.content.skill.SkillManager;
import com.epsilon.world.content.skill.impl.hunter.Hunter;
import com.epsilon.world.content.skill.impl.slayer.Slayer;

import mysql.impl.Hiscores;
import mysql.impl.UpdateForumRank;
import mysql.impl.playersonline.Administrator;
import mysql.impl.playersonline.Developer;
import mysql.impl.playersonline.Moderator;
import mysql.impl.playersonline.Owner;
import mysql.impl.playersonline.RegularPlayer;
import mysql.impl.playersonline.Support;
import mysql.impl.playersonline.TotalPlayers;

public class PlayerHandler {
	
	public static void handleLogin(Player player) {
		//Register the player
		Date loginTime = new Date();
		System.out.println("[World] Registering player - [username, host] : [" + player.getUsername() + ", " + player.getHostAddress() + " Log in time: " + loginTime.toString() + "]");
		ConnectionHandler.add(player.getHostAddress());
		World.getPlayers().add(player);
	//	World.updatePlayersOnline();
		PlayersOnlineInterface.add(player);
		player.getSession().setState(SessionState.LOGGED_IN);

		//Packets
		player.getPacketSender().sendMapRegion().sendDetails();

		player.getRecordedLogin().reset();


		//Tabs
		player.getPacketSender().sendTabs();

		//Setting up the player's item containers..
		for(int i = 0; i < player.getBanks().length; i++) {
			if(player.getBank(i) == null) {
				player.setBank(i, new Bank(player));
			}
		}

		//Weapons and equipment..
		WeaponAnimations.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
		WeaponInterfaces.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
		CombatSpecial.updateBar(player);
		BonusManager.update(player);

		//Skills
		player.getSummoning().login();
		player.getFarming().load();
		Slayer.checkDuoSlayer(player, true);
		for (Skill skill : Skill.values()) {
			player.getSkillManager().updateSkill(skill);
		}

		//Relations
		player.getRelations().setPrivateMessageId(1).onLogin(player).updateLists(true, true);

		//Client configurations
		player.getPacketSender().sendConfig(172, player.isAutoRetaliate() ? 1 : 0)
		.sendTotalXp(player.getSkillManager().getTotalGainedExp())
		.sendConfig(player.getFightType().getParentId(), player.getFightType().getChildId())
		.sendRunStatus()
		.sendRunEnergy(player.getRunEnergy())
		.sendString(8135, ""+player.getMoneyInPouch())
		.sendInteractionOption("Follow", 3, false)
		.sendInteractionOption("Trade With", 4, false)
		.sendInterfaceRemoval().sendString(39161, "@or2@Server time: @or2@[ @yel@"+Misc.getCurrentServerTime()+"@or2@ ]");

		player.getInventory().refreshItems();
		player.getEquipment().refreshItems();
		
		Autocasting.onLogin(player);
		PrayerHandler.deactivateAll(player);
		CurseHandler.deactivateAll(player);
		BonusManager.sendCurseBonuses(player);
		Achievements.updateInterface(player);
		Barrows.updateInterface(player);


		//Tasks
		TaskManager.submit(new PlayerSkillsTask(player));
		if (player.isPoisoned()) {
			TaskManager.submit(new CombatPoisonEffect(player));
		}
		if(player.getPrayerRenewalPotionTimer() > 0) {
			TaskManager.submit(new PrayerRenewalPotionTask(player));
		}
		if(player.getOverloadPotionTimer() > 0) {
			TaskManager.submit(new OverloadPotionTask(player));
		}
		if (player.getTeleblockTimer() > 0) {
			TaskManager.submit(new CombatTeleblockEffect(player));
		}
		if (player.getSkullTimer() > 0) {
			player.setSkullIcon(1);
			TaskManager.submit(new CombatSkullEffect(player));
		}
		if(player.getFireImmunity() > 0) {
			FireImmunityTask.makeImmune(player, player.getFireImmunity(), player.getFireDamageModifier());
		}
		if(player.getSpecialPercentage() < 100) {
			TaskManager.submit(new PlayerSpecialAmountTask(player));
		}
		if(player.hasStaffOfLightEffect()) {
			TaskManager.submit(new StaffOfLightSpecialAttackTask(player));
		}
		if(player.getMinutesBonusExp() >= 0) {
			TaskManager.submit(new BonusExperienceTask(player));
		}

		//Update appearance
		player.getUpdateFlag().flag(Flag.APPEARANCE);

		//Others```
		Lottery.onLogin(player);
		Locations.login(player);
		player.getPacketSender().sendMessage("Welcome to @red@Epsilon</col>.");
		player.getPacketSender().sendMessage("[@red@Epsilon</col>]: Check our latest updates by doing ::updates.");
		if(GameSettings.DOUBLE_LOOT) {
			player.getPacketSender().sendMessage("[@red@Event</col>]: Drops rates are boosted by 2x for monday!");
		}
		
		if(GameSettings.BONUS_EXP) {
			player.getPacketSender().sendMessage("[@red@Event</col>]: Double experience is currently active for the weekend!");
		}
		
		if(GameSettings.DOUBLE_PC_POINTS) {
			player.getPacketSender().sendMessage("[@red@Event</col>]: Double pest control points is currently active for tuesday!");
		}
		
		if(GameSettings.SKILLING_DAY) {
			player.getPacketSender().sendMessage("[@red@Event</col>]: Double loot through skilling is currently active for wednesday!");
		}
		
		if(GameSettings.VOTING_DAY) {
			player.getPacketSender().sendMessage("[@red@Event</col>]: Double vote points is currently active for thursday!");
		}
		
		if(GameSettings.DOUBLE_SLAYER_POINTS) {
			player.getPacketSender().sendMessage("[@red@Event</col>]: Double slayer points is currently active for friday!");
		}
		
		ClanChatManager.handleLogin(player);
		//New player
		if(player.newPlayer()) {
			player.setPlayerLocked(true).setDialogueActionId(45);
			ClanChatManager.join(player, "Divine");
			DialogueManager.start(player, 81);
			Date date = new Date();
			player.getPointsHandler().setDateJoined(date.toString());
			World.sendMessage("[@red@New Player</col>] "+player.getUsername()+" is new to the server, please give him a warm welcome!");
		}

		player.getPacketSender().updateSpecialAttackOrb().sendIronmanMode(player.getGameMode().ordinal());

		if(player.hasRights(PlayerRights.SUPPORT))
			World.sendMessage("<img=10> [@red@Support</col>] "+player.getUsername()+" has logged in.");
		if(player.hasRights(PlayerRights.MODERATOR))
			World.sendMessage("<img=1> [@red@Moderator</col>] "+player.getUsername()+" has logged in.");
		if(player.hasRights(PlayerRights.ADMINISTRATOR))
			World.sendMessage("<img=2> [@red@Administrator</col>] "+player.getUsername()+" has logged in.");
		if(player.hasRights(PlayerRights.OWNER))
			World.sendMessage("<img=3> [@red@Owner</col>] "+player.getUsername()+" has logged in.");
		
		GrandExchange.onLogin(player);
		
		NPCDropTableChecker.getSingleton().refreshDropTableChilds(player);
		
		if(player.getPointsHandler().getAchievementPoints() == 0) {
			Achievements.setPoints(player);
		}
		
		if(Bank.getTabForItem2(player, 11949) == -1 && !player.getInventory().contains(11949)) {
			player.getBank(0).add(11949, 1);
			player.getPacketSender().sendMessage("A snow globe has been added to your bank.");
		}
		
		if (player.getHCILives() == 3) {
			player.getAppearance().setHciIcon(3);
			player.getAppearance().setBountyHunterSkull(-1);
		} else if (player.getHCILives() == 2) {
			player.getAppearance().setHciIcon(2);
			player.getAppearance().setBountyHunterSkull(-1);
		} else if (player.getHCILives() == 1) {
			player.getAppearance().setHciIcon(1);
			player.getAppearance().setBountyHunterSkull(-1);
		}
		
		boolean addedCapes = false;
		
		for(int i = 0; i < SkillManager.MASTER_SKILLCAPES.length; i++) {
			if(SkillManager.MASTER_SKILLCAPES[i] != -1 && player.getSkillManager().getExperience(Skill.values()[i]) >= SkillManager.MAX_EXPERIENCE) {
				int itemId = SkillManager.MASTER_SKILLCAPES[i];
				if(Bank.getTabForItem2(player, itemId) == -1 && !player.getInventory().contains(itemId) && !player.getEquipment().contains(itemId)) {
					player.getBank(0).add(itemId, 1);
					addedCapes = true;
				}
			}
		}
		
		if(addedCapes) {
			player.getPacketSender().sendMessage("Your unclaimed master skillcapes have been added to your bank.");
		}
		
		if(Bank.getTabForItem2(player, 11949) == -1 && !player.getInventory().contains(11949)) {
			player.getBank(0).add(11949, 1);
			player.getPacketSender().sendMessage("A snow globe has been added to your bank.");
		}
		
		PlayerLogs.log(player.getUsername(), "Login from host "+player.getHostAddress()+", serial number: "+player.getSerialNumber());
		player.getPacketSender().sendConfig(540, 1);
		Owner.login(player);
		Administrator.login(player);
		Moderator.login(player);
		RegularPlayer.login(player);
		Developer.login(player);
		Support.login(player);
		//TotalPlayers.update();
		ServerInformation.display(player);
	}
	

	public static boolean handleLogout(Player player) {
		
		synchronized(player) {
		
			try {
	
				PlayerSession session = player.getSession();
				
				if(session.getChannel().isOpen()) {
					player.save();
					session.getChannel().close();
				}
	
				if(!player.isRegistered()) {
					return true;
				}
	
				boolean exception = GameServer.isUpdating() || World.getLogoutQueue().contains(player) && player.getLogoutTimer().elapsed(90000);
				if(player.logout() || exception) {
					Date logoutTime = new Date();
					System.out.println("[World] Deregistering player - [username, host] : [" + player.getUsername() + ", " + player.getHostAddress() + " Log out time: " + logoutTime.toString() + "]");
					player.getSession().setState(SessionState.LOGGING_OUT);
					ConnectionHandler.remove(player.getHostAddress());
					player.setTotalPlayTime(player.getTotalPlayTime() + player.getRecordedLogin().elapsed());
					player.getPacketSender().sendInterfaceRemoval();
					if(player.getCannon() != null) {
						DwarfMultiCannon.pickupCannon(player, player.getCannon(), true);
					}
					if(exception && player.getResetPosition() != null) {
						player.moveTo(player.getResetPosition());
						player.setResetPosition(null);
					}
					if(player.getRegionInstance() != null) {
						player.getRegionInstance().destruct();
					}
					Hunter.handleLogout(player);
					Locations.logout(player);
					player.getSummoning().unsummon(false, false);
					player.getFarming().save();
					Hiscores.save(player);
					BountyHunter.handleLogout(player);
					ClanChatManager.leave(player, false);
					player.getRelations().updateLists(false);
					PlayersOnlineInterface.remove(player);
					TaskManager.cancelTasks(player.getCombatBuilder());
					TaskManager.cancelTasks(player);
					player.save();
					World.getPlayers().remove(player);
					session.setState(SessionState.LOGGED_OUT);
					World.updatePlayersOnline();
					/*Owner.logout(player);
					Administrator.logout(player);
					Moderator.logout(player);
					RegularPlayer.logout(player);
					Developer.logout(player);
					Support.logout(player);*/
					if (player.isNeedsForumRankUpdate() && !player.getHighestRights().isStaff()) {
						UpdateForumRank.update(player);
						System.out.println("updated - " + player.getUsername());
					}
					//TotalPlayers.update();
					return true;
				} else {
					player.save();
					return false;
				}
			} catch(Exception e) {
				
				e.printStackTrace();
			}
		}
		return true;
	}
}
