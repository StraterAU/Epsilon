package com.epsilon.clantourn;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.epsilon.engine.task.Task;
import com.epsilon.engine.task.TaskManager;
import com.epsilon.model.Flag;
import com.epsilon.model.Item;
import com.epsilon.model.Position;
import com.epsilon.model.Locations.Location;
import com.epsilon.model.container.impl.Bank;
import com.epsilon.model.container.impl.Equipment;
import com.epsilon.world.World;
import com.epsilon.world.content.clan.ClanChat;
import com.epsilon.world.entity.impl.player.Player;

public final class ClanTournamentManager {

	/**
	 * The current {@link ClanTournament}
	 */
	public static ClanTournament tournament;
	
	
	static Item[] RANDOM_ITEMS = new Item[] {
		new Item(995, 50000000), //250M coins
	};

	/**
	 * Attempts to start a tournament
	 * 
	 * @param tournament
	 *            The {@link ClanTournament} object
	 */
	public static void attemptStart(ClanTournament tournament) {
		if (tournament.getClans().size() < ClanTournamentConstants.MINIMUM_SIZE) {
			System.out.println("Size not met: " + tournament.getClans().size());
			return;
		}
		if(ClanTournamentManager.tournament != null) {
			return;
		}
		ClanTournamentManager.tournament = tournament;
		TaskManager.submit(new Task(2, false) {
			int timer = ClanTournamentConstants.WAITING_TIME;

			@Override
			public void execute() {
				tournament.setState(State.START_COUNTDOWN);
				timer--;
				sendMessage("Timer: " + timer);
				if (tournament.getClans().size() < ClanTournamentConstants.MINIMUM_SIZE) {
					sendMessage("Ending countdown as a clan has left and the minimum requirements aren't met");
					tournament.reset();
					stop();
					return;
				}
				if (timer == 0) {
					begin();
					stop();
				} else if (timer % 10 == 0) {
					sendMessage(timer + " seconds till the game starts");
				}
			}
		});
	}

	/**
	 * Begins the game
	 */
	private static void begin() {
		int amount = tournament.getClans().size();
		tournament.setState(State.IN_PLAY);
		List<ClanTournamentTeam> locs = ClanTournamentTeam.LIST.subList(0, amount);
		Collections.shuffle(locs);	
		for (ClanChat clan : tournament.getClans()) {
			ClanTournamentTeam team = ClanTournamentTeam.BLUE;// clan.
			while (tournament.getTeams().containsKey(team)) {
				team = ClanTournamentTeam.random();
			}
			System.out.println("Clan " + clan.getOwnerName() + " is now team " + team.name());
			tournament.getTeams().put(team, clan);
		}
		for (Map.Entry<ClanTournamentTeam, ClanChat> entry : tournament.getTeams().entrySet()) {
			ClanTournamentTeam key = entry.getKey();
			ClanChat clan = entry.getValue();
			
			for (Player player : clan.getMembers()) {
				clan.getTournamentMembers().add(player);
				player.moveTo(key.getTeleport());
				giveGear(player, key.getCapeId());
			}
		}
		sendMessage("The tournament has begun! Good luck!");
	}

	private static void giveGear(Player player, int capeId) {
		if (player.getEquipment().get(Equipment.CAPE_SLOT) != null && player.getEquipment().get(Equipment.CAPE_SLOT).getId() != -1) {
			Item cape = player.getEquipment().get(Equipment.CAPE_SLOT);
			player.getBank(Bank.getTabForItem(player, cape.getId())).add(cape);
			player.getEquipment().delete(cape);
			player.sendMessage("Your previous cape has been banked.");
		}
		player.getEquipment().set(Equipment.CAPE_SLOT, new Item(capeId));
		player.getEquipment().refreshItems();
		player.getUpdateFlag().flag(Flag.APPEARANCE);
		World.sendMessage("" + player.getUsername() + " has gotten loot from the Christmas tree do ::hint to find next location");
	}
	
	public static void remove(Player player) {
		player.getEquipment().set(Equipment.CAPE_SLOT, new Item(-1, 0));
		player.getEquipment().refreshItems();
		player.getUpdateFlag().flag(Flag.APPEARANCE);
		ClanChat clan = null;
		ClanTournamentTeam l = null;
		for (Map.Entry<ClanTournamentTeam, ClanChat> entry : tournament.getTeams().entrySet()) {
			ClanTournamentTeam key = entry.getKey();
			ClanChat value = entry.getValue();
			for (Player p : value.getMembers()) {
				if (p.getUsername().equalsIgnoreCase(player.getUsername())) {
					clan = value;
					l = key;
				}
			}
		}
		ClanChat playersClan = clan;
		ClanTournamentTeam loc = l;
		if (playersClan != null) {
			if (playersClan.getMembers().size() == 1 || playersClan.getMembers().isEmpty()) {
				tournament.getTeams().values().forEach(c -> c.getMembers().forEach(p -> p.sendMessage(playersClan.getOwnerName() + "'s clan has been removed as they do not have any players left!")));
				clan.getMembers().forEach(p -> p.moveTo(ClanTournamentConstants.REMOVAL_POSITION));
				tournament.getClans().remove(clan);
				tournament.getTeams().remove(loc);
				clan.getMembers().forEach(p -> p.getEquipment().delete(new Item(loc.getCapeId(), 1)));
				if (tournament.getTeams().size() == 1) {
					String name = tournament.getClans().get(0).getOwnerName();
					ClanChat winner =  tournament.getClans().get(0);
					//System.err.println("name=" + name);
					
					handleSecond(clan);
					handleWinner(winner);
				} else {
					handleLoss(clan);
				}
			}
		}
	}
	
	private static void handleLoss(ClanChat clan) {
		clan.getMembers().forEach(player -> {
			player.sendMessage("You have been knocked out of the tournament!");
		});
	}
	
	private static void handleSecond(ClanChat clan) {
		/*clan.getMembers().forEach(player -> {
			player.sendMessage("You have finished second!");
			player.getPointsHandler().setPkPoints(1, true);
			player.sendMessage("You have been awarded 25 points!");
		});*/
	}

	private static void handleWinner(ClanChat clan) {
		
		sendMessage(clan.getOwnerName() + "'s clan has won!");

		Random r = new Random();
		clan.getMembers().forEach(player -> {
			player.getEquipment().set(Equipment.CAPE_SLOT, new Item(-1, 0));
			player.getEquipment().refreshItems();
			player.getUpdateFlag().flag(Flag.APPEARANCE);
			player.sendMessage("Congratulations, you have won!");
			player.getPointsHandler().setPkPoints(1, true);
			player.sendMessage("You have been awarded 1 points!");
			player.moveTo(ClanTournamentConstants.REMOVAL_POSITION);
			if (r.nextInt(100) == 12) { //1 in 100 chance
				Item item = RANDOM_ITEMS[r.nextInt(RANDOM_ITEMS.length)];
				player.getBank(Bank.getTabForItem(player, item.getId())).add(item);
				player.sendMessage("You have been lucky and are awarded " + item.getAmount() + "x " + item.getDefinition().getName() + "!");
			}
		});
		clan.addWin();
		World.sendMessage(clan.getOwnerName() + "'s clan has won the tournament!");
		ClanTournament.INSTANCE = new ClanTournament();
		tournament = null;
		
		
	}

	public static void death(Player victim) {
		if(victim.getCurrentClanChat() == null || !victim.getCurrentClanChat().getTournamentMembers().contains(victim)) {
			return;
		}
		sendMessage(victim.getUsername()+" has been finished off.");
		remove(victim);
	}
	
	protected static void sendMessage(String string) {
		tournament.getClans().forEach(chat -> chat.getMembers().forEach(player -> player.sendMessage(string)));
	}
}
