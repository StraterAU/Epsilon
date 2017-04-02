package com.epsilon.world.content;

import java.util.Date;

import com.epsilon.world.entity.impl.player.Player;
import com.epsilon.world.entity.impl.player.PlayerHandler;

public class PointsHandler {

    private Player p;

    public PointsHandler(Player p) {
        this.p = p;
    }

    public void reset() {
        dungTokens = commendations = (int) (loyaltyPoints = votingPoints = slayerPoints = pkPoints = 0);
        p.getPlayerKillingAttributes().setPlayerKillStreak(0);
        p.getPlayerKillingAttributes().setPlayerKills(0);
        p.getPlayerKillingAttributes().setPlayerDeaths(0);
        p.getDueling().arenaStats[0] = p.getDueling().arenaStats[1] = 0;
    }

    public PointsHandler refreshPanel() {
    	PlayerPanel.refreshPanel(p);
        return this;
    }

    private int prestigePoints;
    private int slayerPoints;
    private int commendations;
    private int dungTokens;
    private int pkPoints;
    private double loyaltyPoints;
    private int votingPoints;
    private int achievementPoints;
	private int ironManPoints;
    private int triviaPoints;
	public String dateJoined;
	private int refeerFriendPoints;
	public int getRefeerFriendPoints() {
		return refeerFriendPoints;
	}

	public void setRefeerFriendPoints(int refeerFriendPoints, boolean add) {
		if(add)
		this.refeerFriendPoints += refeerFriendPoints;
		else
		this.refeerFriendPoints = refeerFriendPoints;
	}
    public String getDateJoined() {
    	return dateJoined;
    }
    public void setDateJoined(String dateJoined) {
    	dateJoined = this.dateJoined;
    }
    
    public int getIronManPoints() {
		return ironManPoints;
	}

	public void setIronManPoints(int ironManPoints, boolean add) {
		if(add){
		this.ironManPoints += ironManPoints;
		}else{
			this.ironManPoints = ironManPoints;
		}
	}


    public int getPrestigePoints() {
        return prestigePoints;
    }

    public void setPrestigePoints(int points, boolean add) {
        if (add) {
            this.prestigePoints += points;
        } else {
            this.prestigePoints = points;
        }
    }

    public int getSlayerPoints() {
        return slayerPoints;
    }

    public void setSlayerPoints(int slayerPoints, boolean add) {
        if (add) {
            this.slayerPoints += slayerPoints;
        } else {
            this.slayerPoints = slayerPoints;
        }
    }

    public int getCommendations() {
        return this.commendations;
    }

    public void setCommendations(int commendations, boolean add) {
        if (add) {
            this.commendations += commendations;
        } else {
            this.commendations = commendations;
        }
    }

    public int getLoyaltyPoints() {
        return (int) this.loyaltyPoints;
    }

    public void setLoyaltyPoints(int points, boolean add) {
        if (add) {
            this.loyaltyPoints += points;
        } else {
            this.loyaltyPoints = points;
        }
    }

    public void incrementLoyaltyPoints(double amount) {
        this.loyaltyPoints += amount;
    }

    public int getPkPoints() {
        return this.pkPoints;
    }

    public void setPkPoints(int points, boolean add) {
        if (add) {
            this.pkPoints += points;
        } else {
            this.pkPoints = points;
        }
    }

    public void setTriviaPoints(int triviaPoints) {
        this.triviaPoints = triviaPoints;
    }

    public void setTriviaPoints(int points, boolean add) {
        if (add) {
            this.triviaPoints += points;
        } else {
            this.triviaPoints = points;
        }
    }

    public int getTriviaPoints() {
        return this.triviaPoints;
    }

    public int getDungeoneeringTokens() {
        return dungTokens;
    }

    public void setDungeoneeringTokens(int dungTokens, boolean add) {
        if (add) {
            this.dungTokens += dungTokens;
        } else {
            this.dungTokens = dungTokens;
        }
    }

    public int getVotingPoints() {
        return votingPoints;
    }

    public void setVotingPoints(int votingPoints) {
        this.votingPoints = votingPoints;
    }

    public void incrementVotingPoints() {
        this.votingPoints++;
    }

    public void incrementVotingPoints(int amt) {
        this.votingPoints += amt;
    }

    public void setVotingPoints(int points, boolean add) {
        if (add) {
            this.votingPoints += points;
        } else {
            this.votingPoints = points;
        }
    }

    public int getAchievementPoints() {
        return achievementPoints;
    }

    public void setAchievementPoints(int points, boolean add) {
        if (add) {
            this.achievementPoints += points;
        } else {
            this.achievementPoints = points;
        }
    }
		
}