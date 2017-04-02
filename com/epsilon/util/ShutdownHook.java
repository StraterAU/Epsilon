package com.epsilon.util;

import java.util.logging.Logger;

import com.epsilon.GameServer;
import com.epsilon.world.World;
import com.epsilon.world.content.Scoreboards;
import com.epsilon.world.content.WellOfGoodwill;
import com.epsilon.world.content.clan.ClanChatManager;
import com.epsilon.world.content.grandexchange.GrandExchangeOffers;
import com.epsilon.world.entity.impl.player.Player;
import com.epsilon.world.entity.impl.player.PlayerHandler;

public class ShutdownHook extends Thread {

	/**
	 * The ShutdownHook logger to print out information.
	 */
	private static final Logger logger = Logger.getLogger(ShutdownHook.class.getName());

	@Override
	public void run() {
		logger.info("The shutdown hook is processing all required actions...");
		World.savePlayers();
		GameServer.setUpdating(true);
		for (Player player : World.getPlayers()) {
			if (player != null) {
				World.deregister(player);
			}
		}
		WellOfGoodwill.save();
		GrandExchangeOffers.save();
		ClanChatManager.save();
		logger.info("The shudown hook actions have been completed, shutting the server down...");
	}
}
