package com.epsilon.world.content;

import java.util.concurrent.CopyOnWriteArrayList;

import com.epsilon.model.GameMode;
import com.epsilon.model.GroundItem;
import com.epsilon.model.Item;
import com.epsilon.model.Locations;
import com.epsilon.model.PlayerRights;
import com.epsilon.model.Locations.Location;
import com.epsilon.model.definitions.ItemDefinition;
import com.epsilon.util.Misc;
import com.epsilon.world.World;
import com.epsilon.world.content.skill.impl.farming.FarmingPatches;
import com.epsilon.world.content.skill.impl.farming.Plant;
import com.epsilon.world.entity.impl.GroundItemManager;
import com.epsilon.world.entity.impl.player.Player;

/**
 * @author: Taylan Selvi
 * Warning:
 * This crap is so messy and ugly. Will redo it once I get some time over.
 * Should be dupe-free.
 */

public class Gamble {
	
	protected int diceRolled;
	
	protected String host;
	protected String gambler;

	private Player player;
	public Gamble(Player p) {
		this.player = p;
	}
	
	public void FiftyFiveXTwo() {
		
	}

	public void requestTrade(Player player2) {
		if(player == null || player2 == null || player.getConstitution() <= 0 || player2.getConstitution() <= 0 || player.isTeleporting() || player2.isTeleporting())
			return;
		
		if(player.getGameMode() == GameMode.IRONMAN) {
			player.getPacketSender().sendMessage("Ironman-players are not allowed to gamble.");
			return;
		}
		if(player.getGameMode() == GameMode.HARDCORE_IRONMAN) {
			player.getPacketSender().sendMessage("Hardcore-ironman-players are not allowed to gamble.");
			return;
		}
		if(player2.getGameMode() == GameMode.IRONMAN) {
			player.getPacketSender().sendMessage("That player is a Hardcore-ironman-player and can therefore not stake.");
			return;
		}
		if(player2.getGameMode() == GameMode.HARDCORE_IRONMAN) {
			player.getPacketSender().sendMessage("That player is an Ironman player and can therefore not stake.");
			return;
		}
		if(player.getLocation() == Location.DUNGEONEERING) {
			player.getPacketSender().sendMessage("You are far too busy to gamble at the moment!");
			return;
		}
		if(player.getLocation() == Location.DUNGEONEERING) {
			player.getPacketSender().sendMessage("You are far too busy to gamble at the moment!");
			return;
		}
		
		/*if(Misc.getMinutesPlayed(player) < 15) {
			player.getPacketSender().sendMessage("You must have played for at least 15 minutes in order to gamble someone.");
			return;
		}*/
		if(player.getBankPinAttributes().hasBankPin() && !player.getBankPinAttributes().hasEnteredBankPin()) {
			BankPin.init(player, false);
			return;
		}
		/*if(player.getHostAdress().equals(player2.getHostAdress()) && player.getRights() != PlayerRights.OWNER && player.getRights() != PlayerRights.DEVELOPER) {
			player.getPacketSender().sendMessage("Same IP-adress found. You cannot gamble yourself from the same IP.");
			return;
		}*/
		if(System.currentTimeMillis() - lastGambleSent < 5000 && !inGambling()) {
			player.getPacketSender().sendMessage("You're sending gamble requests too frequently. Please slow down.");
			return;
		}
		if(player.getLocation() == Location.DUEL_ARENA && player.getDueling().duelingStatus == 5) {
			player.getPacketSender().sendMessage("You are far too busy to gamble at the moment!");
			return;
		}
		if(inGambling()) {
			declineGamble(true);
			return;
		}
		if(player.getLocation() == Location.GODWARS_DUNGEON && player.getMinigameAttributes().getGodwarsDungeonAttributes().hasEnteredRoom() && !player2.getMinigameAttributes().getGodwarsDungeonAttributes().hasEnteredRoom()) {
			player.getPacketSender().sendMessage("You cannot reach that.");
			return;
		}
		if(player.isShopping() || player.isBanking()) {
			player.getPacketSender().sendInterfaceRemoval();
			return;
		}
		if(player.busy()) {
			return;
		}
		if(player2.busy() || player2.getInterfaceId() > 0 || player2.getGamble().inGambling() || player2.isBanking() || player2.isShopping()/* || player2.getDueling().inDuelScreen || FightPit.inFightPits(player2)*/) {
			player.getPacketSender().sendMessage("The other player is currently busy.");
			return;
		}
		if(player.getInterfaceId() > 0 || inGambling() || player.isBanking() || player.isShopping()/* || player.getDueling().inDuelScreen || FightPit.inFightPits(player)*/) {
			player.getPacketSender().sendMessage("You are currently unable to gamble another player.");
			if(player.getInterfaceId() > 0)
				player.getPacketSender().sendMessage("Please close all open interfaces before requesting to open another one.");
			return;
		}
		gambleWith = player2.getIndex();
		if(getGambleWith() == player.getIndex())
			return;
		if(!Locations.goodDistance(player.getPosition().getX(), player.getPosition().getY(), player2.getPosition().getX(), player2.getPosition().getY(), 2)) {
			player.getPacketSender().sendMessage("Please get closer to request a gamble.");
			return;
		}
		if(!inGambling() && player2.getGamble().isGambleRequested() && player2.getGamble().getGambleWith() == player.getIndex()) {
			openTrade();
			player2.getGamble().openTrade();
		} else if(!inGambling()) {
			setGambleRequested(true);
			player.getPacketSender().sendMessage("You've sent a gamble request to "+player2.getUsername()+".");
			player2.getPacketSender().sendMessage(player.getUsername() +" wishes to gamble with you.");
		}
		lastGambleSent = System.currentTimeMillis();
	}
	
	public void handleConfigButtons(Player player, int buttonId) {
		if(!isCanGamble())
			return;
		Player player2 = World.getPlayers().get(getGambleWith());
		if(player2 == null || player == null)
			return;
		if(buttonId == -20522){
			player.getPacketSender().sendConfig(974, 2);
			player2.getPacketSender().sendConfig(974, 2);
			player.setConfigButtonSelected(1);
			player2.getGamble().gambleConfirmed2 = false;
			player.getGamble().gambleConfirmed = false;
			player2.getPacketSender().sendString(45012, "");
			player.getPacketSender().sendString(45012, "");
		}
		if(buttonId == -20523){		
			player.getPacketSender().sendConfig(974, 1);
			player2.getPacketSender().sendConfig(974, 1);
			player.setConfigButtonSelected(2);
			player2.getGamble().gambleConfirmed2 = false;
			player.getGamble().gambleConfirmed = false;
			player2.getPacketSender().sendString(45012, "");
			player.getPacketSender().sendString(45012, "");
		}
	}
	
	

	public void openTrade() {
		player.getPacketSender().sendClientRightClickRemoval();
		Player player2 = World.getPlayers().get(getGambleWith());
		if(player == null || player2 == null || getGambleWith() == player.getIndex() || player.isBanking())
			return;
		player.getPacketSender().sendConfig(974, 2);
		player2.getPacketSender().sendConfig(974, 2);
		player2.getPacketSender().sendString(45012, "");
		player.getPacketSender().sendString(45012, "");
		player.getPacketSender().sendString(45018, Misc.formatPlayerName(player2.getUsername()));
		setinGambling(true);
		setGambleRequested(false);
		setCanGamble(true);
		setGambleStatus(1);
		player.getPacketSender().sendInterfaceItems(3415, offeredItems);
		player2.getPacketSender().sendInterfaceItems(3415, player2.getGamble().offeredItems);
		sendText(player2);
		player.getInventory().refreshItems();
		player.getPacketSender().sendInterfaceItems(3415, offeredItems);
		player.getPacketSender().sendInterfaceItems(3416, player2.getGamble().offeredItems);
		player.getMovementQueue().reset();
		inGamblingWith = player2.getIndex();
	}

	public void declineGamble(boolean tellOther) {
		Player player2 = getGambleWith() >= 0 && !(getGambleWith() > World.getPlayers().capacity()) ? World.getPlayers().get(getGambleWith()) : null;
		for (Item item : offeredItems) {
			if (item.getAmount() < 1)
				continue;
			player.getInventory().add(item);
		}
		offeredItems.clear();
		if(tellOther && getGambleWith() > -1) {
			if(player2 == null)
				return;
			player2.getGamble().declineGamble(false);
			player2.getPacketSender().sendMessage("Other player declined the gamble.");
		}
		resetTrade();
	}

	public void sendText(Player player2) {
		if(player2 == null)
			return;
		//player2.getPacketSender().sendString(3451, "" + Misc.formatPlayerName(player.getUsername()) + "");
		//player2.getPacketSender().sendString(3417, "Trading with: " + Misc.formatPlayerName(player.getUsername()) + "");
		//player.getPacketSender().sendString(3451, "" + Misc.formatPlayerName(player2.getUsername()) + "");
		//player.getPacketSender().sendString(3417, "Trading with: " + Misc.formatPlayerName(player2.getUsername()) + "");
		//player.getPacketSender().sendString(3431, "");
		//player.getPacketSender().sendString(3535, "Are you sure you want to make this trade?");
		player.getPacketSender().sendString(45002, "" + Misc.formatPlayerName(player.getUsername()) + "'s Offer");
		player.getPacketSender().sendString(45003, "" + Misc.formatPlayerName(player2.getUsername()) + "'s Offer");
		player.getPacketSender().sendInterfaceSet(45000, 3321);
		player.getPacketSender().sendItemContainer(player.getInventory(), 3322);
	}

	public void tradeItem(int itemId, int amount, int slot) {
		if(slot < 0)
			return;
		if(!isCanGamble())
			return;
		Player player2 = World.getPlayers().get(getGambleWith());
		if(player2 == null || player == null)
			return;
		if(player.hasRights(PlayerRights.ADMINISTRATOR)) {
			player.getPacketSender().sendMessage("Administrators cannot gamble items.");
			return;
		}
	/*	if(player.getNewPlayerDelay() > 0 && player.getRights().ordinal() == 0) {
			player.getPacketSender().sendMessage("You must wait another "+player.getNewPlayerDelay() / 60+" minutes before being able to trade items.");
			return;
		}*/
		if(!player.hasRights(PlayerRights.DEVELOPER) && player2.hasRights(PlayerRights.DEVELOPER) && !(itemId == 1419 && player.getHighestRights().isStaff())) {
			if (!new Item(itemId).tradeable()) {
				player.getPacketSender().sendMessage("This item is currently untradeable and cannot be gambled.");
				return;
			}
		}
		falseTradeConfirm();
		player.getPacketSender().sendClientRightClickRemoval();
		if(!inGambling() || !canGamble) {
			declineGamble(true);
			return;
		}
		if(!player.getInventory().contains(itemId))
			return;
		if(slot >= player.getInventory().capacity() || player.getInventory().getItems()[slot].getId() != itemId || player.getInventory().getItems()[slot].getAmount() <= 0)
			return;
		Item itemToTrade = player.getInventory().getItems()[slot];
		if(itemToTrade.getId() != itemId)
			return;
		if (player.getInventory().getAmount(itemId) < amount) {
			amount = player.getInventory().getAmount(itemId);
			if (amount == 0 || player.getInventory().getAmount(itemId) < amount) {
				return;
			}
		}
		if (!itemToTrade.getDefinition().isStackable()) {
			for (int a = 0; a < amount && a < 28; a++) {
				if (player.getInventory().getAmount(itemId) >= 1) {
					offeredItems.add(new Item(itemId, 1));
					player.getInventory().delete(itemId, 1);
				}
			}
		} else
			if (itemToTrade.getDefinition().isStackable()) {
				boolean iteminGambling = false;
				for (Item item : offeredItems) {
					if (item.getId() == itemId) {
						iteminGambling = true;
						item.setAmount(item.getAmount() + amount);
						player.getInventory().delete(itemId, amount);
						break;
					}
				}
				if (!iteminGambling) {
					offeredItems.add(new Item(itemId, amount));
					player.getInventory().delete(itemId, amount);
				}
			}
		player.getInventory().refreshItems();
		player.getPacketSender().sendInterfaceItems(3416, player2.getGamble().offeredItems);
		player.getPacketSender().sendInterfaceItems(3415, offeredItems);
		//player.getPacketSender().sendString(3431, "");
		acceptedGamble = false;
		gambleConfirmed = false;
		gambleConfirmed2 = false;
		player2.getPacketSender().sendInterfaceItems(3416, offeredItems);
		//player2.getPacketSender().sendString(3431, "");
		player2.getGamble().acceptedGamble = false;
		player2.getGamble().gambleConfirmed = false;
		player2.getGamble().gambleConfirmed2 = false;
		sendText(player2);
	}

	public void removeTradedItem(int itemId, int amount) {
		if(!isCanGamble())
			return;
		Player player2 = World.getPlayers().get(getGambleWith());
		if(player2 == null)
			return;
		if(!inGambling() || !canGamble) {
			declineGamble(false);
			return;
		}
		falseTradeConfirm();
		ItemDefinition def = ItemDefinition.forId(itemId);
		if (!def.isStackable()) {
			if (amount > 28)
				amount = 28;
			for (int a = 0; a < amount; a++) {
				for (Item item : offeredItems) {
					if (item.getId() == itemId) {
						if (!item.getDefinition().isStackable()) {
							offeredItems.remove(item);
							player.getInventory().add(itemId, 1);
						}
						break;
					}
				}
			}
		} else
			for (Item item : offeredItems) {
				if (item.getId() == itemId) {
					if (item.getDefinition().isStackable()) {
						if (item.getAmount() > amount) {
							item.setAmount(item.getAmount() - amount);
							player.getInventory().add(itemId, amount);
						} else {
							amount = item.getAmount();
							offeredItems.remove(item);
							player.getInventory().add(itemId, amount);
						}
					}
					break;
				}
			}
		falseTradeConfirm();
		player.getInventory().refreshItems();
		player.getPacketSender().sendInterfaceItems(3416, player2.getGamble().offeredItems);
		player.getPacketSender().sendInterfaceItems(3415, offeredItems);
		player2.getPacketSender().sendInterfaceItems(3416, offeredItems);
		sendText(player2);
/*		player.getPacketSender().sendString(3431, "");
		player2.getPacketSender().sendString(3431, "");*/
		player.getPacketSender().sendClientRightClickRemoval();
	}

	public void acceptTrade(int stage) {
		if(!player.getClickDelay().elapsed(1000))
			return;
		if(getGambleWith() < 0) {
			declineGamble(false);
			return;
		}
		Player player2 = World.getPlayers().get(getGambleWith());
		if(player == null || player2 == null) {
			declineGamble(false);
			return;
		}
		if(!twoTraders(player, player2)) {
			player.getPacketSender().sendMessage("An error has occured. Please try re-gambling the player.");
			return;
		}		
		if(stage == 2) {
			if(!inGambling() || !player2.getGamble().inGambling()) {
				declineGamble(true);
				return;
			}
			acceptedGamble = true;
			gambleConfirmed2 = true;
			player2.getPacketSender().sendString(45012, "@gre@Other player has accepted.");
			player.getPacketSender().sendString(45012, "@red@Waiting for other player...");
			if (inGambling() && player2.getGamble().gambleConfirmed2) {
				if(player2.getGamble().gambleConfirmed2 == false && player.getGamble().gambleConfirmed == false) {
					return;
				}
				host = player.getUsername();
				gambler = player2.getUsername();
				diceRolled = Misc.getRandom(100);
				if (host == null)
					return;
				if (gambler == null)
					return;
				if (diceRolled >= 0 && diceRolled <= 55) {
					giveItems();
					player.sendMessage("Congratulations, you won 55x2 against " + player2.getUsername() + "! The number was " + diceRolled + ".");
					player2.sendMessage("Sorry, unfortunately you lost 55x2 against " + player.getUsername() + ". The number was " + diceRolled + ".");
				} else if (diceRolled >= 56 && diceRolled <= 100) {
					player2.getGamble().giveItems();
					player2.sendMessage("Congratulations, you won 55x2 against " + player2.getUsername() + "! The number was " + diceRolled + ".");
					player.sendMessage("Sorry, unfortunately you lost 55x2 against " + player.getUsername() + ". The number was " + diceRolled + ".");
				}
				acceptedGamble = true;
				player2.getGamble().acceptedGamble = true;
				resetTrade();
				player2.getGamble().resetTrade();
			}
		} else if(stage == 1) {
			player2.getGamble().goodGamble = true;
			player2.getPacketSender().sendString(45012, "@gre@Other player has accepted.");
			goodGamble = true;
			player.getPacketSender().sendString(45012, "@red@Waiting for other player...");
			player.sendMessage("When the click accept there's no going back.");
			gambleConfirmed = true;
		}
		player.getClickDelay().reset();
	}

	public void confirmScreen() {
		Player player2 = World.getPlayers().get(getGambleWith());
		if (player2 == null)
			return;
		setCanGamble(false);
		player.getInventory().refreshItems();
		String SendTrade = "Absolutely nothing!";
		String SendAmount;
		int Count = 0;
		for (Item item : offeredItems) {
			if (item.getAmount() >= 1000 && item.getAmount() < 1000000) {
				SendAmount = "@cya@" + (item.getAmount() / 1000) + "K @whi@(" + Misc.format(item.getAmount()) + ")";
			} else if (item.getAmount() >= 1000000) {
				SendAmount = "@gre@" + (item.getAmount() / 1000000) + " million @whi@(" + Misc.format(item.getAmount()) + ")";
			} else {
				SendAmount = "" + Misc.format(item.getAmount());
			}
			if (Count == 0) {
				SendTrade = item.getDefinition().getName().replaceAll("_", " ");
			} else
				SendTrade = SendTrade + "\\n" + item.getDefinition().getName().replaceAll("_", " ");
			if (item.getDefinition().isStackable())
				SendTrade = SendTrade + " x " + SendAmount;
			Count++;
		}

		//player.getPacketSender().sendString(3557, SendTrade);
		SendTrade = "Absolutely nothing!";
		SendAmount = "";
		Count = 0;
		for (Item item : player2.getGamble().offeredItems) {
			if (item.getAmount() >= 1000 && item.getAmount() < 1000000) {
				SendAmount = "@cya@" + (item.getAmount() / 1000) + "K @whi@(" + Misc.format(item.getAmount()) + ")";
			} else if (item.getAmount() >= 1000000) {
				SendAmount = "@gre@" + (item.getAmount() / 1000000) + " million @whi@(" + Misc.format(item.getAmount()) + ")";
			} else {
				SendAmount = "" + Misc.format(item.getAmount());
			}
			if (Count == 0) {
				SendTrade = item.getDefinition().getName().replaceAll("_", " ");
			} else
				SendTrade = SendTrade + "\\n" + item.getDefinition().getName().replaceAll("_", " ");
			if (item.getDefinition().isStackable())
				SendTrade = SendTrade + " x " + SendAmount;
			Count++;
		}
		//player.getPacketSender().sendString(3558, SendTrade);
		player.getPacketSender().sendInterfaceSet(3443, 3321);
		player.getPacketSender().sendItemContainer(player.getInventory(), 3322);
		/*
		 * Remove all tabs!
		 */
		//player.getPacketSender().sendInterfaceSet(3443, Inventory.INTERFACE_ID);
		//player.getPacketSender().sendItemContainer(player.getInventory(), Inventory.INTERFACE_ID);
	}

	public void giveItems() {
		Player player2 = World.getPlayers().get(getGambleWith());
		if (player2 == null)
			return;
		if(!inGambling() || !player2.getGamble().inGambling())
			return;
		try {
			for (Item item : player2.getGamble().offeredItems) {
				if (item.getId() == 995) {
					int coinAmount = item.getAmount();
					player.setMoneyInPouch(player.getMoneyInPouch() + coinAmount);
					player.getPacketSender().sendString(8135, ""+player.getMoneyInPouch()+"");
					player.sendMessage(Misc.format(coinAmount) + " coins were added to your money pouch.");
				} else if(player.getInventory().getFreeSlots() > 0) {
					player.getInventory().add(item);
				} else {
					GroundItemManager.spawnGroundItem(player, new GroundItem(item, player.getPosition().copy(), player.getUsername(), player.getHostAddress(), false, 80, player.getGameMode() == GameMode.LEGEND && player.getGameMode() == GameMode.EXTREME && player.getPosition().getZ() >= 0 && player.getPosition().getZ() < 4 ? true : false, 80));
				}
			}
			for (Item item : player.getGamble().offeredItems) {
				if (item.getId() == 995) {
					int coinAmount = item.getAmount();
					player.setMoneyInPouch(player.getMoneyInPouch() + coinAmount);
					player.getPacketSender().sendString(8135, ""+player.getMoneyInPouch()+"");
					player.sendMessage(Misc.format(coinAmount) + " coins were added to your money pouch.");
				} else if(player.getInventory().getFreeSlots() > 0) {
					player.getInventory().add(item);
				} else {
					GroundItemManager.spawnGroundItem(player, new GroundItem(item, player.getPosition().copy(), player.getUsername(), player.getHostAddress(), false, 80, player.getGameMode() == GameMode.LEGEND && player.getGameMode() == GameMode.EXTREME && player.getPosition().getZ() >= 0 && player.getPosition().getZ() < 4 ? true : false, 80));
				}
			}
		} catch (Exception ignored) {
		}
	}


	public void resetTrade() {
		inGamblingWith = -1;
		offeredItems.clear();
		setCanGamble(true);
		setinGambling(false);
		setGambleWith(-1);
		setGambleStatus(0);
		lastGambleSent = 0;
		acceptedGamble = false;
		gambleConfirmed = false;
		gambleConfirmed2 = false;
		gambleRequested = false;
		canGamble = true;
		goodGamble = false;
		//player.getPacketSender().sendString(3535, "Are you sure you want to make this trade?");
		player.getPacketSender().sendInterfaceRemoval();
		player.getPacketSender().sendInterfaceRemoval();
	}


	private boolean falseTradeConfirm() {
		Player player2 = World.getPlayers().get(getGambleWith());
		return gambleConfirmed = player2.getGamble().gambleConfirmed = false;
	}

	public CopyOnWriteArrayList<Item> offeredItems = new CopyOnWriteArrayList<Item>();
	private boolean inGambling = false;
	private boolean gambleRequested = false;
	private int gambleWith = -1;
	private int gambleStatus;
	public long lastGambleSent, lastAction;
	private boolean canGamble = true;
	public boolean gambleConfirmed = false;
	public boolean gambleConfirmed2 = false;
	public boolean acceptedGamble = false;
	public boolean goodGamble = false;
	public int inGamblingWith = -1;
	
	public int getinGamblingWith() {
		return inGamblingWith;
	}

	public void setinGamblingWith(int inGamblingWith) {
		this.inGamblingWith = inGamblingWith;
	}

	public boolean inGambling() {
		return inGambling;
	}

	public void setinGambling(boolean inGambling) {
		this.inGambling = inGambling;
	}

	public boolean isGambleRequested() {
		return gambleRequested;
	}

	public void setGambleRequested(boolean gambleRequested) {
		this.gambleRequested = gambleRequested;
	}

	public int getGambleWith() {
		return gambleWith;
	}

	public void setGambleWith(int gambleWith) {
		this.gambleWith = gambleWith;
	}

	public int getGambleStatus() {
		return gambleStatus;
	}

	public void setGambleStatus(int gambleStatus) {
		this.gambleStatus = gambleStatus;
	}

	public long getLastGambleSent() {
		return lastGambleSent;
	}

	public void setLastGambleSent(long lastGambleSent) {
		this.lastGambleSent = lastGambleSent;
	}

	public long getLastAction() {
		return lastAction;
	}

	public void setLastAction(long lastAction) {
		this.lastAction = lastAction;
	}

	public boolean isCanGamble() {
		return canGamble;
	}

	public void setCanGamble(boolean canGamble) {
		this.canGamble = canGamble;
	}

	public boolean isGambleConfirmed() {
		return gambleConfirmed;
	}

	public void setGambleConfirmed(boolean gambleConfirmed) {
		this.gambleConfirmed = gambleConfirmed;
	}

	public boolean isGambleConfirmed2() {
		return gambleConfirmed2;
	}

	public void setGambleConfirmed2(boolean gambleConfirmed2) {
		this.gambleConfirmed2 = gambleConfirmed2;
	}

	public boolean isAcceptedGamble() {
		return acceptedGamble;
	}

	public void setAcceptedGamble(boolean acceptedGamble) {
		this.acceptedGamble = acceptedGamble;
	}

	public boolean isGoodGamble() {
		return goodGamble;
	}

	public void setGoodGamble(boolean goodGamble) {
		this.goodGamble = goodGamble;
	}

	/**
	 * Checks if two players are the only ones in a trade.
	 * @param p1	Player1 to check if he's 1/2 player in trade.
	 * @param p2	Player2 to check if he's 2/2 player in trade.
	 * @return		true if only two people are in the trade.
	 */
	public static boolean twoTraders(Player p1, Player p2) {
		int count = 0;
		for(Player player : World.getPlayers()) {
			if(player == null)
				continue;
			if(player.getGamble().inGamblingWith == p1.getIndex() || player.getGamble().inGamblingWith == p2.getIndex()) {
				count++;
			}
		}
		return count == 2;
	}

	/**
	 * The trade interface id.
	 */
	public static final int INTERFACE_ID = 45000;

	/**
	 * The trade interface id for removing items.
	 */
	public static final int INTERFACE_REMOVAL_ID = 3415;

}
