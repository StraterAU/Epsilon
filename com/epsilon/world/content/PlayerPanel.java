package com.epsilon.world.content;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.epsilon.util.Misc;
import com.epsilon.world.World;
import com.epsilon.world.content.dialogue.DialogueManager;
import com.epsilon.world.content.minigames.impl.Nomad;
import com.epsilon.world.content.minigames.impl.RecipeForDisaster;
import com.epsilon.world.content.skill.impl.slayer.SlayerTasks;
import com.epsilon.world.entity.impl.player.Player;
import com.epsilon.world.entity.impl.player.PlayerHandler;

public class PlayerPanel {
	
	public static final int SLAYER_POINTS = 12;
	public static final int PC_POINTS = 13;
	public static final int EXP_LOCK = 3;
	public static final int PVP_POINTS = 17;
	public static final int KDR = 21;
	public static final int DEATHS = 20;
	public static final int KILLS = 19;
	public static final int KILLSTREAK = 22;
	private static final int TIME_ONLINE = 8;
	public static final int SERVER_TIME = 2;
	public static final int SLAYER_TASK = 11;
	
	public static void refreshPanel(Player player) {
		
		boolean combatExpLock = false;
		boolean expLock = player.experienceLocked();
	
		player.getPacketSender().sendString("@red@- @whi@General Information", getLineId(1));
		player.getPacketSender().sendString("@or2@Combat Exp Lock: " + (!combatExpLock ? "@or1@Unlocked" : "@red@Locked"), getLineId(3));
		player.getPacketSender().sendString("@or2@Exp Lock: " + (!expLock ? "@or1@Unlocked" : "@red@Locked"), getLineId(EXP_LOCK));
		player.getPacketSender().sendString("@or2@Email: "+(player.getEmailAddress() == null || player.getEmailAddress().equals("null") ? "@red@NOT SET" : player.getEmailAddress()), getLineId(4));
		player.getPacketSender().sendString("@or2@Crashed Star: " + (ShootingStar.CRASHED_STAR != null ? ShootingStar.CRASHED_STAR.getStarLocation().playerPanelFrame : "None"), getLineId(5));
		//player.getPacketSender().sendString("@or2@Well of Goodwill: " + (WellOfGoodwill.isActive() ? "@gre@Active" : "@red@Inactive"), getLineId(7));
		player.getPacketSender().sendString("@red@- @whi@Personal Statistics", getLineId(6));
		SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
		Date date = new Date();
		player.getPacketSender().sendString("@or2@Server Time: @or1@" + formatter.format(date), getLineId(2));
		
		writeTimeOnline(player);
		
		player.getPacketSender().sendString("@or2@Player Rights: @or1@" + Misc.formatPlayerName(player.getRights().toString().toLowerCase().replaceAll("_", " ")), getLineId(8));
		player.getPacketSender().sendString("@or2@Achievement Points: @or1@" + player.getPointsHandler().getAchievementPoints(), getLineId(9));
		player.getPacketSender().sendString("@or2@Total Donated: @or1@" + player.getAmountDonated(), getLineId(10));
		
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
		
		player.getPacketSender().sendString("@or2@Task: @or1@" + task + "@or2@ Amount: @or1@" + player.getSlayer().getAmountToSlay(), getLineId(SLAYER_TASK));
		player.getPacketSender().sendString("@or2@Slayer Points: @or1@" + player.getPointsHandler().getSlayerPoints(), getLineId(SLAYER_POINTS));
		player.getPacketSender().sendString("@or2@Pest Control Points: @or1@" + player.getPointsHandler().getCommendations() + " ", getLineId(PC_POINTS));
		player.getPacketSender().sendString("@or2@Vote Points: @or1@" + player.getPointsHandler().getVotingPoints() + " ", getLineId(14));
		player.getPacketSender().sendString("@or2@Loyalty Points: @or1@" + player.getPointsHandler().getLoyaltyPoints(), getLineId(15));
		player.getPacketSender().sendString("@or2@Trivia Points: @or1@" + player.getPointsHandler().getTriviaPoints()+ " ", getLineId(16));
		//player.getPacketSender().sendString("@red@- @whi@Pking Statistics", getLineId(18)));
		player.getPacketSender().sendString("@or2@PvP Points: @or1@" + player.getPointsHandler().getPkPoints(), getLineId(PVP_POINTS));
		int val = Math.min(1, 0);
		player.getPacketSender().sendString("@or2@Ep: @or1@" + val, getLineId(18));
		player.getPacketSender().sendString("@or2@Kills: @or1@" + player.getPlayerKillingAttributes().getPlayerKills(), getLineId(KILLS));
		player.getPacketSender().sendString("@or2@Deaths: @or1@" + player.getPlayerKillingAttributes().getPlayerDeaths(), getLineId(DEATHS));
		player.getPacketSender().sendString("@or2@K/D Ratio: @or1@" + player.getPlayerKillingAttributes().getKDR(), getLineId(KDR));
		player.getPacketSender().sendString("@or2@Killstreak: @or1@" + player.getPlayerKillingAttributes().getPlayerKillStreak(), getLineId(KILLSTREAK));
	
		player.getPacketSender().sendString("@red@- @whi@Interfaces", getLineId(23));
		player.getPacketSender().sendString(getLineId(24), "@or2@Open Kills Tracker@or1@");
		player.getPacketSender().sendString(getLineId(25), "@or2@Open Drop Log@or1@");
		player.getPacketSender().sendString(getLineId(26), "@or2@Scoreboard@or1@");
		player.getPacketSender().sendString(getLineId(27), "@or2@YouTuber Points@or1@");
		
		for (int i = 27; i < 40; i++) {
			player.getPacketSender().sendString(null, getLineId(i));
		}
	
	}
	
	public static void writeTimeOnline(Player player) {
		/*final int dayCycle = ServerTimeUnits.getCycleCount(ServerTimeUnits.DAY);
		final int hourCycle = ServerTimeUnits.getCycleCount(ServerTimeUnits.HOUR);
		long time = 0;//player.totalPlayTime;
		int days = 0;
		int hours = 0;
		
		if (time > dayCycle) {
			days = (int) (time / dayCycle);
		}
		if (time > hourCycle) {
			hours = (int) (time / hourCycle);
		}
		
		player.getPacketSender().sendString("@or2@Time Played:@or1@ " + hours + " @or1@Hours", getLineId(TIME_ONLINE));
*/
		player.getPacketSender().sendString(39165, "@or2@Time played:  @yel@"+Misc.getTimePlayed((player.getTotalPlayTime() + player.getRecordedLogin().elapsed())));
		//player.send(new SendStringPacket("@or2@Time Played:@or1@ " + days + " @or1@Days " + hours + " @or1@Hours", QuestTab.getLineId(TIME_ONLINE)));
	}
	
	/**
	 * Gets the id of the line based on the input
	 *
	 * @param line The line to get the id of
	 * @return The id of the line
	 */
	public static int getLineId(int line) {
		/*if (line > lines.length + 1) {
			new IllegalArgumentException("Line cannot be above " + (lines.length + 1) + ".");
			return 0;
		}*/
		int id = line + 39158;
		return id;//lines[line + 1];
	}

	public static void refreshPanel2(Player player) {
		/**
		 * General info
		 */
		player.getPacketSender().sendString(39159, "@or3@ - @whi@ General Information");

		if(ShootingStar.CRASHED_STAR == null) {
			player.getPacketSender().sendString(39162, "@or2@Crashed star: @yel@N/A");
		} else {
			player.getPacketSender().sendString(39162, "@or2@Crashed star: @yel@"+ShootingStar.CRASHED_STAR.getStarLocation().playerPanelFrame+"");
		}
		
		if(WellOfGoodwill.isActive()) {
			player.getPacketSender().sendString(39163, "@or2@Well of Goodwill: @yel@Active");
		} else {
			player.getPacketSender().sendString(39163, "@or2@Well of Goodwill: @yel@N/A");
		}
		
		
		/**
		 * Account info
		 */
		player.getPacketSender().sendString(39165, "@or3@ - @whi@ Account Information");
		player.getPacketSender().sendString(39167, "@or2@Username:  @yel@"+player.getUsername());
		player.getPacketSender().sendString(39168, "@or2@Claimed:  @yel@$"+player.getAmountDonated());
		player.getPacketSender().sendString(39169, "@or2@Rank:  @yel@"+Misc.formatText(player.getRights().toString().toLowerCase()));
		player.getPacketSender().sendString(39170, "@or2@Email:  @yel@"+(player.getEmailAddress() == null || player.getEmailAddress().equals("null") ? "@red@NOT SET" : player.getEmailAddress()));
		player.getPacketSender().sendString(39171, "@or2@Music:  @yel@"+(player.musicActive() ? "On" : "Off")+"");
		player.getPacketSender().sendString(39172, "@or2@Sounds:  @yel@"+(player.soundsActive() ? "On" : "Off")+"");
		player.getPacketSender().sendString(39173, "@or2@Exp Lock:  @yel@"+(player.experienceLocked() ? "Locked" : "Unlocked")+"");
	
		//player.getPacketSender().sendString(39174, "@or2@Date Joined: @yel@" + player.getPointsHandler().getDateJoined());
		/**
		 * Points
		 */
		player.getPacketSender().sendString(39175, "@or3@ - @whi@ Statistics");
		player.getPointsHandler().refreshPanel();

		/**
		 * Slayer
		 */
		player.getPacketSender().sendString(39189, "@or3@ - @whi@ Slayer");
		player.getPacketSender().sendString(39190, "@or2@Open Kills Tracker");
		player.getPacketSender().sendString(39191, "@or2@Open Drop Log");
		player.getPacketSender().sendString(39192, "@or2@Master:  @yel@"+Misc.formatText(player.getSlayer().getSlayerMaster().toString().toLowerCase().replaceAll("_", " ")));
		if(player.getSlayer().getSlayerTask() == SlayerTasks.NO_TASK) 
			player.getPacketSender().sendString(39193, "@or2@Task:  @yel@"+Misc.formatText(player.getSlayer().getSlayerTask().toString().toLowerCase().replaceAll("_", " "))+"");
		else
			player.getPacketSender().sendString(39193, "@or2@Task:  @yel@"+Misc.formatText(player.getSlayer().getSlayerTask().toString().toLowerCase().replaceAll("_", " "))+"s");
		player.getPacketSender().sendString(39194, "@or2@Task Streak:  @yel@"+player.getSlayer().getTaskStreak()+"");
		player.getPacketSender().sendString(39195, "@or2@Task Amount:  @yel@"+player.getSlayer().getAmountToSlay()+"");
		if(player.getSlayer().getDuoPartner() != null)
			player.getPacketSender().sendString(39196, "@or2@Duo Partner:  @yel@"+player.getSlayer().getDuoPartner()+"");
		else
			player.getPacketSender().sendString(39196, "@or2@Duo Partner:");

		/**
		 * Quests
		 */
		player.getPacketSender().sendString(39198, "@or3@ - @whi@ Quests");
		player.getPacketSender().sendString(39199, RecipeForDisaster.getQuestTabPrefix(player) + "Recipe For Disaster");
		player.getPacketSender().sendString(39200, Nomad.getQuestTabPrefix(player) + "Nomad's Requeim");

		/**
		 * Links
		 */
		player.getPacketSender().sendString(39202, "@or3@ - @whi@ Links");
		player.getPacketSender().sendString(39203, "@or2@Forum");
		player.getPacketSender().sendString(39204, "@or2@Rules");
		player.getPacketSender().sendString(39205, "@or2@Store");
		player.getPacketSender().sendString(39206, "@or2@Vote");
		player.getPacketSender().sendString(39207, "@or2@Hiscores");
		player.getPacketSender().sendString(39208, "@or2@Report");
	}

	/**
	 * Cycles to time units
	 *
	 * @Author Abdul <webmaster@zamorak.net>
	 *
	 */
	public enum ServerTimeUnits {

		MINUTE(100), HOUR(6000), DAY(144000), WEEK(1008000),

		;

		/**
		 * The amount of cycles per time unit, cannot do seconds as its less then 2
		 * cycles
		 */
		private int cycles;

		private ServerTimeUnits(int cycles) {
			this.cycles = cycles;
		}

		/**
		 * Gets the amount of cycles 
		 * @return
		 */
		public int getCycles() {
			return cycles;
		}
		
		public static int getCycleCount(ServerTimeUnits unit) {
			return unit.getCycles();
		}

	}


}
