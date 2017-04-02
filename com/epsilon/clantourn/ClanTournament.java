package com.epsilon.clantourn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.epsilon.model.Locations.Location;
import com.epsilon.world.World;
import com.epsilon.world.content.clan.ClanChat;
import com.epsilon.world.content.clan.ClanChatManager;
import com.epsilon.world.entity.impl.player.Player;

/**
 * Represents a {@link ClanTournament} object. All {@link #clans} are teleported
 * and set to fight, the winning clan gets a reward.
 * 
 * @author david
 *
 */
public final class ClanTournament {
	
	public static ClanTournament INSTANCE = new ClanTournament();

	/**
	 * The {@link ArrayList} of clans
	 */
	private final List<ClanChat> clans = new ArrayList<ClanChat>();

	private final Map<ClanTournamentTeam, ClanChat> teams = new HashMap<>();

	private State state = State.START_COUNTDOWN;

	public boolean addPlayer(Player player) {
		if (state.equals(State.IN_PLAY)) {
			player.sendMessage("The tournament is currently in play!");
			return false;
		}
		if (player.getCurrentClanChat() == null) {
			player.sendMessage("You must be in a clan to partake in the tournament.");
			return false;
		}
		if (!clans.contains(player.getCurrentClanChat())) {
			int amount = 0;
			for (Player p : player.getCurrentClanChat().getMembers()) {
				if (p.getLocation().equals(Location.FIST_OF_GUTHIX_WAITING_LOBBY)) {
					amount++;
				}
			}
			if (amount < 3) {
				player.sendMessage("You need at least 3 players at the lobby to join into the tournament!");
				return false;
			}
			return addClan(player.getCurrentClanChat());
		} else {
			player.sendMessage("Your clan has already joined.");
		}
		return false;
	}

	public boolean addClan(ClanChat clan) {
		if (ClanTournamentConstants.MAXIMUM_SIZE < clans.size()) { // Too many
			return false;
		}
		if (state.equals(State.IN_PLAY)) {
			return false;
		}
		ClanChat clanChat = ClanChatManager.getClanChatChannel(clan.getOwnerName());
		boolean success = clans.add(clanChat);
		System.out.println("Adding clan: "+clan.getOwnerName());
		System.out.println(clanChat);
		ClanTournamentManager.attemptStart(this);
		if (success) {
			clans.forEach(c -> c.getMembers().forEach(p -> p.sendMessage(clan.getOwnerName() + "'s clan has joined the tournament!")));
		}
		return success;
	}

	public void reset() {
		clans.clear();
		teams.clear();
		for (Player p : World.getPlayers()) {
			if (p.getLocation().equals(Location.FIST_OF_GUTHIX)) {
				p.moveTo(ClanTournamentConstants.REMOVAL_POSITION);
				;
			}
		}
		state = State.START_COUNTDOWN;
	}

	public List<ClanChat> getClans() {
		return clans;
	}

	protected void setState(State state) {
		this.state = state;
	}

	public Map<ClanTournamentTeam, ClanChat> getTeams() {
		return teams;
	}

}
