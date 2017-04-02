package com.epsilon.net.packet.impl;

import com.epsilon.GameSettings;
import com.epsilon.model.PlayerRights;
import com.epsilon.model.Position;
import com.epsilon.model.Locations.Location;
import com.epsilon.model.container.impl.Bank;
import com.epsilon.model.container.impl.Bank.BankSearchAttributes;
import com.epsilon.model.definitions.WeaponInterfaces.WeaponInterface;
import com.epsilon.model.input.impl.EnterClanChatToJoin;
import com.epsilon.model.input.impl.EnterSyntaxToBankSearchFor;
import com.epsilon.net.packet.Packet;
import com.epsilon.net.packet.PacketListener;
import com.epsilon.util.Misc;
import com.epsilon.world.World;
import com.epsilon.world.content.Achievements;
import com.epsilon.world.content.BankPin;
import com.epsilon.world.content.BonusManager;
import com.epsilon.world.content.Consumables;
import com.epsilon.world.content.DropLog;
import com.epsilon.world.content.Emotes;
import com.epsilon.world.content.EnergyHandler;
import com.epsilon.world.content.ExperienceLamps;
import com.epsilon.world.content.ItemsKeptOnDeath;
import com.epsilon.world.content.KillsTracker;
import com.epsilon.world.content.LoyaltyProgramme;
import com.epsilon.world.content.MoneyPouch;
import com.epsilon.world.content.NPCDropTableChecker;
import com.epsilon.world.content.PlayerPanel;
import com.epsilon.world.content.PlayersOnlineInterface;
import com.epsilon.world.content.Sounds;
import com.epsilon.world.content.WellOfGoodwill;
import com.epsilon.world.content.Sounds.Sound;
import com.epsilon.world.content.clan.ClanChat;
import com.epsilon.world.content.clan.ClanChatManager;
import com.epsilon.world.content.combat.CombatFactory;
import com.epsilon.world.content.combat.magic.Autocasting;
import com.epsilon.world.content.combat.magic.MagicSpells;
import com.epsilon.world.content.combat.prayer.CurseHandler;
import com.epsilon.world.content.combat.prayer.PrayerHandler;
import com.epsilon.world.content.combat.weapon.CombatSpecial;
import com.epsilon.world.content.combat.weapon.FightType;
import com.epsilon.world.content.dialogue.DialogueManager;
import com.epsilon.world.content.dialogue.DialogueOptions;
import com.epsilon.world.content.grandexchange.GrandExchange;
import com.epsilon.world.content.minigames.impl.Dueling;
import com.epsilon.world.content.minigames.impl.Nomad;
import com.epsilon.world.content.minigames.impl.PestControl;
import com.epsilon.world.content.minigames.impl.RecipeForDisaster;
import com.epsilon.world.content.questtab.DailyTasks;
import com.epsilon.world.content.questtab.Links;
import com.epsilon.world.content.questtab.ServerInformation;
import com.epsilon.world.content.questtab.UserStatistics;
import com.epsilon.world.content.skill.ChatboxInterfaceSkillAction;
import com.epsilon.world.content.skill.impl.construction.Construction;
import com.epsilon.world.content.skill.impl.crafting.LeatherMaking;
import com.epsilon.world.content.skill.impl.crafting.Tanning;
import com.epsilon.world.content.skill.impl.dungeoneering.Dungeoneering;
import com.epsilon.world.content.skill.impl.dungeoneering.DungeoneeringParty;
import com.epsilon.world.content.skill.impl.dungeoneering.ItemBinding;
import com.epsilon.world.content.skill.impl.fletching.Fletching;
import com.epsilon.world.content.skill.impl.herblore.IngridientsBook;
import com.epsilon.world.content.skill.impl.slayer.Slayer;
import com.epsilon.world.content.skill.impl.smithing.SmithingData;
import com.epsilon.world.content.skill.impl.summoning.PouchMaking;
import com.epsilon.world.content.skill.impl.summoning.SummoningTab;
import com.epsilon.world.content.transportation.TeleportHandler;
import com.epsilon.world.content.transportation.TeleportType;
import com.epsilon.world.entity.impl.player.Player;

import mysql.impl.Store;
/**
 * This packet listener manages a button that the player has clicked upon.
 * 
 * @author Gabriel Hannason
 */

public class ButtonClickPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {

		int id = packet.readShort();

		if(player.hasRights(PlayerRights.OWNER)) {
			player.getPacketSender().sendConsoleMessage("Clicked button: "+id);
		}
		
		if(NPCDropTableChecker.getSingleton().handleButtonClick(player, id)) {
			return;
		}

		if(checkHandlers(player, id))
			return;
		
		boolean expLock = player.experienceLocked();

		switch(id) {
		
		case -4934://monsters
		case 11008:
			player.getPacketSender().sendString(60662, "Rock").sendInterface(60600);
			player.getPacketSender().sendString(60663, "Crabs");
			player.getPacketSender().sendString(60664, "PVM");
			player.getPacketSender().sendString(60665, "Zone");
			player.getPacketSender().sendString(60666, "");
			player.getPacketSender().sendString(60667, "Experiments");
			player.getPacketSender().sendString(60668, "Yak");
			player.getPacketSender().sendString(60669, "Field");
			player.getPacketSender().sendString(60670, "");
			player.getPacketSender().sendString(60671, "Ghouls");
			player.getPacketSender().sendString(60672, "Chaos");
			player.getPacketSender().sendString(60673, "Druids");
			player.getPacketSender().sendString(60674, "Dust");
			player.getPacketSender().sendString(60675, "Devils");
			player.getPacketSender().sendString(60676, "Armoured");
			player.getPacketSender().sendString(60677, "Skeletons");
			player.getPacketSender().sendString(60678, "Monkey");
			player.getPacketSender().sendString(60679, "Guards");
			player.getPacketSender().sendString(60701, "TzHaar");
			player.getPacketSender().sendString(60702, "");
			player.getPacketSender().sendString(60703, "Skeletons");
			player.getPacketSender().sendString(60704, "Chicken");
			player.getPacketSender().sendString(60705, "Coop");
			break;
		case 11017:
		case -4931:
			player.getPacketSender().sendString(60662, "Warriors").sendInterface(60700);
			player.getPacketSender().sendString(60663, "Guild");
			player.getPacketSender().sendString(60664, "Pest");
			player.getPacketSender().sendString(60665, "Control");
			player.getPacketSender().sendString(60666, "Duel");
			player.getPacketSender().sendString(60667, "Arena");
			player.getPacketSender().sendString(60668, "");
			player.getPacketSender().sendString(60669, "Barrows");
			player.getPacketSender().sendString(60670, "Fight");
			player.getPacketSender().sendString(60671, "Caves");
			player.getPacketSender().sendString(60672, "Clan");
			player.getPacketSender().sendString(60673, "Wars");
			player.getPacketSender().sendString(60674, "");
			player.getPacketSender().sendString(60675, "");
			player.getPacketSender().sendString(60676, "");
			player.getPacketSender().sendString(60677, "");
			player.getPacketSender().sendString(60678, "");
			player.getPacketSender().sendString(60679, "");
			player.getPacketSender().sendString(60701, "");
			player.getPacketSender().sendString(60702, "");
			player.getPacketSender().sendString(60703, "");
			player.getPacketSender().sendString(60704, "");
			player.getPacketSender().sendString(60705, "");
			break;
		case -4928:
		case 11014:
			player.getPacketSender().sendString(60662, "Daggonath").sendInterface(60800);
			player.getPacketSender().sendString(60663, "Kings");
			player.getPacketSender().sendString(60664, "Tormented");
			player.getPacketSender().sendString(60665, "Demons");
			player.getPacketSender().sendString(60666, "King Black");
			player.getPacketSender().sendString(60667, "Dragon");
			player.getPacketSender().sendString(60668, "(Wildy)");
			player.getPacketSender().sendString(60669, "C. Elemental");
			player.getPacketSender().sendString(60670, "Slash");
			player.getPacketSender().sendString(60671, "Bash");
			player.getPacketSender().sendString(60672, "");
			player.getPacketSender().sendString(60673, "Nomad");
			player.getPacketSender().sendString(60674, "Kalphite");
			player.getPacketSender().sendString(60675, "Queen");
			player.getPacketSender().sendString(60676, "");
			player.getPacketSender().sendString(60677, "Phoenix");
			player.getPacketSender().sendString(60678, "Bandos");
			player.getPacketSender().sendString(60679, "Avatar");
			player.getPacketSender().sendString(60680, "");
			player.getPacketSender().sendString(60701, "Glacors");
			player.getPacketSender().sendString(60702, "Corporeal");
			player.getPacketSender().sendString(60703, "Beast");
			player.getPacketSender().sendString(60704, "");
			player.getPacketSender().sendString(60705, "Nex");
			break;
		case -4925:
			player.getPacketSender().sendString(60662, "").sendInterface(60900);
			player.getPacketSender().sendString(60663, "Scorpia");
			player.getPacketSender().sendString(60664, "Cerberus");
			player.getPacketSender().sendString(60665, "(Wildy)");
			player.getPacketSender().sendString(60666, "Abyssal");
			player.getPacketSender().sendString(60667, "Sire");
			player.getPacketSender().sendString(60668, "Lizardman");
			player.getPacketSender().sendString(60669, "Shaman");
			player.getPacketSender().sendString(60670, "Thermonuclear");
			player.getPacketSender().sendString(60671, "Devils (Wildy)");
			player.getPacketSender().sendString(60672, "Venenatis");
			player.getPacketSender().sendString(60673, "(Wildy)");
			player.getPacketSender().sendString(60674, "");
			player.getPacketSender().sendString(60675, "Kraken");
			player.getPacketSender().sendString(60676, "");
			player.getPacketSender().sendString(60677, "Callisto");
			player.getPacketSender().sendString(60678, "");
			player.getPacketSender().sendString(60679, "Zulrah");
			player.getPacketSender().sendString(60680, "");
			player.getPacketSender().sendString(60701, "Skotizo");
			player.getPacketSender().sendString(60702, "");
			player.getPacketSender().sendString(60703, "");
			player.getPacketSender().sendString(60704, "");
			player.getPacketSender().sendString(60705, "");
			break;
		case -4922:
		case 11020:
			player.getPacketSender().sendString(60662, "Wilderness").sendInterface(61000);
			player.getPacketSender().sendString(60663, "Ditch");
			player.getPacketSender().sendString(60664, "West Dragons");
			player.getPacketSender().sendString(60665, "(Wildy)");
			player.getPacketSender().sendString(60666, "East Dragons");
			player.getPacketSender().sendString(60667, "(Wildy)");
			player.getPacketSender().sendString(60668, "Mage Arena");
			player.getPacketSender().sendString(60669, "(Multi)");
			player.getPacketSender().sendString(60670, "Death Arena");
			player.getPacketSender().sendString(60671, "(Multi)");
			player.getPacketSender().sendString(60672, "Frost Dragons");
			player.getPacketSender().sendString(60673, "(Wildy)");
			player.getPacketSender().sendString(60674, "");
			player.getPacketSender().sendString(60675, "");
			player.getPacketSender().sendString(60676, "");
			player.getPacketSender().sendString(60677, "");
			player.getPacketSender().sendString(60678, "");
			player.getPacketSender().sendString(60679, "");
			player.getPacketSender().sendString(60680, "");
			player.getPacketSender().sendString(60701, "");
			player.getPacketSender().sendString(60702, "");
			player.getPacketSender().sendString(60703, "");
			player.getPacketSender().sendString(60704, "");
			player.getPacketSender().sendString(60705, "");
			break;
		case 11011:
		case 1167:
		case -4919:
			player.getPacketSender().sendString(60662, "Godwars").sendInterface(61100);
			player.getPacketSender().sendString(60663, "Dungeon");
			player.getPacketSender().sendString(60664, "Strykwyrm");
			player.getPacketSender().sendString(60665, "Cavern");
			player.getPacketSender().sendString(60666, "Ancient");
			player.getPacketSender().sendString(60667, "Cavern");
			player.getPacketSender().sendString(60668, "Revenants");
			player.getPacketSender().sendString(60669, "(Wildy)");
			player.getPacketSender().sendString(60670, "Chaos");
			player.getPacketSender().sendString(60671, "Tunnels");
			player.getPacketSender().sendString(60672, "Brimhaven");
			player.getPacketSender().sendString(60673, "Dungeon");
			player.getPacketSender().sendString(60674, "Taverly");
			player.getPacketSender().sendString(60675, "Dungeon");
			player.getPacketSender().sendString(60676, "");
			player.getPacketSender().sendString(60677, "");
			player.getPacketSender().sendString(60678, "");
			player.getPacketSender().sendString(60679, "");
			player.getPacketSender().sendString(60680, "");
			player.getPacketSender().sendString(60701, "");
			player.getPacketSender().sendString(60702, "");
			player.getPacketSender().sendString(60703, "");
			player.getPacketSender().sendString(60704, "");
			player.getPacketSender().sendString(60705, "");
			break;
		case -4914://1
			if (player.getInterfaceId() == 60600) {
			TeleportHandler.teleportPlayer(player, new Position(2695, 3714), TeleportType.NORMAL);//rock crabs
			}else if (player.getInterfaceId() == 60700) {
				TeleportHandler.teleportPlayer(player, new Position(2855, 3543), TeleportType.NORMAL);//Warriors Guild
			}else if (player.getInterfaceId() == 60800) {
				TeleportHandler.teleportPlayer(player, new Position(1908, 4367), TeleportType.NORMAL);//Daggonath Kings
			}else if (player.getInterfaceId() == 60900) {
				TeleportHandler.teleportPlayer(player, new Position(2847, 9637), TeleportType.NORMAL);//Scorpia
			}else if (player.getInterfaceId() == 61000) {
				TeleportHandler.teleportPlayer(player, new Position(3087, 3517), TeleportType.NORMAL);//Wildy Ditch
			}else if (player.getInterfaceId() == 61100) {
				TeleportHandler.teleportPlayer(player, new Position(2871, 5318, 2), TeleportType.NORMAL);//Godwars
			}
			break;
		case -4905://4
			if (player.getInterfaceId() == 60600) {
			TeleportHandler.teleportPlayer(player, new Position(3206, 3264), TeleportType.NORMAL);//yaks
		}else if (player.getInterfaceId() == 60700) {
			TeleportHandler.teleportPlayer(player, new Position(3565, 3313), TeleportType.NORMAL);//Barrows
		}else if (player.getInterfaceId() == 60800) {
			TeleportHandler.teleportPlayer(player, new Position(3281, 3914), TeleportType.NORMAL);//Chaos Elemental
		}else if (player.getInterfaceId() == 60900) {
			TeleportHandler.teleportPlayer(player, new Position(2514, 3045), TeleportType.NORMAL);//Lizardman Shaman
		}else if (player.getInterfaceId() == 61000) {
			TeleportHandler.teleportPlayer(player, new Position(3107, 3937), TeleportType.NORMAL);//Mage Arena
		}else if (player.getInterfaceId() == 61100) {
			TeleportHandler.teleportPlayer(player, new Position(3217, 9617), TeleportType.NORMAL);//Revs
		}
			break;
		case -4896://7
			if (player.getInterfaceId() == 60600) {
			TeleportHandler.teleportPlayer(player, new Position(3279, 2964), TeleportType.NORMAL);//dust devils
			}else if (player.getInterfaceId() == 60800) {
				TeleportHandler.teleportPlayer(player, new Position(3483, 9482), TeleportType.NORMAL);//Kalphite Queen
			}else if (player.getInterfaceId() == 60900) {
				TeleportHandler.teleportPlayer(player, new Position(2371, 4673), TeleportType.NORMAL);//Kraken
			}else if (player.getInterfaceId() == 61100) {
				TeleportHandler.teleportPlayer(player, new Position(2884, 9797), TeleportType.NORMAL);//Taverly Dungeon
			}
			break;
		case -4845://10
			if (player.getInterfaceId() == 60600) {
			TeleportHandler.teleportPlayer(player, new Position(2480, 5175), TeleportType.NORMAL);//tzhaar
		} else if (player.getInterfaceId() == 60800) {
			TeleportHandler.teleportPlayer(player, new Position(3050, 9573), TeleportType.NORMAL);//Glacors
		} else if (player.getInterfaceId() == 60900) {
			TeleportHandler.teleportPlayer(player, new Position(2796, 9332), TeleportType.NORMAL);//Skotizo
		}
			break;
		case -4911://2
			if (player.getInterfaceId() == 60600) {
			TeleportHandler.teleportPlayer(player, new Position(2722, 4910), TeleportType.NORMAL);//pvm zone
			}else if (player.getInterfaceId() == 60700) {
				TeleportHandler.teleportPlayer(player, new Position(2664, 2651), TeleportType.NORMAL);//Pest Control
			}else if (player.getInterfaceId() == 60800) {
				TeleportHandler.teleportPlayer(player, new Position(2541, 5794), TeleportType.NORMAL);//Tormented Demons
			}else if (player.getInterfaceId() == 60900) {
				TeleportHandler.teleportPlayer(player, new Position(3097, 3704), TeleportType.NORMAL);//Cerberus
			}else if (player.getInterfaceId() == 61000) {
				TeleportHandler.teleportPlayer(player, new Position(2980, 3597), TeleportType.NORMAL);//West Dragons
			}else if (player.getInterfaceId() == 61100) {
				TeleportHandler.teleportPlayer(player, new Position(2731, 5095), TeleportType.NORMAL);//StrykWyrm Cavern
			}
			break;
		case -4902://5
			if (player.getInterfaceId() == 60600) {
			TeleportHandler.teleportPlayer(player, new Position(3420, 3510), TeleportType.NORMAL);//ghouls
			}else if (player.getInterfaceId() == 60700) {
				TeleportHandler.teleportPlayer(player, new Position(2440, 5172), TeleportType.NORMAL);//Fight Caves
			}else if (player.getInterfaceId() == 60800) {
				TeleportHandler.teleportPlayer(player, new Position(3292, 4942), TeleportType.NORMAL);//Slash Bash
			}else if (player.getInterfaceId() == 60900) {
				TeleportHandler.teleportPlayer(player, new Position(2970, 3948), TeleportType.NORMAL);//Thermonuclear Smoke Devil
			}else if (player.getInterfaceId() == 61000) {
				player.moveTo(new Position(3048, 10266, player.getPosition().getZ()));
				player.getPacketSender().sendInterfaceRemoval();
				player.getPacketSender().sendMessage("@red@Teleport spells are blocked here. Use a portal or ladder to escape!");
				player.getPacketSender().sendMessage("@red@Protect item does not work here either!");
				CombatFactory.skullPlayer(player);//Death Arena
			}else if (player.getInterfaceId() == 61100) {
				TeleportHandler.teleportPlayer(player, new Position(3185, 5471), TeleportType.NORMAL);//Chaos Tunnels
			}
			break;
		case -4842://11
			if (player.getInterfaceId() == 60600) {
				TeleportHandler.teleportPlayer(player, new Position(3132, 9909), TeleportType.NORMAL);//skeles
			}else if (player.getInterfaceId() == 60800) {
					TeleportHandler.teleportPlayer(player, new Position(2564, 4940), TeleportType.NORMAL);//Corp
			}
		case -4893://8
			if (player.getInterfaceId() == 60600) {
			TeleportHandler.teleportPlayer(player, new Position(3085, 9672), TeleportType.NORMAL);//skeletons
		}else if (player.getInterfaceId() == 60800) {
			TeleportHandler.teleportPlayer(player, new Position(2839, 9557), TeleportType.NORMAL);//Phoenix
		}else if (player.getInterfaceId() == 60900) {
			TeleportHandler.teleportPlayer(player, new Position(1753, 5232), TeleportType.NORMAL);//Cellisto
		}
			break;
		case -4908://3
			if (player.getInterfaceId() == 60600) {
			TeleportHandler.teleportPlayer(player, new Position(3557, 9946), TeleportType.NORMAL);//experiments
			}else if (player.getInterfaceId() == 60700) {
				TeleportHandler.teleportPlayer(player, new Position(3369, 3268), TeleportType.NORMAL);//Duel Arena
			}else if (player.getInterfaceId() == 60800) {
				TeleportHandler.teleportPlayer(player, new Position(2273, 4681), TeleportType.NORMAL);//King Black Dragon
			}else if (player.getInterfaceId() == 60900) {
				TeleportHandler.teleportPlayer(player, new Position(2454, 5213), TeleportType.NORMAL);//Abyssal Sire
			}else if (player.getInterfaceId() == 61000) {
				TeleportHandler.teleportPlayer(player, new Position(3329, 3660), TeleportType.NORMAL);//East Dragons
			}else if (player.getInterfaceId() == 61100) {
				TeleportHandler.teleportPlayer(player, new Position(1745, 5325), TeleportType.NORMAL);//Ancient Cavern
			}
			break;
		case -4899://6
			if (player.getInterfaceId() == 60600) {
			TeleportHandler.teleportPlayer(player, new Position(2933, 9849), TeleportType.NORMAL);//Chaos Druids
			} else if (player.getInterfaceId() == 60700) {
				TeleportHandler.teleportPlayer(player, new Position(1995, 4493), TeleportType.NORMAL);//Clanwars
		} else if (player.getInterfaceId() == 60800) {
			TeleportHandler.teleportPlayer(player, new Position(3361, 5856), TeleportType.NORMAL);//Nomad
		}else if (player.getInterfaceId() == 60900) {
			TeleportHandler.teleportPlayer(player, new Position(3186, 3830), TeleportType.NORMAL);//Venenatis
		}else if (player.getInterfaceId() == 61000) {
			TeleportHandler.teleportPlayer(player, new Position(2948, 3904), TeleportType.NORMAL);//Frosts
		}else if (player.getInterfaceId() == 61100) {
			TeleportHandler.teleportPlayer(player, new Position(2713, 9564), TeleportType.NORMAL);//Brimhaven
		}
			break;
		case -4890://9
			if (player.getInterfaceId() == 60600) {
			TeleportHandler.teleportPlayer(player, new Position(2793, 2773), TeleportType.NORMAL);//Monkey Guards
			} else if (player.getInterfaceId() == 60800) {
				TeleportHandler.teleportPlayer(player, new Position(2857, 3808), TeleportType.NORMAL);//Bandos Avatar
			}else if (player.getInterfaceId() == 60900) {
				TeleportHandler.teleportPlayer(player, new Position(2464, 4775), TeleportType.NORMAL);//Zulrah
			}
			break;
		case -4839://12
			if (player.getInterfaceId() == 60600) {
			TeleportHandler.teleportPlayer(player, new Position(3235, 3295), TeleportType.NORMAL);//Chickens
			} else if (player.getInterfaceId() == 60800) {
				TeleportHandler.teleportPlayer(player, new Position(2903, 5204), TeleportType.NORMAL);//Nex
			}
			break;
		
		case 2461:
			if(player.getDialogueActionId() == 148) {
				System.out.println("Here");
				player.getPacketSender().sendInterfaceRemoval();
				new Store().claim(player);
			}
			break;
		/*QUEST START*/
		case -20522:
			player.getGamble().handleConfigButtons(player, -20522);
			break;
		case -20523:
			player.getGamble().handleConfigButtons(player, -20523);
			break;
			
		case 45013:
			player.getPacketSender().sendConfig(974, 2);
			break;
		
		case -15534: // server information
			ServerInformation.display(player);
			break;
			
		case -15533: // user information
			UserStatistics.display(player);
			break;
			
		case -15532: // daily tasks
			DailyTasks.display(player);
			break;
	
		case -15531: // links
			Links.display(player);
			break;
		
		/*QUEST END*/
		case 16137:
		case 16138:
		case 16139:
		case 16140:
		case 16141:
		case 16142:
		case 16143:
			if(player.getSandwichLady() != null) {
				player.getSandwichLady().handleButton(id);
			}
			break;
		case -27454:
		case -27534:
		case 5384:
		case -4534:
			player.getPacketSender().sendInterfaceRemoval();
			break;
		case 1036:
			EnergyHandler.rest(player);
			break;
		case -26376:
			PlayersOnlineInterface.showInterface(player);
			break;
		case 27229:
			DungeoneeringParty.create(player);
			break;
		case 26226:
		case 26229:
			if(Dungeoneering.doingDungeoneering(player)) {
				DialogueManager.start(player, 114);
				player.setDialogueActionId(71);
			} else {
				Dungeoneering.leave(player, false, true);
			}
			break;
		case 26244:
		case 26247:
			if(player.getMinigameAttributes().getDungeoneeringAttributes().getParty() != null) {
				if(player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getOwner().getUsername().equals(player.getUsername())) {
					DialogueManager.start(player, id == 26247 ? 106 : 105);
					player.setDialogueActionId(id == 26247 ? 68 : 67);
				} else {
					player.getPacketSender().sendMessage("Only the party owner can change this setting.");
				}
			}
			break;
		case 28180:
			TeleportHandler.teleportPlayer(player, new Position(3450, 3715), player.getSpellbook().getTeleportType());
			break;
		case 28177:
			if(!TeleportHandler.checkReqs(player, null)) {
				return;
			}
			if(!player.getClickDelay().elapsed(4500) || player.getMovementQueue().isLockMovement()) {
				return;
			}
			if(player.getLocation() == Location.CONSTRUCTION) {
				return;
			}
			Construction.newHouse(player);
			Construction.enterHouse(player, player, true, true);
			break;
		case 14176:
			player.setUntradeableDropItem(null);
			player.getPacketSender().sendInterfaceRemoval();
			break;
		case 14175:
			player.getPacketSender().sendInterfaceRemoval();
			if(player.getUntradeableDropItem() != null && player.getInventory().contains(player.getUntradeableDropItem().getId())) {
				ItemBinding.unbindItem(player, player.getUntradeableDropItem().getId());
				player.getInventory().delete(player.getUntradeableDropItem());
				player.getPacketSender().sendMessage("Your item vanishes as it hits the floor.");
				Sounds.sendSound(player, Sound.DROP_ITEM);
			}
			player.setUntradeableDropItem(null);
			break;
		case 1013:
			player.getSkillManager().setTotalGainedExp(0);
			break;
		case -26373:
			if(WellOfGoodwill.isActive()) {
				player.getPacketSender().sendMessage("<img=10> <col=008FB2>The Well of Goodwill is granting 30% bonus experience for another "+WellOfGoodwill.getMinutesRemaining()+" minutes.");
			} else {
				player.getPacketSender().sendMessage("<img=10> <col=008FB2>The Well of Goodwill needs another "+Misc.insertCommasToNumber(""+WellOfGoodwill.getMissingAmount())+" coins before becoming full.");
			}
			break;
		case -26352:
			player.setDialogueActionId(11);
			DialogueManager.start(player, 20);
			break;
		case -15469:
			KillsTracker.open(player);
			break;
		case -15468:
			DropLog.open(player);
			break;
		case -10531:
			if(player.isKillsTrackerOpen()) {
				player.setKillsTrackerOpen(false);
				player.getPacketSender().sendTabInterface(GameSettings.QUESTS_TAB, 50000);
			}
			break;
		case -26333:
			player.getPacketSender().sendString(1, "www.epsilonps.org/community");
			player.getPacketSender().sendMessage("Attempting to open: epsilonps.org/community");
			break;
		case -26332:
			player.getPacketSender().sendString(1, "www.epsilonps.org/rules/");
			player.getPacketSender().sendMessage("Attempting to open: epsilonps.org/rules");
			break;
		case -26331:
			player.getPacketSender().sendString(1, "www.epsilonps.org/store/");
			player.getPacketSender().sendMessage("Attempting to open: epsilonps.org/store");
			break;
		case -26330:
			player.getPacketSender().sendString(1, "www.epsilonps.org/vote/");
			player.getPacketSender().sendMessage("Attempting to open: epsilonps.org/vote");
			break;
		case -26329:
			player.getPacketSender().sendString(1, "www.epsilonps.org/hiscores/");
			player.getPacketSender().sendMessage("Attempting to open: epsilonps.orgm/hiscores");
			break;
		case -26328:
			player.getPacketSender().sendString(1, "www.epsilonps.org/report/");
			player.getPacketSender().sendMessage("Attempting to open: epsilonps.org/report");
			break;
		case -15484:
			if (Player.serverInformation = true)
				return;
			if (Player.links = true)
				return;
			if (Player.userStatistics = true)
				return;
			if (Player.dailyTasks = true) {
				RecipeForDisaster.openQuestLog(player);
			}
			break;
		case -15483:
			if (Player.serverInformation = true)
				return;
			if (Player.links = true)
				return;
			if (Player.userStatistics = true)
				return;
			if (Player.dailyTasks = true) {
				Nomad.openQuestLog(player);
			}
			break;
		case 350:
			player.getPacketSender().sendMessage("To autocast a spell, please right-click it and choose the autocast option.").sendTab(GameSettings.MAGIC_TAB).sendConfig(108, player.getAutocastSpell() == null ? 3 : 1);
			break;
		case 12162:
			DialogueManager.start(player, 61);
			player.setDialogueActionId(28);
			break;
		case 29335:
			if(player.getInterfaceId() > 0) {
				player.getPacketSender().sendMessage("Please close the interface you have open before opening another one.");
				return;
			}
			DialogueManager.start(player, 60);
			player.setDialogueActionId(27);
			break;
		case 29455:
			if(player.getInterfaceId() > 0) {
				player.getPacketSender().sendMessage("Please close the interface you have open before opening another one.");
				return;
			}
			ClanChatManager.toggleLootShare(player);
			break;
		case 8658:
			DialogueManager.start(player, 55);
			player.setDialogueActionId(26);
			break;
		case 11001:
			   TeleportHandler.teleportPlayer(player, GameSettings.DEFAULT_POSITION.copy(), player.getSpellbook().getTeleportType());
			break;
		case 8667:
			TeleportHandler.teleportPlayer(player, new Position(2742, 3443), player.getSpellbook().getTeleportType());
			break;
		case 8672:
			TeleportHandler.teleportPlayer(player, new Position(2595, 4772), player.getSpellbook().getTeleportType());
			player.getPacketSender().sendMessage("<img=10> To get started with Runecrafting, buy a talisman and use the locate option on it.");
			break;
		case 8861:
			TeleportHandler.teleportPlayer(player, new Position(2914, 3450), player.getSpellbook().getTeleportType());
			break;
		case 8656:
			player.setDialogueActionId(47);
			DialogueManager.start(player, 86);
			break;
		case 8659:
			TeleportHandler.teleportPlayer(player, new Position(3024, 9741), player.getSpellbook().getTeleportType());
			break;
		case 8664:
			TeleportHandler.teleportPlayer(player, new Position(3096, 3509), player.getSpellbook().getTeleportType());
			break;
		case 8666:
			TeleportHandler.teleportPlayer(player, new Position(3085, 3496), player.getSpellbook().getTeleportType());
			break;
		case 8671:
			player.setDialogueActionId(56);
			DialogueManager.start(player, 89);
			break;
		case 8670:
			TeleportHandler.teleportPlayer(player, new Position(2717, 3499), player.getSpellbook().getTeleportType());
			break;
		case 8668:
			TeleportHandler.teleportPlayer(player, new Position(2709, 3437), player.getSpellbook().getTeleportType());
			break;
		case 8665:
			TeleportHandler.teleportPlayer(player, new Position(3079, 3495), player.getSpellbook().getTeleportType());
			break;
		 case 8662:
			   TeleportHandler.teleportPlayer(player, new Position(2590, 3420), player.getSpellbook().getTeleportType());
			   break;
		case 13928:
			TeleportHandler.teleportPlayer(player, new Position(3052, 3304), player.getSpellbook().getTeleportType());
			break;
		case 28179:
			TeleportHandler.teleportPlayer(player, new Position(2209, 5348), player.getSpellbook().getTeleportType());
			break;
		case 28178:
			DialogueManager.start(player, 54);
			player.setDialogueActionId(25);
			break;
		case 1159: //Bones to Bananas
		case 15877://Bones to peaches
		case 30306:
			MagicSpells.handleMagicSpells(player, id);
			break;
		case 10001:
			if(player.getInterfaceId() == -1) {
				Consumables.handleHealAction(player);
			} else {
				player.getPacketSender().sendMessage("You cannot heal yourself right now.");
			}
			break;
		case 18025:
			if(PrayerHandler.isActivated(player, PrayerHandler.AUGURY)) {
				PrayerHandler.deactivatePrayer(player, PrayerHandler.AUGURY);
			} else {
				PrayerHandler.activatePrayer(player, PrayerHandler.AUGURY);
			}
			break;
		case 18018:
			if(PrayerHandler.isActivated(player, PrayerHandler.RIGOUR)) {
				PrayerHandler.deactivatePrayer(player, PrayerHandler.RIGOUR);
			} else {
				PrayerHandler.activatePrayer(player, PrayerHandler.RIGOUR);
			}
			break;
		case 10000:
		case 950:
			if(player.getInterfaceId() < 0)
				player.getPacketSender().sendInterface(40030);
			else
				player.getPacketSender().sendMessage("Please close the interface you have open before doing this.");
			break;
		case 3546:
		case 3420:
			if(System.currentTimeMillis() - player.getTrading().lastAction <= 300)
				return;
			player.getTrading().lastAction = System.currentTimeMillis();
			if(player.getTrading().inTrade()) {
				player.getTrading().acceptTrade(id == 3546 ? 2 : 1);
			} else {
				player.getPacketSender().sendInterfaceRemoval();
			}
			break;
		case -20532:
			if(System.currentTimeMillis() - player.getGamble().lastAction <= 300)
				return;
			player.getGamble().lastAction = System.currentTimeMillis();
			if(player.getGamble().inGambling()) {
				player.getGamble().acceptTrade(id == -20532 ? 2 : 1);
			} else {
				player.getPacketSender().sendInterfaceRemoval();
			}
			break;
		case -20529:
			if(System.currentTimeMillis() - player.getGamble().lastAction <= 300)
				return;
			player.getGamble().lastAction = System.currentTimeMillis();
			if(player.getGamble().inGambling()) {
				player.getGamble().setAcceptedGamble(false);
				player.getPacketSender().sendInterfaceRemoval();
			}
			break;
		case 10162:
		case -18269:
			player.getPacketSender().sendInterfaceRemoval();
			break;
		case 841:
			IngridientsBook.readBook(player, player.getCurrentBookPage() + 2, true);
			break;
		case 839:
			IngridientsBook.readBook(player, player.getCurrentBookPage() - 2, true);
			break;
		case 14922:
			player.getPacketSender().sendClientRightClickRemoval().sendInterfaceRemoval();
			break;
		case 14921:
			player.getPacketSender().sendMessage("Please visit the forums and ask for help in the support section.");
			break;
		case 5294:
			player.getPacketSender().sendClientRightClickRemoval().sendInterfaceRemoval();
			player.setDialogueActionId(player.getBankPinAttributes().hasBankPin() ? 8 : 7);
			DialogueManager.start(player, DialogueManager.getDialogues().get(player.getBankPinAttributes().hasBankPin() ? 12 : 9));
			break;
		case 27653:
			if(!player.busy() && !player.getCombatBuilder().isBeingAttacked() && !Dungeoneering.doingDungeoneering(player)) {
				player.getSkillManager().stopSkilling();
				player.getPriceChecker().open();
			} else {
				player.getPacketSender().sendMessage("You cannot open this right now.");
			}
			break;
		case 2735:
		case 1511:
			if(player.getSummoning().getBeastOfBurden() != null) {
				player.getSummoning().toInventory();
				player.getPacketSender().sendInterfaceRemoval();
			} else {
				player.getPacketSender().sendMessage("You do not have a familiar who can hold items.");
			}
			break;
		case -11501:
		case -11504:
		case -11498:
		case -11507:
		case 1020:
		case 1021:
		case 1019:
		case 1018:
			if(id == 1020 || id == -11504)
				SummoningTab.renewFamiliar(player);
			else if(id == 1019 || id == -11501)
				SummoningTab.callFollower(player);
			else if(id == 1021 || id == -11498)
				SummoningTab.handleDismiss(player, false);
			else if(id == -11507)
				player.getSummoning().store();
			else if(id == 1018) 
				player.getSummoning().toInventory();
			break;
		case 11004:
			player.setDialogueActionId(140);
			DialogueManager.start(player, 140);
			break;
		case 8654:
		case 8657:
		case 8655:
		case 8663:
		case 8669:
		case 8660:
		case 2799:
		case 2798:
		case 1747:
		case 1748:
		case 8890:
		case 8886:
		case 8875:
		case 8871:
		case 8894:
			ChatboxInterfaceSkillAction.handleChatboxInterfaceButtons(player, id);
			break;
		case 14873:
		case 14874:
		case 14875:
		case 14876:
		case 14877:
		case 14878:
		case 14879:
		case 14880:
		case 14881:
		case 14882:
			BankPin.clickedButton(player, id);
			break;
		case 27005:
		case 22012:
			if(!player.isBanking() || player.getInterfaceId() != 5292)
				return;
			Bank.depositItems(player, id == 27005 ? player.getEquipment() : player.getInventory(), false);
			break;
		case 27023:
			if(!player.isBanking() || player.getInterfaceId() != 5292)
				return;
			if(player.getSummoning().getBeastOfBurden() == null) {
				player.getPacketSender().sendMessage("You do not have a familiar which can hold items.");
				return;
			}
			Bank.depositItems(player, player.getSummoning().getBeastOfBurden(), false);
			break;
		case 22008:
			if(!player.isBanking() || player.getInterfaceId() != 5292)
				return;
			player.setNoteWithdrawal(!player.withdrawAsNote());
			break;
		case 21000:
			if(!player.isBanking() || player.getInterfaceId() != 5292)
				return;
			player.getPacketSender().sendConfig(304, player.swapMode() ? 0 : 1);
			player.setSwapMode(!player.swapMode());
			break;
		case 27009:
			MoneyPouch.toBank(player);
			break;
		case 27014:
		case 27015:
		case 27016:
		case 27017:
		case 27018:
		case 27019:
		case 27020:
		case 27021:
		case 27022:
			if(!player.isBanking())
				return;
			if(player.getBankSearchingAttribtues().isSearchingBank())
				BankSearchAttributes.stopSearch(player, true);
			int bankId = id - 27014;
			boolean empty = bankId > 0 ? Bank.isEmpty(player.getBank(bankId)) : false;
			if(!empty || bankId == 0) {
				player.setCurrentBankTab(bankId);
				player.getPacketSender().sendString(5385, "scrollreset");
				player.getPacketSender().sendString(27002, Integer.toString(player.getCurrentBankTab()));
				player.getPacketSender().sendString(27000, "1");
				player.getBank(bankId).open();
			} else
				player.getPacketSender().sendMessage("To create a new tab, please drag an item here.");	
			break;
		case 22004:
			if(!player.isBanking())
				return;
			if(!player.getBankSearchingAttribtues().isSearchingBank()) {
				player.getBankSearchingAttribtues().setSearchingBank(true);
				player.setInputHandling(new EnterSyntaxToBankSearchFor());
				player.getPacketSender().sendEnterInputPrompt("What would you like to search for?");
			} else {
				BankSearchAttributes.stopSearch(player, true);
			}
			break;
		case 22845:
		case 24115:
		case 24010:
		case 24041:
		case 150:
			player.setAutoRetaliate(!player.isAutoRetaliate());
			break;
		case 29332:
			ClanChat clan = player.getCurrentClanChat();
			if (clan == null) {
				player.getPacketSender().sendMessage("You are not in a clanchat channel.");
				return;
			}
			ClanChatManager.leave(player, false);
			player.setClanChatName(null);
			break;
		case 29329:
			if(player.getInterfaceId() > 0) {
				player.getPacketSender().sendMessage("Please close the interface you have open before opening another one.");
				return;
			}
			player.setInputHandling(new EnterClanChatToJoin());
			player.getPacketSender().sendEnterInputPrompt("Enter the name of the clanchat channel you wish to join:");
			break;
		case 19158:
		case 152:
			if(player.getRunEnergy() <= 1) {
				player.getPacketSender().sendMessage("You do not have enough energy to do this.");
				player.setRunning(false);
			} else
				player.setRunning(!player.isRunning());
			player.getPacketSender().sendRunStatus();
			break;
		case 27658:
			player.setExperienceLocked(!player.experienceLocked());
			String type = player.experienceLocked() ? "locked" : "unlocked";
			player.getPacketSender().sendMessage("Your experience is now "+type+".");
			PlayerPanel.refreshPanel(player);
			break;
		case 27651:
		case 21341:
			if(player.getInterfaceId() == -1) {
				player.getSkillManager().stopSkilling();
				BonusManager.update(player);
				player.getPacketSender().sendInterface(21172);
			} else 
				player.getPacketSender().sendMessage("Please close the interface you have open before doing this.");
			break;
		case 27654:
			if(player.getInterfaceId() > 0) {
				player.getPacketSender().sendMessage("Please close the interface you have open before opening another one.");
				return;
			}
			player.getSkillManager().stopSkilling();
			ItemsKeptOnDeath.sendInterface(player);
			break;
		case 2458: //Logout
			if(player.logout()) {
				World.getPlayers().remove(player);
			}
			break;
		case 10003:
		case 29138:
		case 29038:
		case 29063:
		case 29113:
		case 29163:
		case 29188:
		case 29213:
		case 29238:
		case 30007:
		case 48023:
		case 33033:
		case 30108:
		case 7473:
		case 7562:
		case 7487:
		case 7788:
		case 8481:
		case 7612:
		case 7587:
		case 7662:
		case 7462:
		case 7548:
		case 7687:
		case 7537:
		case 12322:
		case 7637:
		case 12311:
			CombatSpecial.activate(player);
			break;
		case 1772: // shortbow & longbow
			if (player.getWeapon() == WeaponInterface.SHORTBOW) {
				player.setFightType(FightType.SHORTBOW_ACCURATE);
			} else if (player.getWeapon() == WeaponInterface.LONGBOW) {
				player.setFightType(FightType.LONGBOW_ACCURATE);
			} else if (player.getWeapon() == WeaponInterface.CROSSBOW) {
				player.setFightType(FightType.CROSSBOW_ACCURATE);
			}
			break;
		case 1771:
			if (player.getWeapon() == WeaponInterface.SHORTBOW) {
				player.setFightType(FightType.SHORTBOW_RAPID);
			} else if (player.getWeapon() == WeaponInterface.LONGBOW) {
				player.setFightType(FightType.LONGBOW_RAPID);
			} else if (player.getWeapon() == WeaponInterface.CROSSBOW) {
				player.setFightType(FightType.CROSSBOW_RAPID);
			}
			break;
		case 1770:
			if (player.getWeapon() == WeaponInterface.SHORTBOW) {
				player.setFightType(FightType.SHORTBOW_LONGRANGE);
			} else if (player.getWeapon() == WeaponInterface.LONGBOW) {
				player.setFightType(FightType.LONGBOW_LONGRANGE);
			} else if (player.getWeapon() == WeaponInterface.CROSSBOW) {
				player.setFightType(FightType.CROSSBOW_LONGRANGE);
			}
			break;
		case 2282: // dagger & sword
			if (player.getWeapon() == WeaponInterface.DAGGER) {
				player.setFightType(FightType.DAGGER_STAB);
			} else if (player.getWeapon() == WeaponInterface.SWORD) {
				player.setFightType(FightType.SWORD_STAB);
			}
			break;
		case 2285:
			if (player.getWeapon() == WeaponInterface.DAGGER) {
				player.setFightType(FightType.DAGGER_LUNGE);
			} else if (player.getWeapon() == WeaponInterface.SWORD) {
				player.setFightType(FightType.SWORD_LUNGE);
			}
			break;
		case 2284:
			if (player.getWeapon() == WeaponInterface.DAGGER) {
				player.setFightType(FightType.DAGGER_SLASH);
			} else if (player.getWeapon() == WeaponInterface.SWORD) {
				player.setFightType(FightType.SWORD_SLASH);
			}
			break;
		case 2283:
			if (player.getWeapon() == WeaponInterface.DAGGER) {
				player.setFightType(FightType.DAGGER_BLOCK);
			} else if (player.getWeapon() == WeaponInterface.SWORD) {
				player.setFightType(FightType.SWORD_BLOCK);
			}
			break;
		case 2429: // scimitar & longsword
			if (player.getWeapon() == WeaponInterface.SCIMITAR) {
				player.setFightType(FightType.SCIMITAR_CHOP);
			} else if (player.getWeapon() == WeaponInterface.LONGSWORD) {
				player.setFightType(FightType.LONGSWORD_CHOP);
			}
			break;
		case 2432:
			if (player.getWeapon() == WeaponInterface.SCIMITAR) {
				player.setFightType(FightType.SCIMITAR_SLASH);
			} else if (player.getWeapon() == WeaponInterface.LONGSWORD) {
				player.setFightType(FightType.LONGSWORD_SLASH);
			}
			break;
		case 2431:
			if (player.getWeapon() == WeaponInterface.SCIMITAR) {
				player.setFightType(FightType.SCIMITAR_LUNGE);
			} else if (player.getWeapon() == WeaponInterface.LONGSWORD) {
				player.setFightType(FightType.LONGSWORD_LUNGE);
			}
			break;
		case 2430:
			if (player.getWeapon() == WeaponInterface.SCIMITAR) {
				player.setFightType(FightType.SCIMITAR_BLOCK);
			} else if (player.getWeapon() == WeaponInterface.LONGSWORD) {
				player.setFightType(FightType.LONGSWORD_BLOCK);
			}
			break;
		case 3802: // mace
			player.setFightType(FightType.MACE_POUND);
			break;
		case 3805:
			player.setFightType(FightType.MACE_PUMMEL);
			break;
		case 3804:
			player.setFightType(FightType.MACE_SPIKE);
			break;
		case 3803:
			player.setFightType(FightType.MACE_BLOCK);
			break;
		case 4454: // knife, thrownaxe, dart & javelin
			if (player.getWeapon() == WeaponInterface.KNIFE) {
				player.setFightType(FightType.KNIFE_ACCURATE);
			} else if (player.getWeapon() == WeaponInterface.THROWNAXE) {
				player.setFightType(FightType.THROWNAXE_ACCURATE);
			} else if (player.getWeapon() == WeaponInterface.DART) {
				player.setFightType(FightType.DART_ACCURATE);
			} else if (player.getWeapon() == WeaponInterface.JAVELIN) {
				player.setFightType(FightType.JAVELIN_ACCURATE);
			}
			break;
		case 4453:
			if (player.getWeapon() == WeaponInterface.KNIFE) {
				player.setFightType(FightType.KNIFE_RAPID);
			} else if (player.getWeapon() == WeaponInterface.THROWNAXE) {
				player.setFightType(FightType.THROWNAXE_RAPID);
			} else if (player.getWeapon() == WeaponInterface.DART) {
				player.setFightType(FightType.DART_RAPID);
			} else if (player.getWeapon() == WeaponInterface.JAVELIN) {
				player.setFightType(FightType.JAVELIN_RAPID);
			}
			break;
		case 4452:
			if (player.getWeapon() == WeaponInterface.KNIFE) {
				player.setFightType(FightType.KNIFE_LONGRANGE);
			} else if (player.getWeapon() == WeaponInterface.THROWNAXE) {
				player.setFightType(FightType.THROWNAXE_LONGRANGE);
			} else if (player.getWeapon() == WeaponInterface.DART) {
				player.setFightType(FightType.DART_LONGRANGE);
			} else if (player.getWeapon() == WeaponInterface.JAVELIN) {
				player.setFightType(FightType.JAVELIN_LONGRANGE);
			}
			break;
		case 4685: // spear
			player.setFightType(FightType.SPEAR_LUNGE);
			break;
		case 4688:
			player.setFightType(FightType.SPEAR_SWIPE);
			break;
		case 4687:
			player.setFightType(FightType.SPEAR_POUND);
			break;
		case 4686:
			player.setFightType(FightType.SPEAR_BLOCK);
			break;
		case 4711: // 2h sword
			player.setFightType(FightType.TWOHANDEDSWORD_CHOP);
			break;
		case 4714:
			player.setFightType(FightType.TWOHANDEDSWORD_SLASH);
			break;
		case 4713:
			player.setFightType(FightType.TWOHANDEDSWORD_SMASH);
			break;
		case 4712:
			player.setFightType(FightType.TWOHANDEDSWORD_BLOCK);
			break;
		case 5576: // pickaxe
			player.setFightType(FightType.PICKAXE_SPIKE);
			break;
		case 5579:
			player.setFightType(FightType.PICKAXE_IMPALE);
			break;
		case 5578:
			player.setFightType(FightType.PICKAXE_SMASH);
			break;
		case 5577:
			player.setFightType(FightType.PICKAXE_BLOCK);
			break;
		case 7768: // claws
			player.setFightType(FightType.CLAWS_CHOP);
			break;
		case 7771:
			player.setFightType(FightType.CLAWS_SLASH);
			break;
		case 7770:
			player.setFightType(FightType.CLAWS_LUNGE);
			break;
		case 7769:
			player.setFightType(FightType.CLAWS_BLOCK);
			break;
		case 8466: // halberd
			player.setFightType(FightType.HALBERD_JAB);
			break;
		case 8468:
			player.setFightType(FightType.HALBERD_SWIPE);
			break;
		case 8467:
			player.setFightType(FightType.HALBERD_FEND);
			break;
		case 5862: // unarmed
			player.setFightType(FightType.UNARMED_PUNCH);
			break;
		case 5861:
			player.setFightType(FightType.UNARMED_KICK);
			break;
		case 5860:
			player.setFightType(FightType.UNARMED_BLOCK);
			break;
		case 12298: // whip
			player.setFightType(FightType.WHIP_FLICK);
			break;
		case 12297:
			player.setFightType(FightType.WHIP_LASH);
			break;
		case 12296:
			player.setFightType(FightType.WHIP_DEFLECT);
			break;
		case 336: // staff
			player.setFightType(FightType.STAFF_BASH);
			break;
		case 335:
			player.setFightType(FightType.STAFF_POUND);
			break;
		case 334:
			player.setFightType(FightType.STAFF_FOCUS);
			break;
		case 433: // warhammer
			player.setFightType(FightType.WARHAMMER_POUND);
			break;
		case 432:
			player.setFightType(FightType.WARHAMMER_PUMMEL);
			break;
		case 431:
			player.setFightType(FightType.WARHAMMER_BLOCK);
			break;
		case 782: // scythe
			player.setFightType(FightType.SCYTHE_REAP);
			break;
		case 784:
			player.setFightType(FightType.SCYTHE_CHOP);
			break;
		case 785:
			player.setFightType(FightType.SCYTHE_JAB);
			break;
		case 783:
			player.setFightType(FightType.SCYTHE_BLOCK);
			break;
		case 1704: // battle axe
			player.setFightType(FightType.BATTLEAXE_CHOP);
			break;
		case 1707:
			player.setFightType(FightType.BATTLEAXE_HACK);
			break;
		case 1706:
			player.setFightType(FightType.BATTLEAXE_SMASH);
			break;
		case 1705:
			player.setFightType(FightType.BATTLEAXE_BLOCK);
			break;
		}
	}

	private boolean checkHandlers(Player player, int id) {
		if(Construction.handleButtonClick(id, player)) {
			return true;
		}
		switch(id) {
		case 2494:
		case 2495:
		case 2496:
		case 2497:
		case 2498:
		case 2471:
		case 2472:
		case 2473:
		case 2461:
		case 2462:
		case 2482:
		case 2483:
		case 2484:
		case 2485:
			DialogueOptions.handle(player, id);
			return true;
		}
		if(player.isPlayerLocked() && id != 2458) {
			return true;
		}
		if(Achievements.handleButton(player, id)) {
			return true;
		}
		if(Sounds.handleButton(player, id)) {
			return true;
		}
		if (PrayerHandler.isButton(id)) {
			PrayerHandler.togglePrayerWithActionButton(player, id);
			return true;
		}
		if (CurseHandler.isButton(player, id)) {
			return true;
		}
		if(Autocasting.handleAutocast(player, id)) {
			return true;
		}
		if(SmithingData.handleButtons(player, id)) {
			return true;
		}
		if(PouchMaking.pouchInterface(player, id)) {
			return true;
		}
		if(LoyaltyProgramme.handleButton(player, id)) {
			return true;
		}
		if(Fletching.fletchingButton(player, id)) {
			return true;
		}
		if(LeatherMaking.handleButton(player, id) || Tanning.handleButton(player, id)) {
			return true;
		}
		if(Emotes.doEmote(player, id)) {
			return true;
		}
		if(PestControl.handleInterface(player, id)) {
			return true;
		}
		if(player.getLocation() == Location.DUEL_ARENA && Dueling.handleDuelingButtons(player, id)) {
			return true;
		}
		if(Slayer.handleRewardsInterface(player, id)) {
			return true;
		}
		if(ExperienceLamps.handleButton(player, id)) {
			return true;
		}
		if(PlayersOnlineInterface.handleButton(player, id)) {
			return true;
		}
		if(GrandExchange.handleButton(player, id)) {
			return true;
		}
		if(ClanChatManager.handleClanChatSetupButton(player, id)) {
			return true;
		}
		return false;
	}
	
	public static final int OPCODE = 185;
}
