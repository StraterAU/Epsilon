package com.epsilon.world.content;

import com.epsilon.world.entity.impl.player.Player;

public class QuestTab {

	public enum TabType {
		INFORMATION,
		STATISTICS,
		PANELS,
		LINKS;
	}

	public static void startUp(Player player) {
		player.getPacketSender().sendConfig(540, 1);
	}

}
