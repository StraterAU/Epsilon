package com.epsilon.clantourn;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.epsilon.model.Position;

public enum ClanTournamentTeam {
	YELLOW(new Position(1661, 5730, 0), 4335), //North
	PINK(new Position(1686, 5716, 0), 4315), //North east
	RED(new Position(1699, 5695, 0), 4411), //South east
	BLUE(new Position(1661, 5661, 0), 4371), //South
	GREEN(new Position(1627, 5697, 0), 4389), //South west
	CYAN(new Position(1639, 5722, 0), 3759), //North west
	;
	
	public static final List<ClanTournamentTeam> LIST = Arrays.asList(values());
	
	private Position teleport;
	
	private int capeId;
	
	ClanTournamentTeam(Position teleport, int capeId) {
		this.teleport = (teleport);
		this.capeId = (capeId);
	}

	public Position getTeleport() {
		return teleport;
	}

	public int getCapeId() {
		return capeId;
	}

	private static Random rng = new Random();
	
	public static ClanTournamentTeam random() {
		return LIST.get(rng.nextInt(LIST.size()));
	}

}
