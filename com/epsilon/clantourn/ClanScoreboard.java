package com.epsilon.clantourn;

import java.util.Arrays;
import java.util.Comparator;

import com.epsilon.world.content.Scoreboards.Scoreboard;
import com.epsilon.world.content.clan.ClanChat;
import com.epsilon.world.content.clan.ClanChatManager;
import com.epsilon.world.entity.impl.player.Player;

public class ClanScoreboard {

	public static void open(Player player) {
		
		ClanChat[] clans = new ClanChat[ClanChatManager.getClans().length];
		
		System.arraycopy(ClanChatManager.getClans(), 0, clans, 0, clans.length);
		
		Arrays.sort(clans, new Comparator<ClanChat>() {

			@Override
			public int compare(ClanChat arg0, ClanChat arg1) {
				if(arg0 == null) {
					return -1;
				}
				if(arg1 == null) {
					return 1;
				}
				int v1 = arg0.getWins();
				int v2 = arg1.getWins();
				if (v1 == v2) {
					return 0;
				} else if (v1 > v2) {
					return -1;
				} else {
					return 1;
				}
			}});
		
		int stringId = 6402;
		for (int i = 0; i <= 50; stringId++, i++) {
			if(i == 10) {
				stringId = 8578;
			}
			if(i > clans.length || clans[i] == null) {
				player.getPacketSender().sendString(stringId, "");
				continue;
			}
			String line = "@whi@Rank @or1@"+(i+1)+"@whi@ - "+clans[i].getOwnerName()+" - "+clans[i].getWins()+" wins";
			
			player.getPacketSender().sendString(stringId, line);
		}
		player.getPacketSender().sendInterface(6308).sendString(6400, "Scoreboard - Clan Wins").sendString(6399, "").sendString(6401, "Close");
	
		
	}
	
}
