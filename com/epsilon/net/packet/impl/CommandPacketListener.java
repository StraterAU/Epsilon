package com.epsilon.net.packet.impl;

import mysql.MySQLController;

import java.util.Arrays;

import com.epsilon.GameServer;
import com.epsilon.GameSettings;
import com.epsilon.engine.task.Task;
import com.epsilon.engine.task.TaskManager;
import com.epsilon.model.Animation;
import com.epsilon.model.Direction;
import com.epsilon.model.Flag;
import com.epsilon.model.GameMode;
import com.epsilon.model.GameObject;
import com.epsilon.model.Graphic;
import com.epsilon.model.GroundItem;
import com.epsilon.model.Item;
import com.epsilon.model.PlayerRights;
import com.epsilon.model.Position;
import com.epsilon.model.Skill;
import com.epsilon.model.Locations.Location;
import com.epsilon.model.container.impl.Bank;
import com.epsilon.model.container.impl.Equipment;
import com.epsilon.model.container.impl.Shop.ShopManager;
import com.epsilon.model.definitions.ItemDefinition;
import com.epsilon.model.definitions.WeaponAnimations;
import com.epsilon.model.definitions.WeaponInterfaces;
import com.epsilon.model.input.impl.ItemSearch;
import com.epsilon.model.input.impl.YellTitle;
import com.epsilon.model.movement.MovementQueue;
import com.epsilon.net.packet.Packet;
import com.epsilon.net.packet.PacketListener;
import com.epsilon.net.security.ConnectionHandler;
import com.epsilon.util.Misc;
import com.epsilon.world.World;
import com.epsilon.world.content.BonusManager;
import com.epsilon.world.content.ChristmasEvent;
import com.epsilon.world.content.Lottery;
import com.epsilon.world.content.PlayerLogs;
import com.epsilon.world.content.PlayerPanel;
import com.epsilon.world.content.PlayerPunishment;
import com.epsilon.world.content.PlayersOnlineInterface;
import com.epsilon.world.content.ShootingStar;
import com.epsilon.world.content.Trivia;
import com.epsilon.world.content.WellOfGoodwill;
import com.epsilon.world.content.PlayerPunishment.Jail;
import com.epsilon.world.content.Trivia.Questions;
import com.epsilon.world.content.Trivia.ResponseState;
import com.epsilon.world.content.clan.ClanChatManager;
import com.epsilon.world.content.combat.CombatFactory;
import com.epsilon.world.content.combat.DesolaceFormulas;
import com.epsilon.world.content.combat.weapon.CombatSpecial;
import com.epsilon.world.content.dialogue.DialogueManager;
import com.epsilon.world.content.grandexchange.GrandExchange;
import com.epsilon.world.content.grandexchange.GrandExchangeOffer;
import com.epsilon.world.content.grandexchange.GrandExchangeOffers;
import com.epsilon.world.content.minigames.impl.WarriorsGuild;
import com.epsilon.world.content.random.impl.SandwichLady;
import com.epsilon.world.content.skill.SkillManager;
import com.epsilon.world.content.skill.impl.construction.Construction;
import com.epsilon.world.content.skill.impl.runecrafting.Runecrafting;
import com.epsilon.world.content.skill.impl.slayer.SlayerTasks;
import com.epsilon.world.content.transportation.TeleportHandler;
import com.epsilon.world.content.transportation.TeleportType;
import com.epsilon.world.entity.impl.GroundItemManager;
import com.epsilon.world.entity.impl.npc.NPC;
import com.epsilon.world.entity.impl.player.Player;
import com.epsilon.world.entity.impl.player.PlayerSaving;


/**
 * This packet listener manages commands a player uses by using the
 * command console prompted by using the "`" char.
 * 
 * @author Adam.trinity1    <-- skype
 */

public class CommandPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		String command = Misc.readString(packet.getBuffer());
		String[] parts = command.toLowerCase().split(" ");
		if(command.contains("\r") || command.contains("\n")) {
			return;
		}
		try {
			switch (player.getHighestRights()) {
			case PLAYER:
				playerCommands(player, parts, command);
				break;
			case MODERATOR:
				playerCommands(player, parts, command);
				memberCommands(player, parts, command);
				helperCommands(player, parts, command);
				moderatorCommands(player, parts, command);
				break;
			case ADMINISTRATOR:
				playerCommands(player, parts, command);
				memberCommands(player, parts, command);
				helperCommands(player, parts, command);
				moderatorCommands(player, parts, command);
				administratorCommands(player, parts, command);
				break;
			case OWNER:
				playerCommands(player, parts, command);
				memberCommands(player, parts, command);
				helperCommands(player, parts, command);
				moderatorCommands(player, parts, command);
				administratorCommands(player, parts, command);
				ownerCommands(player, parts, command);
				developerCommands(player, parts, command);
				break;
			case DEVELOPER:
				playerCommands(player, parts, command);
				memberCommands(player, parts, command);
				helperCommands(player, parts, command);
				moderatorCommands(player, parts, command);
				administratorCommands(player, parts, command);
				ownerCommands(player, parts, command);
				developerCommands(player, parts, command);
				break;
			case SUPPORT:
				playerCommands(player, parts, command);
				memberCommands(player, parts, command);
				helperCommands(player, parts, command);
				break;
			case VETERAN:
				playerCommands(player, parts, command);
				memberCommands(player, parts, command);
				break;
			case DONATOR:
			case SUPER_DONATOR:
			case EXTREME_DONATOR:
			case LEGENDARY_DONATOR:
			case UBER_DONATOR:
				playerCommands(player, parts, command);
				memberCommands(player, parts, command);
				break;
			default:
				break;
			}
		} catch (Exception exception) {
			//exception.printStackTrace();

			if(player.hasRights(PlayerRights.DEVELOPER)) {
				player.getPacketSender().sendConsoleMessage("Error executing that command.");
			} else {
				player.getPacketSender().sendMessage("Error executing that command.");
			}

		}
	}

	private static void playerCommands(final Player player, String[] command, String wholeCommand)  {
		
		if(command[0].equalsIgnoreCase("mb")) {
			TeleportHandler.teleportPlayer(player, new Position(2539, 4716), TeleportType.NORMAL);
		}
		
		//System.out.println("Command: " + wholeCommand + ", " + Arrays.toString(command)); 
		if(command[0].equalsIgnoreCase("answer")) {
			
			String answer = command[1];
			//System.out.println("Answer command: " + answer + " -> " + Trivia.answer(player, answer));
			answer = Misc.formatText(answer.replaceAll("_", " "));
			
			ResponseState state = Trivia.answer(player, answer);
			
			switch(state) {
			case CORRECT:
				World.sendMessage("<img=9> <col=0066FF><shad=222222>[Trivia Bot]" + player.getUsername() + " has answered the trivia question, and now has " + player.getPointsHandler().getTriviaPoints() + " trivia points.");
				player.getPacketSender().sendMessage("You answered the question correctly and have been awarded one Trivia point.");
				player.getPacketSender().sendMessage("You now have " + player.getPointsHandler().getTriviaPoints() + " Trivia points.");
				PlayerPanel.refreshPanel(player);
				break;
			case INCORRECT:
				player.getPacketSender().sendMessage("The answer was not correct.");
				break;
			case ALREADY_ANSWERED:
				player.getPacketSender().sendMessage("This question has already been answered.");
				break;
				
			}
			
		}
		if (wholeCommand.equalsIgnoreCase("commands")) { // change name to whatever.
			player.getPacketSender().sendString(23126, "Epsilon Commands");
			int id = 23127;
			player.getPacketSender().sendString(id++, "::players");
			player.getPacketSender().sendString(id++, "::yell");
			player.getPacketSender().sendString(id++, "::home");
			player.getPacketSender().sendString(id++, "::help");
			player.getPacketSender().sendString(id++, "::empty");
			player.getPacketSender().sendString(id++, "::store");
			player.getPacketSender().sendString(id++, "::forums");
			player.getPacketSender().sendString(id++, "::vote");
			player.getPacketSender().sendString(id++, "::mz");
			player.getPacketSender().sendString(id++, "::donate");
			player.getPacketSender().sendString(id++, "::train");
			player.getPacketSender().sendString(id++, "Blue Torva");
			player.getPacketSender().sendString(id++, "Sky Torva");
			player.getPacketSender().sendString(id++, "White Knight");
			player.getPacketSender().sendString(id++, "Dark Torva");
			player.getPacketSender().sendString(id++, "Mbox");
			player.getPacketSender().sendString(id++, "Youtuber Zone");
			player.getPacketSender().sendString(id++, "Ownercape");
			player.getPacketSender().sendString(id++, "@cya@--------------------------------@lre@ ~ More Commands coming soon! ~@cya@ -----------------------------------------------");
			for (int i = id; i < 23225; i++) 
				player.getPacketSender().sendString(i, "");
			player.getPacketSender().sendInterface(23121);	
		}
		if(wholeCommand.equalsIgnoreCase("claim")) {
			DialogueManager.start(player, 146);
		}
		if (wholeCommand.equalsIgnoreCase("donate") || wholeCommand.equalsIgnoreCase("store")) {
			player.getPacketSender().sendString(1, "http://epsilonps.org/store/");
			player.getPacketSender().sendMessage("Attempting to open: www.epsilonps.org/store/");
		}
		if(wholeCommand.equalsIgnoreCase("helpatbarrows")) {
			player.getPacketSender().sendMessage("Your barrows kill count has been reseted.");
			player.getMinigameAttributes().getBarrowsMinigameAttributes().setKillcount(0);
		}
		if(command[0].equalsIgnoreCase("attacks")) {
			int attack = DesolaceFormulas.getMeleeAttack(player);
			int range = DesolaceFormulas.getRangedAttack(player);
			int magic = DesolaceFormulas.getMagicAttack(player);
			player.getPacketSender().sendMessage("@bla@Melee attack: @or2@"+attack+"@bla@, ranged attack: @or2@"+range+"@bla@, magic attack: @or2@"+magic);
                }
                if (command[0].equals("home")) {
			TeleportHandler.teleportPlayer(player, new Position(3093, 3503), TeleportType.NORMAL);
		}
		if (command[0].equals("save")) {
			player.save();
			player.getPacketSender().sendMessage("Your progress has been saved.");
		}
		if (command[0].equals("forums")) {
			player.getPacketSender().sendString(1, "www.epsilonps.org/community");
			player.getPacketSender().sendMessage("Attempting to open: www.epsilonps.org/community/");
		}
		if (command[0].equals("vote")) {
			player.getPacketSender().sendString(1, "www.epsilonps.org/vote/?user="+player.getUsername());
			player.getPacketSender().sendMessage("Attempting to open: www.epsilonps.org/vote/");
		}
		if(command[0].equals("help")) {
			if(player.getLastYell().elapsed(30000)) {
				World.sendStaffMessage("<col=FF0066><img=10> [TICKET SYSTEM]<col=6600FF> "+player.getUsername()+" has requested help. Please help them!");
				player.getLastYell().reset();
				player.getPacketSender().sendMessage("<col=663300>Your help request has been received. Please be patient.");
			} else {
				player.getPacketSender().sendMessage("").sendMessage("<col=663300>You need to wait 30 seconds before using this again.").sendMessage("<col=663300>If it's an emergency, please private message a staff member directly instead.");
			}
		}
		if(command[0].equals("empty")) {
			player.getPacketSender().sendInterfaceRemoval().sendMessage("You clear your inventory.");
			player.getSkillManager().stopSkilling();
			player.getInventory().resetItems().refreshItems();
		}
		if(command[0].equals("players")) {
			player.getPacketSender().sendInterfaceRemoval();
			PlayersOnlineInterface.showInterface(player);
		}
		if(command[0].equalsIgnoreCase("[cn]")) {
			if(player.getInterfaceId() == 40172) {
				ClanChatManager.setName(player, wholeCommand.substring(wholeCommand.indexOf(command[1])));
			}
		}
	}

	private static void memberCommands(final Player player, String[] command, String wholeCommand) {
		if(command[0].equalsIgnoreCase("title")) {
			player.getPacketSender().sendInterfaceRemoval();
			player.setInputHandling(new YellTitle());
			player.getPacketSender().sendEnterInputPrompt("Enter the yell title you would like to use...");
		}
		if(wholeCommand.toLowerCase().startsWith("yell")) {
			if(PlayerPunishment.muted(player.getUsername()) || PlayerPunishment.IPMuted(player.getHostAddress())) {
				player.getPacketSender().sendMessage("You are muted and cannot yell.");
				return;
			}
			int delay = player.getHighestRights().getYellDelay();
			if(!player.getLastYell().elapsed((delay*1000))) {
				player.getPacketSender().sendMessage("You must wait at least "+delay+" seconds between every yell-message you send.");
				return;
			}
			String yellMessage = wholeCommand.substring(4, wholeCommand.length());
			String yellTitle = "";
			if(player.getYellTitle() != null) {
				yellTitle = player.getYellTitle();
			} else {
				yellTitle = Misc.formatPlayerName(player.getHighestRights().name());
			}
			if (player.getGameMode() == GameMode.IRONMAN || player.getGameMode() == GameMode.HARDCORE_IRONMAN) {
				World.sendMessage("<img="+player.getHighestRights().ordinal()+"> <img=33>[" + player.getHighestRights().getYellPrefix() + Misc.formatText(yellTitle) +"</col>] "+player.getUsername()+":"+Misc.capitalFirst(yellMessage));
			} else {
				World.sendMessage("<img="+player.getHighestRights().ordinal()+"> [" + player.getHighestRights().getYellPrefix() + Misc.formatText(yellTitle) +"</col>] "+player.getUsername()+":"+Misc.capitalFirst(yellMessage));
			}
			player.getLastYell().reset(); 	
			}
		if (command[0].equals("mz")) {
			if(player.getCombatBuilder().isAttacking() || player.getCombatBuilder().isBeingAttacked()){
				player.getPacketSender().sendMessage("You can't do that on combat.");
				return;
			}
			TeleportHandler.teleportPlayer(player, new Position(3423, 2914), TeleportType.NORMAL);
		}
		if (command[0].equals("bank")) {
			if(player.getLocation() != Location.WILDERNESS && player.getLocation() != Location.FIST_OF_GUTHIX && player.getLocation() != Location.FIST_OF_GUTHIX_WAITING_LOBBY){
			player.getBank(player.getCurrentBankTab()).open();
			}else{
				player.getPacketSender().sendMessage("You cannot use this command in wilderness");
			}
		}
	}

	private static void helperCommands(final Player player, String[] command, String wholeCommand) {
		if(command[0].equalsIgnoreCase("staff")) {
			int delay = player.getHighestRights().getYellDelay();
			if(!player.getLastYell().elapsed((delay*1000))) {
				player.getPacketSender().sendMessage("You must wait at least "+delay+" seconds between every yell-message you send.");
				return;
			}
			String yellMessage = wholeCommand.substring(5, wholeCommand.length());
			for(Player other : World.getPlayers()) {
				if(other != null && other.getHighestRights().isStaff()) {
					other.getPacketSender().sendMessage("<col=FF0000><shad=000000>[Staff Chat] <img="+player.getHighestRights().ordinal()+">"+player.getUsername()+":"+yellMessage);
				}
			}
			player.getLastYell().reset();
		}
		if(command[0].equalsIgnoreCase("jail")) {
			Player player2 = World.getPlayerByName(wholeCommand.substring(5));
			if (player2 != null) {
				if(Jail.isJailed(player2)) {
					player.getPacketSender().sendConsoleMessage("That player is already jailed!");
					return;
				}
				if(Jail.jailPlayer(player2)) {
					player2.getSkillManager().stopSkilling();
					PlayerLogs.log(player.getUsername(), ""+player.getUsername()+" just jailed "+player2.getUsername()+"!");
					player.getPacketSender().sendMessage("Jailed player: "+player2.getUsername()+"");
					player2.getPacketSender().sendMessage("You have been jailed by "+player.getUsername()+".");
				} else {
					player.getPacketSender().sendConsoleMessage("Jail is currently full.");
				}
			} else {
				player.getPacketSender().sendConsoleMessage("Could not find that player online.");
			}
		}
		if(command[0].equals("remindvote")) {
			World.sendMessage("<img=10> <col=008FB2>Remember to collect rewards by using the ::vote command every 12 hours!");
		}
		if(command[0].equalsIgnoreCase("unjail")) {
			Player player2 = World.getPlayerByName(wholeCommand.substring(7));
			if (player2 != null) {
				Jail.unjail(player2);
				PlayerLogs.log(player.getUsername(), ""+player.getUsername()+" just unjailed "+player2.getUsername()+"!");
				player.getPacketSender().sendMessage("Unjailed player: "+player2.getUsername()+"");
				player2.getPacketSender().sendMessage("You have been unjailed by "+player.getUsername()+".");
			} else {
				player.getPacketSender().sendConsoleMessage("Could not find that player online.");
			}
		}
		if (command[0].equals("staffzone")) {
			if (command.length > 1 && command[1].equals("all")) {
				for (Player players : World.getPlayers()) {
					if (players != null) {
						if (players.getHighestRights().isStaff()) {
							TeleportHandler.teleportPlayer(players, new Position(2846, 5147), TeleportType.NORMAL);
						}
					}
				}
			} else {
				TeleportHandler.teleportPlayer(player, new Position(2846, 5147), TeleportType.NORMAL);
			}
		}
		if(command[0].equalsIgnoreCase("saveall")) {
			World.savePlayers();
			player.getPacketSender().sendMessage("Saved players!");
		}
		if(command[0].equalsIgnoreCase("teleto")) {
			String playerToTele = wholeCommand.substring(7);
			Player player2 = World.getPlayerByName(playerToTele);
			if(player2 == null) {
				player.getPacketSender().sendConsoleMessage("Cannot find that player online..");
				return;
			} else {
				boolean canTele = TeleportHandler.checkReqs(player, player2.getPosition().copy()) && player.getRegionInstance() == null && player2.getRegionInstance() == null;
				if(canTele) {
					TeleportHandler.teleportPlayer(player, player2.getPosition().copy(), TeleportType.NORMAL);
					player.getPacketSender().sendConsoleMessage("Teleporting to player: "+player2.getUsername()+"");
				} else
					player.getPacketSender().sendConsoleMessage("You can not teleport to this player at the moment. Minigame maybe?");
			}
		}
		if(command[0].equalsIgnoreCase("movehome")) {
			String player2 = command[1];
			player2 = Misc.formatText(player2.replaceAll("_", " "));
			if(command.length >= 3 && command[2] != null)
				player2 += " "+Misc.formatText(command[2].replaceAll("_", " "));
			Player playerToMove = World.getPlayerByName(player2);
			if(playerToMove != null) {
				playerToMove.moveTo(GameSettings.DEFAULT_POSITION.copy());
				playerToMove.getPacketSender().sendMessage("You've been teleported home by "+player.getUsername()+".");
				player.getPacketSender().sendConsoleMessage("Sucessfully moved "+playerToMove.getUsername()+" to home.");
			} 
		}
		if(command[0].equalsIgnoreCase("mute")) {
			String player2 = Misc.formatText(wholeCommand.substring(5));
			if(!PlayerSaving.playerExists(player2)) {
				player.getPacketSender().sendConsoleMessage("Player "+player2+" does not exist.");
				return;
			} else {
				if(PlayerPunishment.muted(player2)) {
					player.getPacketSender().sendConsoleMessage("Player "+player2+" already has an active mute.");
					return;
				}
				PlayerLogs.log(player.getUsername(), ""+player.getUsername()+" just muted "+player2+"!");
				PlayerPunishment.mute(player2);
				player.getPacketSender().sendConsoleMessage("Player "+player2+" was successfully muted. Command logs written.");
				Player plr = World.getPlayerByName(player2);
				if(plr != null) {
					plr.getPacketSender().sendMessage("You have been muted by "+player.getUsername()+".");
				}
			}
		}
	}

	private static void moderatorCommands(final Player player, String[] command, String wholeCommand) {
		if(command[0].equalsIgnoreCase("unmute")) {
			String player2 = wholeCommand.substring(7);
			if(!PlayerSaving.playerExists(player2)) {
				player.getPacketSender().sendConsoleMessage("Player "+player2+" does not exist.");
				return;
			} else {
				if(!PlayerPunishment.muted(player2)) {
					player.getPacketSender().sendConsoleMessage("Player "+player2+" is not muted!");
					return;
				}
				PlayerLogs.log(player.getUsername(), ""+player.getUsername()+" just unmuted "+player2+"!");
				PlayerPunishment.unmute(player2);
				player.getPacketSender().sendConsoleMessage("Player "+player2+" was successfully unmuted. Command logs written.");
				Player plr = World.getPlayerByName(player2);
				if(plr != null) {
					plr.getPacketSender().sendMessage("You have been unmuted by "+player.getUsername()+".");
				}
			}
		}
		if(command[0].equalsIgnoreCase("ipmute")) {
			Player player2 = World.getPlayerByName(wholeCommand.substring(7));
			if(player2 == null) {
				player.getPacketSender().sendConsoleMessage("Could not find that player online.");
				return;
			} else {
				if(PlayerPunishment.IPMuted(player2.getHostAddress())){
					player.getPacketSender().sendConsoleMessage("Player "+player2.getUsername()+"'s IP is already IPMuted. Command logs written.");
					return;
				}
				final String mutedIP = player2.getHostAddress();
				PlayerPunishment.addMutedIP(mutedIP);
				player.getPacketSender().sendConsoleMessage("Player "+player2.getUsername()+" was successfully IPMuted. Command logs written.");
				player2.getPacketSender().sendMessage("You have been IPMuted by "+player.getUsername()+".");
				PlayerLogs.log(player.getUsername(), ""+player.getUsername()+" just IPMuted "+player2.getUsername()+"!");
			}
		}
		if(command[0].equalsIgnoreCase("ban")) {
			String playerToBan = wholeCommand.substring(4);
			if(!PlayerSaving.playerExists(playerToBan)) {
				player.getPacketSender().sendConsoleMessage("Player "+playerToBan+" does not exist.");
				return;
			} else {
				if(PlayerPunishment.banned(playerToBan)) {
					player.getPacketSender().sendConsoleMessage("Player "+playerToBan+" already has an active ban.");
					return;
				}
				PlayerLogs.log(player.getUsername(), ""+player.getUsername()+" just banned "+playerToBan+"!");
				PlayerPunishment.ban(playerToBan);
				player.getPacketSender().sendConsoleMessage("Player "+playerToBan+" was successfully banned. Command logs written.");
				Player toBan = World.getPlayerByName(playerToBan);
				if(toBan != null) {
					World.deregister(toBan);
				}
			}
		}
		if(command[0].equalsIgnoreCase("unban")) {
			String playerToBan = wholeCommand.substring(6);
			if(!PlayerSaving.playerExists(playerToBan)) {
				player.getPacketSender().sendConsoleMessage("Player "+playerToBan+" does not exist.");
				return;
			} else {
				if(!PlayerPunishment.banned(playerToBan)) {
					player.getPacketSender().sendConsoleMessage("Player "+playerToBan+" is not banned!");
					return;
				}
				PlayerLogs.log(player.getUsername(), ""+player.getUsername()+" just unbanned "+playerToBan+"!");
				PlayerPunishment.unban(playerToBan);
				player.getPacketSender().sendConsoleMessage("Player "+playerToBan+" was successfully unbanned. Command logs written.");
			}
		}
		if(command[0].equals("sql")) {
			MySQLController.toggle();
			if(player.hasRights(PlayerRights.OWNER)) {
				player.getPacketSender().sendConsoleMessage("Sql toggled to status: "+GameSettings.MYSQL_ENABLED);
			} else {
				player.getPacketSender().sendMessage("Sql toggled to status: "+GameSettings.MYSQL_ENABLED+".");
			}
		}
		if(command[0].equalsIgnoreCase("cpuban")) {
			Player player2 = World.getPlayerByName(wholeCommand.substring(7));
			if(player2 != null && !player2.getSerialNumber().equals("null")) {
				World.deregister(player2);
				ConnectionHandler.banComputer(player2.getUsername(), player2.getSerialNumber());
				PlayerPunishment.ban(player2.getUsername());
				player.getPacketSender().sendConsoleMessage("CPU Banned player.");
				PlayerLogs.log(player.getUsername(), ""+player.getUsername()+" just CPUBanned "+player2.getUsername()+"!");
			} else
				player.getPacketSender().sendConsoleMessage("Could not CPU-ban that player.");
		}
		if(command[0].equalsIgnoreCase("toggleinvis")) {
			player.setNpcTransformationId(player.getNpcTransformationId() > 0 ? -1 : 8254);
			player.getUpdateFlag().flag(Flag.APPEARANCE);
		}
		if(command[0].equalsIgnoreCase("ipban")) {
			Player player2 = World.getPlayerByName(wholeCommand.substring(6));
			if(player2 == null) {
				player.getPacketSender().sendConsoleMessage("Could not find that player online.");
				return;
			} else {
				if(PlayerPunishment.IPBanned(player2.getHostAddress())){
					player.getPacketSender().sendConsoleMessage("Player "+player2.getUsername()+"'s IP is already banned. Command logs written.");
					return;
				}
				final String bannedIP = player2.getHostAddress();
				PlayerPunishment.addBannedIP(bannedIP);
				player.getPacketSender().sendConsoleMessage("Player "+player2.getUsername()+"'s IP was successfully banned. Command logs written.");
				for(Player playersToBan : World.getPlayers()) {
					if(playersToBan == null)
						continue;
					if(playersToBan.getHostAddress() == bannedIP) {
						PlayerLogs.log(player.getUsername(), ""+player.getUsername()+" just IPBanned "+playersToBan.getUsername()+"!");
						World.deregister(playersToBan);
						if(player2.getUsername() != playersToBan.getUsername())
							player.getPacketSender().sendConsoleMessage("Player "+playersToBan.getUsername()+" was successfully IPBanned. Command logs written.");
					}
				}
			}
		}
		if(command[0].equalsIgnoreCase("unipmute")) {
			player.getPacketSender().sendConsoleMessage("Unipmutes can only be handled manually.");
		}
		if(command[0].equalsIgnoreCase("teletome")) {
			String playerToTele = wholeCommand.substring(9);
			Player player2 = World.getPlayerByName(playerToTele);
			if(player2 == null) {
				player.getPacketSender().sendConsoleMessage("Cannot find that player online..");
				return;
			} else {
				boolean canTele = TeleportHandler.checkReqs(player, player2.getPosition().copy()) && player.getRegionInstance() == null && player2.getRegionInstance() == null;
				if(canTele) {
					TeleportHandler.teleportPlayer(player2, player.getPosition().copy(), TeleportType.NORMAL);
					player.getPacketSender().sendConsoleMessage("Teleporting player to you: "+player2.getUsername()+"");
					player2.getPacketSender().sendMessage("You're being teleported to "+player.getUsername()+"...");
				} else
					player.getPacketSender().sendConsoleMessage("You can not teleport that player at the moment. Maybe you or they are in a minigame?");
			}
		}
		if(command[0].equalsIgnoreCase("movetome")) {
			String playerToTele = wholeCommand.substring(9);
			Player player2 = World.getPlayerByName(playerToTele);
			if(player2 == null) {
				player.getPacketSender().sendConsoleMessage("Cannot find that player..");
				return;
			} else {
				boolean canTele = TeleportHandler.checkReqs(player, player2.getPosition().copy()) && player.getRegionInstance() == null && player2.getRegionInstance() == null;
				if(canTele) {
					player.getPacketSender().sendConsoleMessage("Moving player: "+player2.getUsername()+"");
					player2.getPacketSender().sendMessage("You've been moved to "+player.getUsername());
					player2.moveTo(player.getPosition().copy());
				} else
					player.getPacketSender().sendConsoleMessage("Failed to move player to your coords. Are you or them in a minigame?");
			}
		}
		if(command[0].equalsIgnoreCase("kick")) {
			String player2 = wholeCommand.substring(5);
			Player playerToKick = World.getPlayerByName(player2);
			if(playerToKick == null) {
				player.getPacketSender().sendConsoleMessage("Player "+player2+" couldn't be found on Epsilon.");
				return;
			} else if(playerToKick.getLocation() != Location.WILDERNESS) {
				World.deregister(playerToKick);
				player.getPacketSender().sendConsoleMessage("Kicked "+playerToKick.getUsername()+".");
				PlayerLogs.log(player.getUsername(), ""+player.getUsername()+" just kicked "+playerToKick.getUsername()+"!");
			}
		}
	}

	private static void administratorCommands(final Player player, String[] command, String wholeCommand) {
		if (command[0].equals("coords")) {
			
			player.getPacketSender().sendMessage("Your X position is " + player.getPosition().getX());
			 player.getPacketSender().sendMessage("Your Y position is " +player.getPosition().getY());
				player.getPacketSender().sendMessage("Your Z position is " + player.getPosition().getZ());
		}
		if (command[0].equals("reset")) {
			for (Skill skill : Skill.values()) {
				int level = skill.equals(Skill.CONSTITUTION) ? 100 : skill.equals(Skill.PRAYER) ? 10 : 1;
				player.getSkillManager().setCurrentLevel(skill, level).setMaxLevel(skill, level).setExperience(skill, SkillManager.getExperienceForLevel(skill == Skill.CONSTITUTION ? 10 : 1));
			}
			player.getPacketSender().sendConsoleMessage("Your skill levels have now been reset.");
			player.getUpdateFlag().flag(Flag.APPEARANCE);
		}
		
		if (command[0].equals("master")) {
			for (Skill skill : Skill.values()) {
				int level = SkillManager.getMaxAchievingLevel(skill);
				player.getSkillManager().setCurrentLevel(skill, level).setMaxLevel(skill, level).setExperience(skill, SkillManager.MAX_EXPERIENCE);
			}
			player.getPacketSender().sendConsoleMessage("You are now a master of all skills.");
			player.getUpdateFlag().flag(Flag.APPEARANCE);
		}
		if (command[0].equals("setlevel") && !player.getUsername().equalsIgnoreCase("Jack")) {
			int skillId = Integer.parseInt(command[1]);
			int level = Integer.parseInt(command[2]);
			if(level > 15000) {
				player.getPacketSender().sendConsoleMessage("You can only have a maxmium level of 15000.");
				return;
			}
			Skill skill = Skill.forId(skillId);
			player.getSkillManager().setCurrentLevel(skill, level).setMaxLevel(skill, level).setExperience(skill, SkillManager.getExperienceForLevel(level));
			player.getPacketSender().sendConsoleMessage("You have set your " + skill.getName() + " level to " + level);
		}
		if (command[0].equals("item")) {
			int id = Integer.parseInt(command[1]);		
			int amount = (command.length == 2 ? 1 : Integer.parseInt(command[2].trim().toLowerCase().replaceAll("k", "000").replaceAll("m", "000000").replaceAll("b", "000000000")));
			if(amount > Integer.MAX_VALUE) {
				amount = Integer.MAX_VALUE;
			}
			Item item = new Item(id, amount);
			player.getInventory().add(item, true);

			player.getPacketSender().sendItemOnInterface(47052, 11694, 1);
		}
		if(wholeCommand.toLowerCase().startsWith("yell") && player.getHighestRights() == PlayerRights.PLAYER) {
			player.getPacketSender().sendMessage("Only members can yell. To become one, simply use ::store, buy a scroll").sendMessage("and then claim it.");
		}
		if (command[0].contains("pure")) {
			int[][] data = 
					new int[][]{
					{Equipment.HEAD_SLOT, 1153},
					{Equipment.CAPE_SLOT, 10499},
					{Equipment.AMULET_SLOT, 1725},
					{Equipment.WEAPON_SLOT, 4587},
					{Equipment.BODY_SLOT, 1129},
					{Equipment.SHIELD_SLOT, 1540},
					{Equipment.LEG_SLOT, 2497},
					{Equipment.HANDS_SLOT, 7459},
					{Equipment.FEET_SLOT, 3105},
					{Equipment.RING_SLOT, 2550},
					{Equipment.AMMUNITION_SLOT, 9244}
			};
			for (int i = 0; i < data.length; i++) {
				int slot = data[i][0], id = data[i][1];
				player.getEquipment().setItem(slot, new Item(id, id == 9244 ? 500 : 1));
			}
			BonusManager.update(player);
			WeaponInterfaces.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
			WeaponAnimations.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
			player.getEquipment().refreshItems();
			player.getUpdateFlag().flag(Flag.APPEARANCE);
			player.getInventory().resetItems();
			player.getInventory().add(1216, 1000).add(9186, 1000).add(862, 1000).add(892, 10000).add(4154, 5000).add(2437, 1000).add(2441, 1000).add(2445, 1000).add(386, 1000).add(2435, 1000);
			player.getSkillManager().newSkillManager();
			player.getSkillManager().setMaxLevel(Skill.ATTACK, 60).setMaxLevel(Skill.STRENGTH, 85).setMaxLevel(Skill.RANGED, 85).setMaxLevel(Skill.PRAYER, 520).setMaxLevel(Skill.MAGIC, 70).setMaxLevel(Skill.CONSTITUTION, 850);
			for(Skill skill : Skill.values()) {
				player.getSkillManager().setCurrentLevel(skill, player.getSkillManager().getMaxLevel(skill)).setExperience(skill, SkillManager.getExperienceForLevel(player.getSkillManager().getMaxLevel(skill)));
			}
		}
		if (command[0].equals("emptyitem")) {
			if(player.getInterfaceId() > 0 || player.getLocation() != null && player.getLocation() == Location.WILDERNESS) {
				player.getPacketSender().sendMessage("You cannot do this at the moment.");
				return;
			}
			int item = Integer.parseInt(command[1]);
			int itemAmount = player.getInventory().getAmount(item);
			Item itemToDelete = new Item(item, itemAmount);
			player.getInventory().delete(itemToDelete).refreshItems();
		}
		if(command[0].equals("gold")) {
			Player p = World.getPlayerByName(wholeCommand.substring(5));
			if(p != null) {
				long gold = 0;
				for(Item item : p.getInventory().getItems()) {
					if(item != null && item.getId() > 0 && item.tradeable())
						gold+= item.getDefinition().getValue();
				}
				for(Item item : p.getEquipment().getItems()) {
					if(item != null && item.getId() > 0 && item.tradeable())
						gold+= item.getDefinition().getValue();
				}
				for(int i = 0; i < 9; i++) {
					for(Item item : p.getBank(i).getItems()) {
						if(item != null && item.getId() > 0 && item.tradeable())
							gold+= item.getDefinition().getValue();
					}
				}
				gold += p.getMoneyInPouch();
				player.getPacketSender().sendMessage(p.getUsername() + " has "+Misc.insertCommasToNumber(String.valueOf(gold))+" coins.");
			} else
				player.getPacketSender().sendMessage("Can not find player online.");
		}
		if(command[0].equals("pray")) {
			player.getSkillManager().setCurrentLevel(Skill.PRAYER, 15000);
		}
		if(command[0].equals("cashineco")) {
			int gold = 0 , plrLoops = 0;
			for(Player p : World.getPlayers()) {
				if(p != null) {
					for(Item item : p.getInventory().getItems()) {
						if(item != null && item.getId() > 0 && item.tradeable())
							gold+= item.getDefinition().getValue();
					}
					for(Item item : p.getEquipment().getItems()) {
						if(item != null && item.getId() > 0 && item.tradeable())
							gold+= item.getDefinition().getValue();
					}
					for(int i = 0; i < 9; i++) {
						for(Item item : player.getBank(i).getItems()) {
							if(item != null && item.getId() > 0 && item.tradeable())
								gold+= item.getDefinition().getValue();
						}
					}
					gold += p.getMoneyInPouch();
					plrLoops++;
				}
			}
			player.getPacketSender().sendMessage("Total gold in economy right now: "+gold+", went through "+plrLoops+" players items.");
		}
		if (command[0].equals("tele")) {
			int x = Integer.valueOf(command[1]), y = Integer.valueOf(command[2]);
			int z = player.getPosition().getZ();
			if (command.length > 3)
				z = Integer.valueOf(command[3]);
			Position position = new Position(x, y, z);
			player.moveTo(position);
			player.getPacketSender().sendConsoleMessage("Teleporting to " + position.toString());
		}
		if (command[0].equals("bank")) {
			player.getBank(player.getCurrentBankTab()).open();
		}
		if (command[0].equals("find")) {
			String name = wholeCommand.substring(5).toLowerCase().replaceAll("_", " ");
			player.getPacketSender().sendConsoleMessage("Finding item id for item - " + name);
			boolean found = false;
			for (int i = 0; i < ItemDefinition.getMaxAmountOfItems(); i++) {
				if (ItemDefinition.forId(i).getName().toLowerCase().contains(name)) {
					player.getPacketSender().sendConsoleMessage("Found item with name [" + ItemDefinition.forId(i).getName().toLowerCase() + "] - id: " + i);
					found = true;
				}
			}
			if (!found) {
				player.getPacketSender().sendConsoleMessage("No item with name [" + name + "] has been found!");
			}
		} else if (command[0].equals("id")) {
			String name = wholeCommand.substring(3).toLowerCase().replaceAll("_", " ");
			player.getPacketSender().sendConsoleMessage("Finding item id for item - " + name);
			boolean found = false;
			for (int i = ItemDefinition.getMaxAmountOfItems()-1; i > 0; i--) {
				if (ItemDefinition.forId(i).getName().toLowerCase().contains(name)) {
					player.getPacketSender().sendConsoleMessage("Found item with name [" + ItemDefinition.forId(i).getName().toLowerCase() + "] - id: " + i);
					found = true;
				}
			}
			if (!found) {
				player.getPacketSender().sendConsoleMessage("No item with name [" + name + "] has been found!");
			}
		}
		if(command[0].equals("spec")) {
			player.setSpecialPercentage(100);
			CombatSpecial.updateBar(player);
		}
		if(command[0].equals("runes")) {
			for(Item t : ShopManager.getShops().get(0).getItems()) {
				if(t != null) {
					player.getInventory().add(new Item(t.getId(), 200000));
				}
			}
		}
		if (command[0].contains("gear")) {
			int[][] data = wholeCommand.contains("jack") ? 
					new int[][]{
				{Equipment.HEAD_SLOT, 1050},
				{Equipment.CAPE_SLOT, 12170},
				{Equipment.AMULET_SLOT, 15126},
				{Equipment.WEAPON_SLOT, 15444},
				{Equipment.BODY_SLOT, 14012},
				{Equipment.SHIELD_SLOT, 13740},
				{Equipment.LEG_SLOT, 14013},
				{Equipment.HANDS_SLOT, 7462},
				{Equipment.FEET_SLOT, 11732},
				{Equipment.RING_SLOT, 15220}
			} : wholeCommand.contains("range") ? 
					new int[][]{
				{Equipment.HEAD_SLOT, 3749},
				{Equipment.CAPE_SLOT, 10499},
				{Equipment.AMULET_SLOT, 15126},
				{Equipment.WEAPON_SLOT, 18357},
				{Equipment.BODY_SLOT, 2503},
				{Equipment.SHIELD_SLOT, 13740},
				{Equipment.LEG_SLOT, 2497},
				{Equipment.HANDS_SLOT, 7462},
				{Equipment.FEET_SLOT, 11732},
				{Equipment.RING_SLOT, 15019},
				{Equipment.AMMUNITION_SLOT, 9244},
			}:
				new int[][]{
						{Equipment.HEAD_SLOT, 1163},
						{Equipment.CAPE_SLOT, 19111},
						{Equipment.AMULET_SLOT, 6585},
						{Equipment.WEAPON_SLOT, 4151},
						{Equipment.WEAPON_SLOT, 5432},
						{Equipment.BODY_SLOT, 1127},
						{Equipment.SHIELD_SLOT, 13262},
						{Equipment.LEG_SLOT, 1079},
						{Equipment.HANDS_SLOT, 7462},
						{Equipment.FEET_SLOT, 11732},
						{Equipment.RING_SLOT, 2550}
				};
				for (int i = 0; i < data.length; i++) {
					int slot = data[i][0], id = data[i][1];
					player.getEquipment().setItem(slot, new Item(id, id == 9244 ? 500 : 1));
				}
				BonusManager.update(player);
				WeaponInterfaces.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
				WeaponAnimations.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
				player.getEquipment().refreshItems();
				player.getUpdateFlag().flag(Flag.APPEARANCE);
		}
	}


	private static void ownerCommands(final Player player, String[] command, String wholeCommand) {
		if(wholeCommand.equals("afk")) {
			World.sendMessage("<img=10> <col=FF0000><shad=0>"+player.getUsername()+": I am now away, please don't message me; I won't reply.");
		}
		if(wholeCommand.equals("123")) {
			World.sendMessage("[@red@Server@bla@]: A shooting star has just crashed at Al-Kharid Bank!");
		}
		if(wholeCommand.equals("restartlottery")) {
			Lottery.restartLottery();
		}
		if (command[0].equals("giveitem")) {
			int item = Integer.parseInt(command[1]);
			int amount = Integer.parseInt(command[2]);
			String rss = command[3];
			if(command.length > 4)
				rss+= " "+command[4];
			if(command.length > 5)
				rss+= " "+command[5];
			Player target = World.getPlayerByName(rss);
			if (target == null) {
				player.getPacketSender().sendConsoleMessage("Player must be online to give them stuff!");
			} else {
				player.getPacketSender().sendConsoleMessage("Gave player gold.");
				target.getInventory().add(item, amount);
			}
		}
		if (command[0].equals("update")) {
			int time = Integer.parseInt(command[1]);
			if(time > 0) {
				GameServer.setUpdating(true);
				for (Player players : World.getPlayers()) {
					if (players == null)
						continue;
					players.getPacketSender().sendSystemUpdate(time);
				}
				TaskManager.submit(new Task(time) {
					@Override
					protected void execute() {
						for (Player player : World.getPlayers()) {
							if (player != null) {
								World.savePlayers();
								GameServer.getLogger().info("Saved all players!");
								World.deregister(player);
							}
						}
						WellOfGoodwill.save();
						GrandExchangeOffers.save();
						ClanChatManager.save();
						GameServer.getLogger().info("Update task finished!");
						stop();
					}
				});
			}
		}
		if(command[0].contains("host")) {
			String plr = wholeCommand.substring(command[0].length()+1);
			Player playr2 = World.getPlayerByName(plr);
			if(playr2 != null) {
				player.getPacketSender().sendConsoleMessage(""+playr2.getUsername()+" host IP: "+playr2.getHostAddress()+", serial number: "+playr2.getSerialNumber());
			} else
				player.getPacketSender().sendConsoleMessage("Could not find player: "+plr);
		}
	}

	private static void developerCommands(Player player, String command[], String wholeCommand) {
		if(command[0].equals("doublexp")) {
			GameSettings.BONUS_EXP = !GameSettings.BONUS_EXP;
			player.getPacketSender().sendMessage("Double XP is now "+(GameSettings.BONUS_EXP ? "enabled" : "disabled")+".");
		}
		if(command[0].equals("regionid")) {
			int regionX = player.getPosition().getRegionX() >> 3;
			int regionY = player.getPosition().getRegionX() >> 3;
			int regionId = (regionX / 8 << 8) + regionY / 8;
			player.getPacketSender().sendMessage("Region id: "+regionId);
		}
		if(command[0].equals("fm")) {
			int[] forceMovement = new int[7];
			forceMovement[MovementQueue.FIRST_MOVEMENT_X] = 0;
			forceMovement[MovementQueue.FIRST_MOVEMENT_Y] = 2;
			forceMovement[MovementQueue.SECOND_MOVEMENT_X] = 0;
			forceMovement[MovementQueue.SECOND_MOVEMENT_Y] = 2;
			forceMovement[MovementQueue.MOVEMENT_SPEED] = 2 * 30;
			forceMovement[MovementQueue.MOVEMENT_REVERSE_SPEED] = 0;
			forceMovement[MovementQueue.MOVEMENT_DIRECTION] = Direction.NORTH.ordinal();
			player.setForceMovement(forceMovement);
			player.getUpdateFlag().flag(Flag.FORCED_MOVEMENT);
			player.performAnimation(new Animation(6132));
		}
		if(command[0].equals("sandwichlady")) {
			player.setSandwichLady(new SandwichLady(player));
			player.getSandwichLady().spawn();
		}
		if(command[0].equals("Runecraft")) {
			Misc.giveReward(player, Runecrafting.ITEMS);
		}
		if(command[0].equals("sendstring")) {
			int child = Integer.parseInt(command[1]);
			String string = command[2];
			player.getPacketSender().sendString(child, string);
		}
		if(command[0].equals("kill")) {
			Player target = World.getPlayerByName(command[1]);
			if (target == null) {
				player.getPacketSender().sendConsoleMessage("Player must be online to give them stuff!");
			} else {
				player.getPacketSender().sendConsoleMessage("killed?");
				target.appendDeath();
			}
		}
		if(command[0].equals("tasks")) {
			player.getPacketSender().sendConsoleMessage("Found "+TaskManager.getTaskAmount()+" tasks.");
		}
		if(command[0].equals("reloadcpubans")) {
			ConnectionHandler.reloadUUIDBans();
			player.getPacketSender().sendConsoleMessage("UUID bans reloaded!");
		}
		if(command[0].equals("reloadipbans")) {
			PlayerPunishment.reloadIPBans();
			player.getPacketSender().sendConsoleMessage("IP bans reloaded!");
		}
		if(command[0].equals("reloadipmutes")) {
			PlayerPunishment.reloadIPMutes();
			player.getPacketSender().sendConsoleMessage("IP mutes reloaded!");
		}
		if(command[0].equalsIgnoreCase("cpuban2")) {
			String serial = wholeCommand.substring(8);
			ConnectionHandler.banComputer("cpuban2", serial);
			player.getPacketSender().sendConsoleMessage(""+serial+" cpu was successfully banned. Command logs written.");
		}
		if(command[0].equalsIgnoreCase("ipban2")) {
			String ip = wholeCommand.substring(7);
			PlayerPunishment.addBannedIP(ip);
			player.getPacketSender().sendConsoleMessage(""+ip+" IP was successfully banned. Command logs written.");
		}
		if(command[0].equals("scc")) {
			/*PlayerPunishment.addBannedIP("46.16.33.9");
			ConnectionHandler.banComputer("Kustoms", -527305299);
			player.getPacketSender().sendMessage("Banned Kustoms.");
			 */
			/*for(GrandExchangeOffer of : GrandExchangeOffers.getOffers()) {
				if(of != null) {
					if(of.getId() == 34) {
					//	if(of.getOwner().toLowerCase().contains("eliyahu") || of.getOwner().toLowerCase().contains("matt")) {

							player.getPacketSender().sendConsoleMessage("FOUND IT! Owner: "+of.getOwner()+", amount: "+of.getAmount()+", finished: "+of.getAmountFinished());
						//	GrandExchangeOffers.getOffers().remove(of);
						//}
					}
				}
			}*/
			/*Player cc = World.getPlayerByName("Thresh");
			if(cc != null) {
				//cc.getPointsHandler().setPrestigePoints(50, true);
				//cc.getPointsHandler().refreshPanel();
				//player.getPacketSender().sendConsoleMessage("Did");
					cc.getSkillManager().setCurrentLevel(Skill.CONSTITUTION, 15000).updateSkill(Skill.CONSTITUTION);
					cc.getSkillManager().setCurrentLevel(Skill.PRAYER, 15000).updateSkill(Skill.PRAYER);
			}*/
			//player.getSkillManager().addExperience(Skill.CONSTITUTION, 200000000);
			//player.getSkillManager().setExperience(Skill.ATTACK, 1000000000);
			System.out.println("Seri: "+player.getSerialNumber());
		}
		if(command[0].equals("memory")) {
			//	ManagementFactory.getMemoryMXBean().gc();
			/*MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
			long mb = (heapMemoryUsage.getUsed() / 1000);*/
			long used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			player.getPacketSender().sendConsoleMessage("Heap usage: "+Misc.insertCommasToNumber(""+used+"")+" bytes!");
		}
		if(command[0].equals("star")) {
			ShootingStar.despawn(true);
			player.getPacketSender().sendConsoleMessage("star method called.");
		}
		if(command[0].equals("save")) {
			player.save();
		}
		if(command[0].equals("saveall")) {
			World.savePlayers();
		}
		if(command[0].equals("v1")) {
			World.sendMessage("<img=10> <col=008FB2>Another 20 voters have been rewarded! Vote now using the ::vote command!");
		}
		if(command[0].equals("test2")) {
			player.getSkillManager().addExperience(Skill.FARMING, 500);
		}
		if(command[0].equalsIgnoreCase("frame")) {
			int frame = Integer.parseInt(command[1]);
			String text = command[2];
			player.getPacketSender().sendString(frame, text);
		}
		if(command[0].equals("pos")) {
			player.getPacketSender().sendConsoleMessage(player.getPosition().toString());
		}
		if(command[0].equals("npc")) {
			int id = Integer.parseInt(command[1]);
			NPC npc = new NPC(id, new Position(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ()));
			World.register(npc);
			if (id == 1) {
				npc.setConstitution(5000);
			}
			
			//player.getPacketSender().sendEntityHint(npc);
			/*TaskManager.submit(new Task(5) {

				@Override
				protected void execute() {
					npc.moveTo(new Position(npc.getPosition().getX() + 2, npc.getPosition().getY() + 2));
					player.getPacketSender().sendEntityHintRemoval(false);
					stop();
				}

			});*/
			//npc.getMovementCoordinator().setCoordinator(new Coordinator().setCoordinate(true).setRadius(5));
		}
		if (command[0].equals("skull")) {
			if(player.getSkullTimer() > 0) {
				player.setSkullTimer(0);
				player.setSkullIcon(0);
				player.getUpdateFlag().flag(Flag.APPEARANCE);
			} else {
				CombatFactory.skullPlayer(player);
			}
		}
		if (command[0].equals("fillinv")) {
			for(int i = 0; i < 28; i++) {
				int it = Misc.getRandom(10000);
				player.getInventory().add(it, 1);
			}
		}
		if(command[0].equals("playnpc")) {
			player.setNpcTransformationId(Integer.parseInt(command[1]));
			player.getUpdateFlag().flag(Flag.APPEARANCE);
		} else if(command[0].equals("playobject")) {
			player.getPacketSender().sendObjectAnimation(new GameObject(2283, player.getPosition().copy()), new Animation(751));
			player.getUpdateFlag().flag(Flag.APPEARANCE);
		}
		if (command[0].equals("interface")) {
			int id = Integer.parseInt(command[1]);
			player.getPacketSender().sendInterface(id);
		}
		if(command[0].equals("test3")) {
			player.getPacketSender().sendInteractionOption("Pelt", 2, true);
		}
		if (command[0].equals("chatinterface")) {
			int id = Integer.parseInt(command[1]);
			player.getPacketSender().sendChatboxInterface(id);
		}
		if (command[0].equals("walkableinterface")) {
			int id = Integer.parseInt(command[1]);
			player.getPacketSender().sendWalkableInterface(id);
		}
		if (command[0].equals("anim")) {
			int id = Integer.parseInt(command[1]);
			player.performAnimation(new Animation(id));
			player.getPacketSender().sendConsoleMessage("Sending animation: " + id);
		}
		if(command[0].equals("cons")) {
			Construction.newHouse(player);
			Construction.enterHouse(player, player, true, true);
		}
		if (command[0].equals("gfx")) {
			int id = Integer.parseInt(command[1]);
			player.performGraphic(new Graphic(id));
			player.getPacketSender().sendConsoleMessage("Sending graphic: " + id);
		}
		if (command[0].equals("object")) {
			int id = Integer.parseInt(command[1]);
			player.getPacketSender().sendObject(new GameObject(id, player.getPosition(), 10, 2));
			player.getPacketSender().sendConsoleMessage("Sending object: " + id);
		}
		if (command[0].equals("config")) {
			int id = Integer.parseInt(command[1]);
			int state = Integer.parseInt(command[2]);
			player.getPacketSender().sendConfig(id, state).sendConsoleMessage("Sent config.");
		}
		if(command[0].equals("hint")) {
			player.getPacketSender().sendMessage("<img=10> @red@The Christmas Tree is currently located "+ChristmasEvent.CRASHED_EVENT.getEventLocation().clue+"!");
		}
		
		if (command[0].equals("checkbank")) {
			Player plr = World.getPlayerByName(wholeCommand.substring(10));
			if(plr != null) {
				player.getPacketSender().sendConsoleMessage("Loading bank..");
				for(Bank b : player.getBanks()) {
					if(b != null) {
						b.resetItems();
					}
				}
				for(int i = 0; i < plr.getBanks().length; i++) {
					for(Item it : plr.getBank(i).getItems()) {
						if(it != null) {
							player.getBank(i).add(it, false);
						}
					}
				}
				player.getBank(0).open();
			} else {
				player.getPacketSender().sendConsoleMessage("Player is offline!");
			}
		}
		if (command[0].equals("checkinv")) {
			Player player2 = World.getPlayerByName(wholeCommand.substring(9));
			if(player2 == null) {
				player.getPacketSender().sendConsoleMessage("Cannot find that player online..");
				return;
			}
			player.getInventory().setItems(player2.getInventory().getCopiedItems()).refreshItems();
		}
		if (command[0].equals("checkequip")) {
			Player player2 = World.getPlayerByName(wholeCommand.substring(11));
			if(player2 == null) {
				player.getPacketSender().sendConsoleMessage("Cannot find that player online..");
				return;
			}
			player.getEquipment().setItems(player2.getEquipment().getCopiedItems()).refreshItems();
			WeaponInterfaces.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
			WeaponAnimations.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
			BonusManager.update(player);
			player.getUpdateFlag().flag(Flag.APPEARANCE);
		}
	}
}
