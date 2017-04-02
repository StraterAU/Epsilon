package com.epsilon.world.content;

import java.util.Collections;
import java.util.Comparator;

import com.epsilon.GameSettings;
import com.epsilon.world.entity.impl.player.Player;

public class KillsTracker {

	public static void submit(Player player, KillsEntry[] kills) {
		for(KillsEntry kill : kills) {
			if(kill != null)
				submit(player, kill);
		}
	}

	public static void submit(Player player, KillsEntry kill) {
		int index = getIndex(player, kill);
		if(index >= 0) {
			player.getKillsTracker().get(index).amount += kill.amount;
		} else {
			player.getKillsTracker().add(kill);
		}
		if(player.isKillsTrackerOpen()) {
			open(player);
		}
	}

	public static void open(Player player) {
		try {
			/* RESORT THE KILLS */
			Collections.sort(player.getKillsTracker(), new Comparator<KillsEntry>() {
				@Override
				public int compare(KillsEntry kill1, KillsEntry kill2) {
					if(kill1.boss && !kill2.boss) {
						return -1;
					}
					if(kill2.boss && !kill1.boss) {
						return 1;
					}
					if(kill1.boss && kill2.boss || !kill1.boss && !kill2.boss) {
						if(kill1.amount > kill2.amount) {
							return -1;
						} else if(kill2.amount > kill1.amount) {
							return 1;
						} else {
							if(kill1.npcName.compareTo(kill2.npcName) > 0) {
								return 1;
							} else {
								return -1;
							}
						}
					}
					return 0;
				}
			});
			/* SHOW THE INTERFACE */
			player.setKillsTrackerOpen(true);
			resetInterface(player);
			player.getPacketSender().sendTabInterface(GameSettings.QUESTS_TAB, 55000);
			player.getPacketSender().sendString(55020, "@or3@ - @whi@ Boss Kills");
			int index = 1;
			for(KillsEntry entry : player.getKillsTracker()) {
				if(55020+index >= 55064)
					break;
				if(entry.boss) {
					player.getPacketSender().sendString(55020+index, "@or2@ "+entry.npcName+": "+entry.amount+"");
					index++;
				}
			}
			index+=2;
			player.getPacketSender().sendString(55020+index, "@or3@ - @whi@ Other Kills");
			index++;
			for(KillsEntry entry : player.getKillsTracker()) {
				if(entry.boss)
					continue;
				if(55020+index >= 55064)
					break;
				player.getPacketSender().sendString(55020+index, "@or2@ "+entry.npcName+": "+entry.amount+"");
				index++;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void resetInterface(Player player) {
		for(int i = 55020; i < 55064; i++) {
			player.getPacketSender().sendString(i, "");
		}
	}

	public static int getIndex(Player player, KillsEntry kill) {
		for(int i = 0; i < player.getKillsTracker().size(); i++) {
			if(player.getKillsTracker().get(i).npcName.equals(kill.npcName)) {
				return i;
			}
		}
		return -1;
	}

	public static class KillsEntry {

		public KillsEntry(String npcName, int amount, boolean boss) {
			this.npcName = npcName;
			this.amount = amount;
			this.boss = boss;
		}

		private String npcName;
		private int amount;
		private boolean boss;
	}

}
