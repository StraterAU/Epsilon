package com.epsilon.world.content.combat.pvp;

import java.util.ArrayList;
import java.util.List;

import com.epsilon.model.Locations.Location;
import com.epsilon.util.Misc;
import com.epsilon.world.content.Achievements;
import com.epsilon.world.content.Artifacts;
import com.epsilon.world.content.LoyaltyProgramme;
import com.epsilon.world.content.Scoreboards;
import com.epsilon.world.content.Achievements.AchievementData;
import com.epsilon.world.content.LoyaltyProgramme.LoyaltyTitles;
import com.epsilon.world.entity.impl.player.Player;

public class PlayerKillingAttributes {

	private final Player player;
	private Player target;
	private int playerKills;
	private int playerKillStreak;
	private int playerDeaths;
	private int targetPercentage;
	private long lastPercentageIncrease;
	private int safeTimer;

	private final int WAIT_LIMIT = 2;
	private List<String> killedPlayers = new ArrayList<String>();

	public PlayerKillingAttributes(Player player) {
		this.player = player;
	}
	
	public double getKDR() {
		double ratio = playerKills / Math.max(1D, playerDeaths);
		return ratio;
	}

	public void add(Player other) {

		if(other.getAppearance().getBountyHunterSkull() >= 0)
			other.getAppearance().setBountyHunterSkull(-1);

		boolean target = player.getPlayerKillingAttributes().getTarget() != null && player.getPlayerKillingAttributes().getTarget().getIndex() == other.getIndex() || other.getPlayerKillingAttributes().getTarget() != null && other.getPlayerKillingAttributes().getTarget().getIndex() == player.getIndex();
		if(target)
			killedPlayers.clear();
		
		//System.out.println("killers: "+killedPlayers.size());

		if (killedPlayers.size() >= WAIT_LIMIT) {
			killedPlayers.clear();
			//System.out.println("test4");
			handleReward(other, target);
		} else {
			//System.out.println("test5");
			if (!killedPlayers.contains(other.getUsername())) {
				handleReward(other, target);
				//System.out.println("test6");
			} else
				player.getPacketSender().sendMessage("You were not given points because you have recently defeated " + other.getUsername() + ".");
		}
		
		//System.out.println("test7");

		if(target)
			BountyHunter.resetTargets(player, other, true, "You have defeated your target!");
	}

	/**
	 * Gives the player a reward for defeating his opponent
	 * @param other
	 */
	private void handleReward(Player o, boolean targetKilled) {
		//System.out.println("test8 - "+o.getUsername()+" - "+player.getUsername()+" - "+o.getHostAddress()+" - "+player.getHostAddress());
		if (!player.getHostAddress().equalsIgnoreCase(o.getHostAddress()) && player.getLocation() == Location.WILDERNESS) {
			//System.out.println("test9");
			if(!killedPlayers.contains(o.getUsername()))
				killedPlayers.add(o.getUsername());
			player.getPacketSender().sendMessage(getRandomKillMessage(o.getUsername()));
			player.getPointsHandler().setPkPoints(1, true);
			this.playerKills += 1;
			this.playerKillStreak +=1;
			player.getPacketSender().sendMessage("You've received a Pk point.");
			Artifacts.handleDrops(player, o, targetKilled);
			if(player.getAppearance().getBountyHunterSkull() < 4)
				player.getAppearance().setBountyHunterSkull(player.getAppearance().getBountyHunterSkull()+1);
			player.getPointsHandler().refreshPanel();
			
			/** ACHIEVEMENTS AND LOYALTY TITLES **/
			LoyaltyProgramme.unlock(player, LoyaltyTitles.KILLER);
			Achievements.doProgress(player, AchievementData.DEFEAT_10_PLAYERS);
			Achievements.doProgress(player, AchievementData.DEFEAT_30_PLAYERS);
			if(this.playerKills >= 15) {
				LoyaltyProgramme.unlock(player, LoyaltyTitles.SLAUGHTERER);
			} else if(this.playerKills >= 50) {
				LoyaltyProgramme.unlock(player, LoyaltyTitles.GENOCIDAL);
			}
			if(this.playerKillStreak >= 3) {
				Achievements.finishAchievement(player, AchievementData.REACH_A_KILLSTREAK_OF_3);
			}
			if(this.playerKillStreak >= 6) {
				Achievements.finishAchievement(player, AchievementData.REACH_A_KILLSTREAK_OF_6);
			}
			if(this.playerKillStreak >= 15) {
				LoyaltyProgramme.unlock(player, LoyaltyTitles.IMMORTAL);
			}
		}
	}

	public List<String> getKilledPlayers() {
		return killedPlayers;
	}

	public void setKilledPlayers(List<String> list) {
		killedPlayers = list;
	}

	/**
	 * Gets a random message after killing a player
	 * @param killedPlayer 		The player that was killed
	 */
	public static String getRandomKillMessage(String killedPlayer){
		int deathMsgs = Misc.getRandom(8);
		switch(deathMsgs) {
		case 0: return "With a crushing blow, you defeat "+killedPlayer+".";
		case 1: return "It's humiliating defeat for "+killedPlayer+".";
		case 2: return ""+killedPlayer+" didn't stand a chance against you.";
		case 3: return "You've defeated "+killedPlayer+".";
		case 4: return ""+killedPlayer+" regrets the day they met you in combat.";
		case 5: return "It's all over for "+killedPlayer+".";
		case 6: return ""+killedPlayer+" falls before you might.";
		case 7: return "Can anyone defeat you? Certainly not "+killedPlayer+".";
		case 8: return "You were clearly a better fighter than "+killedPlayer+".";
		}
		return null;
	}

	public int getPlayerKills() {
		return playerKills;
	}

	public void setPlayerKills(int playerKills) {
		this.playerKills = playerKills;
	}

	public int getPlayerKillStreak() {
		return playerKillStreak;
	}

	public void setPlayerKillStreak(int playerKillStreak) {
		this.playerKillStreak = playerKillStreak;
	}

	public int getPlayerDeaths() {
		return playerDeaths;
	}

	public void setPlayerDeaths(int playerDeaths) {
		this.playerDeaths = playerDeaths;
	}

	public Player getTarget() {
		return target;
	}

	public void setTarget(Player target) {
		this.target = target;
	}

	public int getTargetPercentage() {
		return targetPercentage;
	}

	public void setTargetPercentage(int targetPercentage) {
		this.targetPercentage = targetPercentage;
	}

	public long getLastTargetPercentageIncrease() {
		return lastPercentageIncrease;
	}

	public void setLastTargetPercentageIncrease(long lastPercentageIncrease) {
		this.lastPercentageIncrease = lastPercentageIncrease;
	}

	public int getSafeTimer() {
		return safeTimer;
	}

	public void setSafeTimer(int safeTimer) {
		this.safeTimer = safeTimer;
	}
}
