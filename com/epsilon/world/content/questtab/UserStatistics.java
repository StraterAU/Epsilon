package com.epsilon.world.content.questtab;

import com.epsilon.util.Misc;
import com.epsilon.world.World;
import com.epsilon.world.content.ShootingStar;
import com.epsilon.world.content.WellOfGoodwill;
import com.epsilon.world.content.skill.impl.slayer.SlayerTasks;
import com.epsilon.world.entity.impl.player.Player;

public class UserStatistics {
	
	public static void display(Player player) {
		player.getPacketSender().sendString(50007, "User Statistics");
		boolean expLock = player.experienceLocked();
		Player.serverInformation = false;
		Player.userStatistics = true;
		Player.dailyTasks = false;
		Player.links = false;
		for (int i = 50051; i <= 50071; i++) {
			player.getPacketSender().sendString(i, "");
		}
		player.getPacketSender().sendString(50051, "@or1@Time Played:  @whi@"+Misc.getTimePlayed((player.getTotalPlayTime() + player.getRecordedLogin().elapsed())));
		player.getPacketSender().sendString(50052, "@or1@Rank: @whi@" + Misc.formatText(player.getHighestRights().name().toLowerCase()));
		player.getPacketSender().sendString(50053, "@or1@Achievement Points: @whi@" + player.getPointsHandler().getAchievementPoints());
		player.getPacketSender().sendString(50054, "@or1@Total Donated: @whi@" + player.getAmountDonated());
		String task = player.getSlayer().getSlayerTask() != SlayerTasks.NO_TASK ? Misc.formatText(player.getSlayer().getSlayerTask().toString().toLowerCase().replaceAll("_", " ")) : "None";
		
		if (!task.isEmpty() && !task.equalsIgnoreCase("none")) {
			String[] args = task.split(" ");
			if (args.length > 1) {
				String temp = "";
				for (String s : args) {
					int length = s.length();
					temp += s.substring(0, args.length == 2 ? (5 > length ? length : 5) : (4 > length ? length : 4));
					temp += " ";
				}
				task = temp;
			}
		}
		
		player.getPacketSender().sendString(50055, "@or1@Task: @yel@" + task + "@whi@ x @yel@" + player.getSlayer().getAmountToSlay());
		
		player.getPacketSender().sendString(50056, "@or1@Slayer Points: @whi@" + player.getPointsHandler().getSlayerPoints());
		player.getPacketSender().sendString(50057, "@or1@Pest Control Points: @whi@" + player.getPointsHandler().getCommendations() + " ");
		player.getPacketSender().sendString(50058, "@or1@Vote Points: @whi@" + player.getPointsHandler().getVotingPoints() + " ");
		player.getPacketSender().sendString(50059, "@or1@Loyalty Points: @whi@" + player.getPointsHandler().getLoyaltyPoints());
		player.getPacketSender().sendString(50060, "@or1@Trivia Points: @whi@" + player.getPointsHandler().getTriviaPoints()+ " ");
		player.getPacketSender().sendString(50061, "@or1@PvP Points: @whi@" + player.getPointsHandler().getPkPoints());
		int val = Math.min(1, 0);
		player.getPacketSender().sendString(50062, "@or1@Ep: @whi@" + val);
		player.getPacketSender().sendString(50063, "@or1@Kills: @whi@" + player.getPlayerKillingAttributes().getPlayerKills());
		player.getPacketSender().sendString(50064, "@or1@Deaths: @whi@" + player.getPlayerKillingAttributes().getPlayerDeaths());
		player.getPacketSender().sendString(50065, "@or1@K/D Ratio: @whi@" + player.getPlayerKillingAttributes().getKDR());
		player.getPacketSender().sendString(50066, "@or1@Killstreak: @whi@" + player.getPlayerKillingAttributes().getPlayerKillStreak());
		player.getPacketSender().sendString(50067, "@or1@Open Kills Tracker@or1@");
		player.getPacketSender().sendString(50068, "@or1@Open Drop Log@or1@");
	}

}
