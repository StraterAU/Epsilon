package com.epsilon.world.content.questtab;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.epsilon.util.Misc;
import com.epsilon.world.World;
import com.epsilon.world.content.ShootingStar;
import com.epsilon.world.content.WellOfGoodwill;
import com.epsilon.world.entity.impl.player.Player;

public class ServerInformation {
	
	
	
	public static void display(Player player) {
		boolean expLock = player.experienceLocked();
		Player.serverInformation = true;
		Player.userStatistics = false;
		Player.dailyTasks = false;
		Player.links = false;
		for (int i = 50051; i <= 50071; i++) {
			player.getPacketSender().sendString(i, "");
		}
		player.getPacketSender().sendString(50007, "Server Information");
		player.getPacketSender().sendString(50051, "@or1@Players Online: @whi@"+(int)(World.players.size()));
		player.getPacketSender().sendString(50052, "@or1@Server Time: @yel@[ @whi@"+Misc.getCurrentServerTime()+"@yel@ ]");
		player.getPacketSender().sendString(50053, "@or1@Exp Lock: " + (!expLock ? "@whi@Unlocked" : "@whi@Locked"));
		player.getPacketSender().sendString(50054, "@or1@Email:@whi@ "+(player.getEmailAddress() == null || player.getEmailAddress().equals("null") ? "@whi@NOT SET" : player.getEmailAddress()));
		player.getPacketSender().sendString(50055, "@or1@Crashed Star: @whi@" + (ShootingStar.CRASHED_STAR != null ? ShootingStar.CRASHED_STAR.getStarLocation().playerPanelFrame : "None"));
		player.getPacketSender().sendString(50056, "@or1@Well of Goodwill: " + (WellOfGoodwill.isActive() ? "@gre@Active" : "@red@Inactive"));
		player.getPacketSender().sendString(50057, "@or1@Latest Voter: @whi@" + Player.getLatestVoter());
	}

}
