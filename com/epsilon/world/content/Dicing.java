package com.epsilon.world.content;

import java.util.Random;

import com.epsilon.engine.task.Task;
import com.epsilon.engine.task.TaskManager;
import com.epsilon.model.Animation;
import com.epsilon.model.Graphic;
import com.epsilon.world.content.clan.ClanChat;
import com.epsilon.world.content.clan.ClanChatManager;
import com.epsilon.world.entity.impl.player.Player;

/**
 * 
 * @author Apache Ah64
 *
 */
public class Dicing {

	public static boolean handleRoll(final Player player, int itemId,
			boolean friends) {
		if (itemId >= 15086 && itemId <= 15100) {
			if (friends) {
				switch (itemId) {
				case 15086:
					friendsRoll(player, itemId, 2072, 1, 6);
					break;
				case 15088:
					friendsRoll(player, itemId, 2074, 1, 12);
					break;
				case 15090:
					friendsRoll(player, itemId, 2071, 1, 8);
					break;
				case 15092:
					friendsRoll(player, itemId, 2070, 1, 10);
					break;
				case 15094:
					friendsRoll(player, itemId, 2073, 1, 12);
					break;
				case 15096:
					friendsRoll(player, itemId, 2068, 1, 20);
					break;
				case 15098:
					friendsRoll(player, itemId, 2075, 1, 100);
					break;
				case 15100:
					friendsRoll(player, itemId, 2069, 1, 4);
					break;
				}
			} else {
				switch (itemId) {
				case 15086:
					privateRoll(player, itemId, 2072, 1, 6);
					break;
				case 15088:
					privateRoll(player, itemId, 2074, 1, 12);
					break;
				case 15090:
					privateRoll(player, itemId, 2071, 1, 8);
					break;
				case 15092:
					privateRoll(player, itemId, 2070, 1, 10);
					break;
				case 15094:
					privateRoll(player, itemId, 2073, 1, 12);
					break;
				case 15096:
					privateRoll(player, itemId, 2068, 1, 20);
					break;
				case 15098:
					privateRoll(player, itemId, 2075, 1, 100);
					break;
				case 15100:
					privateRoll(player, itemId, 2069, 1, 4);
					break;
				}
			}
			return true;
		}
		return false;
	}

	public static void privateRoll(final Player player, final int itemId,
			int graphic, final int lowest, final int highest) {
		player.sendMessage("Rolling...");
		player.getInventory().delete(itemId, 1);
		player.setAnimation(new Animation(11900));
		player.setGraphic(new Graphic(graphic));
		player.setCurrentTask(new Task(1) {

			@Override
			protected void execute() {
				super.stop();
				player.getInventory().add(itemId, 1);
				player.sendMessage("You rolled <col=db3535>"
						+ getRandom(lowest, highest) + "</col> on "
						+ diceText(itemId) + " die.");
			}
			
		});
		TaskManager.submit(player.getCurrentTask());
	}
	public static void riggedRoll(final Player player, final int itemId,
			int graphic, final int number) {
		final ClanChat chat = ClanChatManager.getClanChatChannel(player);
		if (chat == null) {
			player.sendMessage("You need to be in a clan chat to use this option.");
			return;
		}
		if(player.isPlayerLocked()) {
			return;
		}
		player.setPlayerLocked(true);
		player.sendMessage("Rolling...");
		player.getInventory().delete(itemId, 1);
		player.setAnimation(new Animation(11900));
		player.setGraphic(new Graphic(graphic));
		player.setCurrentTask(new Task(1) {
			
			@Override
			public void execute() {
				super.stop();
				player.getInventory().add(itemId, 1);
				ClanChatManager.sendMessage(chat, "Clan Chat channel-mate <col=db3535>"
						+ player.getUsername()
						+ "</col> rolled <col=db3535>" + number
						+ "</col> on " + diceText(itemId) + " die.");
				player.setPlayerLocked(false);
			}
			
		});
		TaskManager.submit(player.getCurrentTask());
	}
	
	public static void friendsRoll(final Player player, final int itemId,
			int graphic, final int lowest, final int highest) {
		final ClanChat chat = ClanChatManager.getClanChatChannel(player);
		if (chat == null) {
			player.sendMessage("You need to be in a friend chat to use this option.");
			return;
		}
		if(player.isPlayerLocked()) {
			return;
		}
		player.setPlayerLocked(true);
		player.sendMessage("Rolling...");
		player.getInventory().delete(itemId, 1);
		player.setAnimation(new Animation(11900));
		player.setGraphic(new Graphic(graphic));
		player.setCurrentTask(new Task(1) {
			
			@Override
			public void execute() {
				super.stop();
				player.getInventory().add(itemId, 1);
				ClanChatManager.sendMessage(chat, "Clan Chat channel-mate <col=db3535>"
								+ player.getUsername()
								+ "</col> rolled <col=db3535>"
								+ getRandom(lowest, highest) + "</col> on "
								+ diceText(itemId) + " die.");
				player.setPlayerLocked(false);
			}
			
		});
		TaskManager.submit(player.getCurrentTask());
	}

	public static int getRandom(int lowest, int highest) {
		Random r = new Random();
		if (lowest > highest) {
			return -1;
		}
		long range = (long) highest - (long) lowest + 1;
		long fraction = (long) (range * r.nextDouble());
		int numberRolled = (int) (fraction + lowest);
		return numberRolled;
	}

	public static String diceText(int id) {
		switch (id) {
		case 15086:
			return "a six-sided";
		case 15088:
			return "two six-sided";
		case 15090:
			return "an eight-sided";
		case 15092:
			return "a ten-sided";
		case 15094:
			return "a twelve-sided";
		case 15096:
			return "a a twenty-sided";
		case 15098:
			return "the percentile";
		case 15100:
			return "a four-sided";
		}
		return "";
	}
}
