package com.epsilon.net.packet.impl;

import com.epsilon.model.Flag;
import com.epsilon.model.Item;
import com.epsilon.model.PlayerRights;
import com.epsilon.model.Locations.Location;
import com.epsilon.model.container.impl.Bank;
import com.epsilon.model.container.impl.BeastOfBurden;
import com.epsilon.model.container.impl.Equipment;
import com.epsilon.model.container.impl.PriceChecker;
import com.epsilon.model.container.impl.Shop;
import com.epsilon.model.definitions.ItemDefinition;
import com.epsilon.model.definitions.WeaponAnimations;
import com.epsilon.model.definitions.WeaponInterfaces;
import com.epsilon.model.input.impl.EnterAmountToBank;
import com.epsilon.model.input.impl.EnterAmountToBuyFromShop;
import com.epsilon.model.input.impl.EnterAmountToPriceCheck;
import com.epsilon.model.input.impl.EnterAmountToRemoveFromBank;
import com.epsilon.model.input.impl.EnterAmountToRemoveFromBob;
import com.epsilon.model.input.impl.EnterAmountToRemoveFromPriceCheck;
import com.epsilon.model.input.impl.EnterAmountToRemoveFromStake;
import com.epsilon.model.input.impl.EnterAmountToRemoveFromTrade;
import com.epsilon.model.input.impl.EnterAmountToSellToShop;
import com.epsilon.model.input.impl.EnterAmountToStake;
import com.epsilon.model.input.impl.EnterAmountToStore;
import com.epsilon.model.input.impl.EnterAmountToTrade;
import com.epsilon.net.packet.Packet;
import com.epsilon.net.packet.PacketListener;
import com.epsilon.world.World;
import com.epsilon.world.content.BonusManager;
import com.epsilon.world.content.Gamble;
import com.epsilon.world.content.Trading;
import com.epsilon.world.content.combat.CombatFactory;
import com.epsilon.world.content.combat.magic.Autocasting;
import com.epsilon.world.content.combat.weapon.CombatSpecial;
import com.epsilon.world.content.grandexchange.GrandExchange;
import com.epsilon.world.content.grandexchange.GrandExchangeOffer;
import com.epsilon.world.content.minigames.impl.Dueling;
import com.epsilon.world.content.minigames.impl.Dueling.DuelRule;
import com.epsilon.world.content.skill.impl.smithing.EquipmentMaking;
import com.epsilon.world.content.skill.impl.smithing.SmithingData;
import com.epsilon.world.content.transportation.JewelryTeleporting;
import com.epsilon.world.entity.impl.player.Player;

public class ItemContainerActionPacketListener implements PacketListener {

	/**
	 * Manages an item's first action.
	 * @param player	The player clicking the item.
	 * @param packet	The packet to read values from.
	 */
	private static void firstAction(Player player, Packet packet) {
		int interfaceId = packet.readShortA();
		int slot = packet.readShortA();
		int id = packet.readShortA();
		Item item = new Item(id);
		switch (interfaceId) {
		case GrandExchange.COLLECT_ITEM_PURCHASE_INTERFACE:
			GrandExchange.collectItem(player, id, slot, GrandExchangeOffer.OfferType.BUYING);
			break;
		case GrandExchange.COLLECT_ITEM_SALE_INTERFACE:
			GrandExchange.collectItem(player, id, slot, GrandExchangeOffer.OfferType.SELLING);
			break;
		case Trading.INTERFACE_ID:
			if(player.getTrading().inTrade()) {
				player.getTrading().tradeItem(id, 1, slot);
			} else if(Dueling.checkDuel(player, 1) || Dueling.checkDuel(player, 2)) {
				player.getDueling().stakeItem(id, 1, slot);
			} else if(player.getGamble().inGambling()) {
				player.getGamble().tradeItem(id, 1, slot);
				Player player2 = World.getPlayers().get(player.getGamble().getGambleWith());
				player2.getGamble().gambleConfirmed2 = false;
				player.getGamble().gambleConfirmed = false;
				player.getGamble().gambleConfirmed2 = false;
				player2.getGamble().gambleConfirmed = false;
				player.getPacketSender().sendString(45012, "");
				player2.getPacketSender().sendString(45012, "");
			}
			break;
		case Trading.INTERFACE_REMOVAL_ID:
			if(player.getTrading().inTrade()) {
				player.getTrading().removeTradedItem(id, 1);
			} else if(player.getGamble().inGambling()) {
				player.getGamble().removeTradedItem(id, 1);
				Player player2 = World.getPlayers().get(player.getGamble().getGambleWith());
				player2.getGamble().gambleConfirmed2 = false;
				player.getGamble().gambleConfirmed = false;
				player.getGamble().gambleConfirmed2 = false;
				player2.getGamble().gambleConfirmed = false;
				player.getPacketSender().sendString(45012, "");
				player2.getPacketSender().sendString(45012, "");
			}
			break;
/*		case Gamble.INTERFACE_ID:
			if(player.getGamble().inGambling()) {
				player.getGamble().tradeItem(id, 1, slot);
			}
			break;
		case Gamble.INTERFACE_REMOVAL_ID:
			if(player.getGamble().inGambling())
				player.getGamble().removeTradedItem(id, 1);
			break;*/
		case Dueling.INTERFACE_REMOVAL_ID:
			if(Dueling.checkDuel(player, 1) || Dueling.checkDuel(player, 2)) {
				player.getDueling().removeStakedItem(id, 1);
				return;
			}
			break;
		case Equipment.INVENTORY_INTERFACE_ID:
			item = slot < 0 ? null : player.getEquipment().getItems()[slot];
			if(item == null || item.getId() != id)
				return;
			if(player.getLocation() == Location.DUEL_ARENA) {
				if(player.getDueling().selectedDuelRules[DuelRule.LOCK_WEAPON.ordinal()]) {
					if(item.getDefinition().getEquipmentSlot() == Equipment.WEAPON_SLOT || item.getDefinition().isTwoHanded()) {
						player.getPacketSender().sendMessage("Weapons have been locked during this duel!");
						return;
					}
				}
			}
			boolean stackItem = item.getDefinition().isStackable() && player.getInventory().getAmount(item.getId()) > 0;
			int inventorySlot = player.getInventory().getEmptySlot();
			if (inventorySlot != -1) {
				Item itemReplacement = new Item(-1, 0);
				player.getEquipment().setItem(slot, itemReplacement);
				if(!stackItem)
					player.getInventory().setItem(inventorySlot, item);
				else
					player.getInventory().add(item.getId(), item.getAmount());
				BonusManager.update(player);
				if (item.getDefinition().getEquipmentSlot() == Equipment.WEAPON_SLOT) {
					WeaponInterfaces.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
					WeaponAnimations.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
					if(player.getAutocastSpell() != null || player.isAutocast()) {
						Autocasting.resetAutocast(player, true);
						player.getPacketSender().sendMessage("Autocast spell cleared.");
					}
					player.setSpecialActivated(false);
					CombatSpecial.updateBar(player);
					if (player.hasStaffOfLightEffect()) {
						player.setStaffOfLightEffect(-1);
						player.getPacketSender().sendMessage("You feel the spirit of the Staff of Light begin to fade away...");
					}
				}
				player.getEquipment().refreshItems();
				player.getInventory().refreshItems();
				player.getUpdateFlag().flag(Flag.APPEARANCE);
			} else {
				player.getInventory().full();
			}
			break;
		case Bank.INTERFACE_ID:
			if (!player.isBanking() || player.getInterfaceId() != 5292)
				break;
			player.getBank(player.getCurrentBankTab()).switchItem(player.getInventory(), item, slot, true, true);
			player.getBank(player.getCurrentBankTab()).open();
			break;
		case Bank.INVENTORY_INTERFACE_ID:
			if (!player.isBanking() || !player.getInventory().contains(item.getId()) || player.getInterfaceId() != 5292)
				return;
			player.setCurrentBankTab(Bank.getTabForItem(player, item.getId()));
			player.getInventory().switchItem(player.getBank(player.getCurrentBankTab()), item, slot, false, true);
			break;
		case Shop.ITEM_CHILD_ID:
			if(player.getShop() != null)
				player.getShop().checkValue(player, slot, false);
			break;
		case Shop.INVENTORY_INTERFACE_ID:
			if(player.getShop() != null)
				player.getShop().checkValue(player, slot, true);
			break;
		case BeastOfBurden.INTERFACE_ID:
			if(player.getInterfaceId() == BeastOfBurden.INTERFACE_ID && player.getSummoning().getBeastOfBurden() != null) {
				if(item.getDefinition().isStackable()){
					player.getPacketSender().sendMessage("You cannot store stackable items.");
					return;
				}
				player.getInventory().switchItem(player.getSummoning().getBeastOfBurden(), item, slot, false, true);
			}
			break;
		case PriceChecker.INTERFACE_PC_ID:
			if(player.getInterfaceId() == PriceChecker.INTERFACE_ID && player.getPriceChecker().isOpen()) {
				player.getInventory().switchItem(player.getPriceChecker(), item, slot, false, true);
			}
			break;
		case 1119: //smithing interface row 1
		case 1120: // row 2
		case 1121: // row 3
		case 1122: // row 4
		case 1123: // row 5
			int barsRequired = SmithingData.getBarAmount(item);
			Item bar = new Item(player.getSelectedSkillingItem(), barsRequired);
			int x = 1;
			if(x > (player.getInventory().getAmount(bar.getId()) / barsRequired))
				x = (player.getInventory().getAmount(bar.getId()) / barsRequired);
			EquipmentMaking.smithItem(player, new Item(player.getSelectedSkillingItem(), barsRequired), new Item(item.getId(), SmithingData.getItemAmount(item)), x);
			break;
		}

		if(BeastOfBurden.beastOfBurdenSlot(interfaceId) >= 0) {
			if(player.getInterfaceId() == BeastOfBurden.INTERFACE_ID && player.getSummoning().getBeastOfBurden() != null) {
				player.getSummoning().getBeastOfBurden().switchItem(player.getInventory(), item, BeastOfBurden.beastOfBurdenSlot(interfaceId), false, true);
			}
		} else if(PriceChecker.priceCheckerSlot(interfaceId) >= 0) {
			if(player.getPriceChecker().isOpen()) {
				player.getPriceChecker().switchItem(player.getInventory(), new Item(id, 1), PriceChecker.priceCheckerSlot(interfaceId), false, true);
			}
		}
	}

	/**
	 * Manages an item's second action.
	 * @param player	The player clicking the item.
	 * @param packet	The packet to read values from.
	 */
	private static void secondAction(Player player, Packet packet) {
		int interfaceId = packet.readLEShortA();
		int id = packet.readLEShortA();
		int slot = packet.readLEShort();
		Item item = new Item(id);
		switch (interfaceId) {
		case Trading.INTERFACE_ID:
			if(player.getTrading().inTrade()) {
				player.getTrading().tradeItem(id, 5, slot);
			} else if(Dueling.checkDuel(player, 1) || Dueling.checkDuel(player, 2)) {
				player.getDueling().stakeItem(id, 5, slot);
			}  else if(player.getGamble().inGambling()) {
				player.getGamble().tradeItem(id, 5, slot);
				Player player2 = World.getPlayers().get(player.getGamble().getGambleWith());
				player2.getGamble().gambleConfirmed2 = false;
				player.getGamble().gambleConfirmed = false;
				player.getGamble().gambleConfirmed2 = false;
				player2.getGamble().gambleConfirmed = false;
				player.getPacketSender().sendString(45012, "");
				player2.getPacketSender().sendString(45012, "");
			}
			break;
		case Trading.INTERFACE_REMOVAL_ID:
			if(player.getTrading().inTrade() || player.getGamble().inGambling())
				player.getTrading().removeTradedItem(id, 5);
			Player player2 = World.getPlayers().get(player.getGamble().getGambleWith());
			player2.getGamble().gambleConfirmed2 = false;
			player.getGamble().gambleConfirmed = false;
			player.getGamble().gambleConfirmed2 = false;
			player2.getGamble().gambleConfirmed = false;
			player.getPacketSender().sendString(45012, "");
			player2.getPacketSender().sendString(45012, "");
			break;
/*		case Gamble.INTERFACE_ID:
			if(player.getGamble().inGambling()) {
				player.getGamble().tradeItem(id, 5, slot);
			}
			break;
		case Gamble.INTERFACE_REMOVAL_ID:
			if(player.getGamble().inGambling())
				player.getGamble().removeTradedItem(id, 5);
			break;*/
		case Dueling.INTERFACE_REMOVAL_ID:
			if(Dueling.checkDuel(player, 1) || Dueling.checkDuel(player, 2)) {
				player.getDueling().removeStakedItem(id, 5);
				return;
			}
			break;
		case Bank.INTERFACE_ID:
			if (!player.isBanking() || item.getId() != id || player.getInterfaceId() != 5292)
				return;
			player.getBank(player.getCurrentBankTab()).switchItem(player.getInventory(), new Item(id, 5), slot, true, true);
			player.getBank(player.getCurrentBankTab()).open();
			break;
		case Bank.INVENTORY_INTERFACE_ID:
			item = player.getInventory().forSlot(slot).copy().setAmount(5).copy();
			if (!player.isBanking() || item.getId() != id || !player.getInventory().contains(item.getId()) || player.getInterfaceId() != 5292)
				return;
			player.setCurrentBankTab(Bank.getTabForItem(player, item.getId()));
			player.getInventory().switchItem(player.getBank(player.getCurrentBankTab()), item, slot, false, true);
			break;
		case Shop.ITEM_CHILD_ID:
			if(player.getShop() == null)
				return;
			item = player.getShop().forSlot(slot).copy().setAmount(1).copy();
			player.getShop().setPlayer(player).switchItem(player.getInventory(), item, slot, false, true);
			break;
		case Shop.INVENTORY_INTERFACE_ID:
			if(player.isShopping()) {
				player.getShop().sellItem(player, slot, 1);
				return;
			}
			break;
		case BeastOfBurden.INTERFACE_ID:
			if(player.getInterfaceId() == BeastOfBurden.INTERFACE_ID && player.getSummoning().getBeastOfBurden() != null) {
				if(item.getDefinition().isStackable()){
					player.getPacketSender().sendMessage("You cannot store stackable items.");
					return;
				}
				player.getInventory().switchItem(player.getSummoning().getBeastOfBurden(), new Item(id, 5), slot, false, true);
			}
			break;
		case PriceChecker.INTERFACE_PC_ID:
			if(player.getInterfaceId() == PriceChecker.INTERFACE_ID && player.getPriceChecker().isOpen()) {
				player.getInventory().switchItem(player.getPriceChecker(), new Item(id, 5), slot, false, true);
			}
			break;
		case 1119: //smithing interface row 1
		case 1120: // row 2
		case 1121: // row 3
		case 1122: // row 4
		case 1123: // row 5
			int barsRequired = SmithingData.getBarAmount(item);
			Item bar = new Item(player.getSelectedSkillingItem(), barsRequired);
			int x = 5;
			if(x > (player.getInventory().getAmount(bar.getId()) / barsRequired))
				x = (player.getInventory().getAmount(bar.getId()) / barsRequired);
			EquipmentMaking.smithItem(player, new Item(player.getSelectedSkillingItem(), barsRequired), new Item(item.getId(), SmithingData.getItemAmount(item)), x);
			break;
		}

		if(BeastOfBurden.beastOfBurdenSlot(interfaceId) >= 0) {
			if(player.getInterfaceId() == BeastOfBurden.INTERFACE_ID && player.getSummoning().getBeastOfBurden() != null) {
				player.getSummoning().getBeastOfBurden().switchItem(player.getInventory(), new Item(id, 5), BeastOfBurden.beastOfBurdenSlot(interfaceId), false, true);
			}
		} else if(PriceChecker.priceCheckerSlot(interfaceId) >= 0) {
			if(player.getPriceChecker().isOpen()) {
				player.getPriceChecker().switchItem(player.getInventory(), new Item(id, 5), PriceChecker.priceCheckerSlot(interfaceId), false, true);
			}
		}
	}

	/**
	 * Manages an item's third action.
	 * @param player	The player clicking the item.
	 * @param packet	The packet to read values from.
	 */
	private static void thirdAction(Player player, Packet packet) {
		int interfaceId = packet.readLEShort();
		int id = packet.readShortA();
		int slot = packet.readShortA();
		Item item1 = new Item(id);
		switch (interfaceId) {
		case Equipment.INVENTORY_INTERFACE_ID:
			if(!player.getEquipment().contains(id))
				return;
			switch(id) {
			case 14022:
			case 14024:
			case 14019:
				player.getPacketSender().sendInterface(60000);
				break;
			case 13661:
				player.setInfernoSmelt(!player.isInfernoSmelt());
				player.sendMessage("Your inferno adze is now set to "+(player.isInfernoSmelt() ? "smelt" : "not smelt")+" ores.");
				break;
			case 1712:
			case 1710:
			case 1708:
			case 1706:
			case 11118:
			case 11120:
			case 11122:
			case 11124:
				JewelryTeleporting.rub(player, id);
				break;
			case 1704:
				player.getPacketSender().sendMessage("Your amulet has run out of charges.");
				break;
			case 11126:
				player.getPacketSender().sendMessage("Your bracelet has run out of charges.");
				break;
			case 11283:
				int charges = player.getDfsCharges();
				if(charges > 0 ) {
					if(player.getCombatBuilder().isAttacking())
						CombatFactory.handleDragonFireShield(player, player.getCombatBuilder().getVictim());
					else
						player.getPacketSender().sendMessage("You can only use this in combat.");
				} else
					player.getPacketSender().sendMessage("Your shield doesn't have enough power yet. It has "+player.getDfsCharges()+"/20 dragon-fire charges.");
				break;
			}
			break;
		case Trading.INTERFACE_ID:
			if(player.getTrading().inTrade()) {
				player.getTrading().tradeItem(id, 10, slot);
			} else if(Dueling.checkDuel(player, 1) || Dueling.checkDuel(player, 2)) {
				player.getDueling().stakeItem(id, 10, slot);
			}  else if(player.getGamble().inGambling()) {
				player.getGamble().tradeItem(id, 10, slot);
				Player player2 = World.getPlayers().get(player.getGamble().getGambleWith());
				player2.getGamble().gambleConfirmed2 = false;
				player.getGamble().gambleConfirmed = false;
				player.getGamble().gambleConfirmed2 = false;
				player2.getGamble().gambleConfirmed = false;
				player.getPacketSender().sendString(45012, "");
				player2.getPacketSender().sendString(45012, "");
			}
			break;
		case Trading.INTERFACE_REMOVAL_ID:
			if(player.getTrading().inTrade() || player.getGamble().inGambling())
				player.getTrading().removeTradedItem(id, 10);
			Player player2 = World.getPlayers().get(player.getGamble().getGambleWith());
			player2.getGamble().gambleConfirmed2 = false;
			player.getGamble().gambleConfirmed = false;
			player.getGamble().gambleConfirmed2 = false;
			player2.getGamble().gambleConfirmed = false;
			player.getPacketSender().sendString(45012, "");
			player2.getPacketSender().sendString(45012, "");
			break;
/*		case Gamble.INTERFACE_ID:
			if(player.getGamble().inGambling()) {
				player.getGamble().tradeItem(id, 10, slot);
			}
			break;
		case Gamble.INTERFACE_REMOVAL_ID:
			if(player.getGamble().inGambling())
				player.getGamble().removeTradedItem(id, 10);
			break;*/
		case Dueling.INTERFACE_REMOVAL_ID:
			if(Dueling.checkDuel(player, 1) || Dueling.checkDuel(player, 2)) {
				player.getDueling().removeStakedItem(id, 10);
				return;
			}
			break;
		case Bank.INTERFACE_ID:
			if (!player.isBanking() || player.getInterfaceId() != 5292)
				return;
			player.getBank(player.getCurrentBankTab()).switchItem(player.getInventory(), new Item(id, 10), slot, true, true);
			player.getBank(player.getCurrentBankTab()).open();
			break;
		case Bank.INVENTORY_INTERFACE_ID:
			Item item = player.getInventory().forSlot(slot).copy().setAmount(10).copy();
			if (!player.isBanking() || item.getId() != id || !player.getInventory().contains(item.getId()) || player.getInterfaceId() != 5292)
				return;
			player.setCurrentBankTab(Bank.getTabForItem(player, item.getId()));
			player.getInventory().switchItem(player.getBank(player.getCurrentBankTab()), item, slot, false, true);
			break;
		case Shop.ITEM_CHILD_ID:
			if(player.getShop() == null)
				return;
			item = player.getShop().forSlot(slot).copy().setAmount(5).copy();
			player.getShop().setPlayer(player).switchItem(player.getInventory(), item, slot, false, true);
			break;
		case Shop.INVENTORY_INTERFACE_ID:
			if(player.isShopping()) {
				player.getShop().sellItem(player, slot, 5);
				return;
			}
			break;
		case BeastOfBurden.INTERFACE_ID:
			if(player.getInterfaceId() == BeastOfBurden.INTERFACE_ID && player.getSummoning().getBeastOfBurden() != null) {
				Item storeItem = new Item(id, 10);
				if(storeItem.getDefinition().isStackable()){
					player.getPacketSender().sendMessage("You cannot store stackable items.");
					return;
				}
				player.getInventory().switchItem(player.getSummoning().getBeastOfBurden(), storeItem, slot, false, true);
			}
			break;
		case PriceChecker.INTERFACE_PC_ID:
			if(player.getInterfaceId() == PriceChecker.INTERFACE_ID && player.getPriceChecker().isOpen()) {
				player.getInventory().switchItem(player.getPriceChecker(), new Item(id, 10), slot, false, true);
			}
			break;
		case 1119: //smithing interface row 1
		case 1120: // row 2
		case 1121: // row 3
		case 1122: // row 4
		case 1123: // row 5
			int barsRequired = SmithingData.getBarAmount(item1);
			Item bar = new Item(player.getSelectedSkillingItem(), barsRequired);
			int x = 10;
			if(x > (player.getInventory().getAmount(bar.getId()) / barsRequired))
				x = (player.getInventory().getAmount(bar.getId()) / barsRequired);
			EquipmentMaking.smithItem(player, new Item(player.getSelectedSkillingItem(), barsRequired), new Item(item1.getId(), SmithingData.getItemAmount(item1)), x);
			break;
		}

		if(BeastOfBurden.beastOfBurdenSlot(interfaceId) >= 0) {
			if(player.getInterfaceId() == BeastOfBurden.INTERFACE_ID && player.getSummoning().getBeastOfBurden() != null) {
				player.getSummoning().getBeastOfBurden().switchItem(player.getInventory(), new Item(id, 10), BeastOfBurden.beastOfBurdenSlot(interfaceId), false, true);
			}
		} else if(PriceChecker.priceCheckerSlot(interfaceId) >= 0) {
			if(player.getPriceChecker().isOpen()) {
				player.getPriceChecker().switchItem(player.getInventory(), new Item(id, 10), PriceChecker.priceCheckerSlot(interfaceId), false, true);
			}
		}
	}

	/**
	 * Manages an item's fourth action.
	 * @param player	The player clicking the item.
	 * @param packet	The packet to read values from.
	 */
	private static void fourthAction(Player player, Packet packet) {
		int slot = packet.readShortA();
		int interfaceId = packet.readShort();
		int id = packet.readShortA();
		switch (interfaceId) {
		case Trading.INTERFACE_ID:
			if(player.getTrading().inTrade()) {
				player.getTrading().tradeItem(id, player.getInventory().getAmount(id), slot);
			} else if(Dueling.checkDuel(player, 1) || Dueling.checkDuel(player, 2)) {
				player.getDueling().stakeItem(id, player.getInventory().getAmount(id), slot);
			} else if(player.getGamble().inGambling()) {
				player.getGamble().tradeItem(id, player.getInventory().getAmount(id), slot);
				Player player2 = World.getPlayers().get(player.getGamble().getGambleWith());
				player2.getGamble().gambleConfirmed2 = false;
				player.getGamble().gambleConfirmed = false;
				player.getGamble().gambleConfirmed2 = false;
				player2.getGamble().gambleConfirmed = false;
				player.getPacketSender().sendString(45012, "");
				player2.getPacketSender().sendString(45012, "");
			}
			break;
		case Trading.INTERFACE_REMOVAL_ID:
			if(player.getTrading().inTrade() || player.getGamble().inGambling()) {
				for(Item item: player.getTrading().offeredItems) {
					if(item != null && item.getId() == id) {
						player.getTrading().removeTradedItem(id, item.getAmount());
						
						if(ItemDefinition.forId(id) != null && ItemDefinition.forId(id).isStackable())
							break;
					}
				}
				Player player2 = World.getPlayers().get(player.getGamble().getGambleWith());
				player2.getGamble().gambleConfirmed2 = false;
				player.getGamble().gambleConfirmed = false;
				player.getGamble().gambleConfirmed2 = false;
				player2.getGamble().gambleConfirmed = false;
				player.getPacketSender().sendString(45012, "");
				player2.getPacketSender().sendString(45012, "");
			}
			break;
/*		case Gamble.INTERFACE_ID:
			if(player.getGamble().inGambling()) {
				player.getGamble().tradeItem(id, player.getInventory().getAmount(id), slot);
			}
			break;*/
/*		case Gamble.INTERFACE_REMOVAL_ID:
			if(player.getGamble().inGambling()) {
				for(Item item: player.getGamble().offeredItems) {
					if(item != null && item.getId() == id) {
						player.getGamble().removeTradedItem(id, item.getAmount());
						if(ItemDefinition.forId(id) != null && ItemDefinition.forId(id).isStackable())
							break;
					}
				}
			}
			break;*/
		case Dueling.INTERFACE_REMOVAL_ID:
			if(Dueling.checkDuel(player, 1) || Dueling.checkDuel(player, 2)) {
				for(Item item: player.getDueling().stakedItems) {
					if(item != null && item.getId() == id) {
						player.getDueling().removeStakedItem(id, item.getAmount());
						if(ItemDefinition.forId(id) != null && ItemDefinition.forId(id).isStackable())
							break;
					}
				}
			}
			break;
		case Bank.INTERFACE_ID:
			if (!player.isBanking() || player.getBank(Bank.getTabForItem(player, id)).getAmount(id) <= 0 || player.getInterfaceId() != 5292)
				return;
			player.getBank(player.getCurrentBankTab()).switchItem(player.getInventory(), new Item(id, player.getBank(Bank.getTabForItem(player, id)).getAmount(id)), slot, true, true);
			player.getBank(player.getCurrentBankTab()).open();
			break;
		case Bank.INVENTORY_INTERFACE_ID:
			Item item = player.getInventory().forSlot(slot).copy().setAmount(player.getInventory().getAmount(id));
			if (!player.isBanking() || item.getId() != id || !player.getInventory().contains(item.getId()) || player.getInterfaceId() != 5292)
				return;
			player.setCurrentBankTab(Bank.getTabForItem(player, item.getId()));
			player.getInventory().switchItem(player.getBank(player.getCurrentBankTab()), item, slot, false, true);
			break;
		case Shop.ITEM_CHILD_ID:
			if(player.getShop() == null)
				return;
			item = player.getShop().forSlot(slot).copy().setAmount(10).copy();
			player.getShop().setPlayer(player).switchItem(player.getInventory(), item, slot, true, true);
			break;
		case Shop.INVENTORY_INTERFACE_ID:
			if(player.isShopping()) {
				player.getShop().sellItem(player,slot, 10);
				return;
			}
			break;
		case BeastOfBurden.INTERFACE_ID:
			if(player.getInterfaceId() == BeastOfBurden.INTERFACE_ID && player.getSummoning().getBeastOfBurden() != null) {
				Item storeItem = new Item(id, 29);
				if(storeItem.getDefinition().isStackable()){
					player.getPacketSender().sendMessage("You cannot store stackable items.");
					return;
				}
				player.getInventory().switchItem(player.getSummoning().getBeastOfBurden(), storeItem, slot, false, true);
			}
			break;
		case PriceChecker.INTERFACE_PC_ID:
			if(player.getInterfaceId() == PriceChecker.INTERFACE_ID && player.getPriceChecker().isOpen()) {
				player.getInventory().switchItem(player.getPriceChecker(), new Item(id, player.getInventory().getAmount(id)), slot, false, true);
			}
			break;
		}

		if(BeastOfBurden.beastOfBurdenSlot(interfaceId) >= 0) {
			if(player.getInterfaceId() == BeastOfBurden.INTERFACE_ID && player.getSummoning().getBeastOfBurden() != null) {
				player.getSummoning().getBeastOfBurden().switchItem(player.getInventory(), new Item(id, 29), BeastOfBurden.beastOfBurdenSlot(interfaceId), false, true);
			}
		} else if(PriceChecker.priceCheckerSlot(interfaceId) >= 0) {
			if(player.getPriceChecker().isOpen()) {
				player.getPriceChecker().switchItem(player.getInventory(), new Item(id, player.getPriceChecker().getAmount(id)), PriceChecker.priceCheckerSlot(interfaceId), false, true);
			}
		}
	}


	/**
	 * Manages an item's fifth action.
	 * @param player	The player clicking the item.
	 * @param packet	The packet to read values from.
	 */
	private static void fifthAction(Player player, Packet packet) {
		int slot = packet.readLEShort();
		int interfaceId = packet.readShortA();
		int id = packet.readLEShort();
		switch (interfaceId) {

		case Trading.INTERFACE_ID:
			if(player.getTrading().inTrade()) {
				player.setInputHandling(new EnterAmountToTrade(id, slot));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to trade?");
			} else if(Dueling.checkDuel(player, 1) || Dueling.checkDuel(player, 2)) {
				player.setInputHandling(new EnterAmountToStake(id, slot));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to stake?");
			}  else if(player.getGamble().inGambling()) {
				player.setInputHandling(new EnterAmountToTrade(id, slot));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to gamble?");
				Player player2 = World.getPlayers().get(player.getGamble().getGambleWith());
				player2.getGamble().gambleConfirmed2 = false;
				player.getGamble().gambleConfirmed = false;
				player.getGamble().gambleConfirmed2 = false;
				player2.getGamble().gambleConfirmed = false;
				player.getPacketSender().sendString(45012, "");
				player2.getPacketSender().sendString(45012, "");
			}
			break;
		case Trading.INTERFACE_REMOVAL_ID:
			if(player.getTrading().inTrade() ) {
				player.setInputHandling(new EnterAmountToRemoveFromTrade(id));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to remove?");
			} else if (player.getGamble().inGambling() ) {
				player.setInputHandling(new EnterAmountToRemoveFromTrade(id));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to remove?");
				Player player2 = World.getPlayers().get(player.getGamble().getGambleWith());
				player2.getGamble().gambleConfirmed2 = false;
				player.getGamble().gambleConfirmed = false;
				player.getGamble().gambleConfirmed2 = false;
				player2.getGamble().gambleConfirmed = false;
				player.getPacketSender().sendString(45012, "");
				player2.getPacketSender().sendString(45012, "");
			}
			break;
/*		case Gamble.INTERFACE_ID:
			if(player.getGamble().inGambling()) {
				player.setInputHandling(new EnterAmountToTrade(id, slot));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to trade?");
			}
			break;
		case Gamble.INTERFACE_REMOVAL_ID:
			if(player.getGamble().inGambling()) {
				player.setInputHandling(new EnterAmountToRemoveFromTrade(id));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to remove?");
			}
			break;*/
		case Dueling.INTERFACE_REMOVAL_ID:
			if(Dueling.checkDuel(player, 1) || Dueling.checkDuel(player, 2)) {
				player.setInputHandling(new EnterAmountToRemoveFromStake(id));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to remove?");
			}
			break;
		case Bank.INVENTORY_INTERFACE_ID: //BANK X
			if(player.isBanking()) {
				player.setInputHandling(new EnterAmountToBank(id, slot));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to bank?");
			}
			break;
		case Bank.INTERFACE_ID:
		case 11:
			if(player.isBanking()) {
				if(interfaceId == 11) {
					player.setInputHandling(new EnterAmountToRemoveFromBank(id, slot));
					player.getPacketSender().sendEnterAmountPrompt("How many would you like to withdraw?");
				} else {
					player.getBank(player.getCurrentBankTab()).switchItem(player.getInventory(), new Item(id, player.getBank(Bank.getTabForItem(player, id)).getAmount(id) - 1), slot, true, true);
					player.getBank(player.getCurrentBankTab()).open();
				}
			}
			break;
		case Shop.ITEM_CHILD_ID:
			if(player.isBanking())
				return;
			if(player.isShopping()) {
				player.setInputHandling(new EnterAmountToBuyFromShop(id, slot));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to buy?");
				player.getShop().setPlayer(player);
			}
			break;
		case Shop.INVENTORY_INTERFACE_ID:
			if(player.isBanking())
				return;
			if(player.isShopping()) {
				player.setInputHandling(new EnterAmountToSellToShop(id, slot));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to sell?");
				player.getShop().setPlayer(player);
			}
			break;
		case PriceChecker.INTERFACE_PC_ID:
			if(player.getInterfaceId() == PriceChecker.INTERFACE_ID && player.getPriceChecker().isOpen()) {
				player.setInputHandling(new EnterAmountToPriceCheck(id, slot));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to pricecheck?");
			}
			break;
		case BeastOfBurden.INTERFACE_ID:
			if(player.getInterfaceId() == BeastOfBurden.INTERFACE_ID && player.getSummoning().getBeastOfBurden() != null) {
				Item storeItem = new Item(id, 10);
				if(storeItem.getDefinition().isStackable()){
					player.getPacketSender().sendMessage("You cannot store stackable items.");
					return;
				}
				player.setInputHandling(new EnterAmountToStore(id, slot));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to store?");
			}
			break;
		}

		if(BeastOfBurden.beastOfBurdenSlot(interfaceId) >= 0) {
			if(player.getInterfaceId() == BeastOfBurden.INTERFACE_ID && player.getSummoning().getBeastOfBurden() != null) {
				player.setInputHandling(new EnterAmountToRemoveFromBob(id, slot));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to remove?");
			}
		} else if(PriceChecker.priceCheckerSlot(interfaceId) >= 0) {
			if(player.getPriceChecker().isOpen()) {
				player.setInputHandling(new EnterAmountToRemoveFromPriceCheck(id, slot));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to remove?");
			}
		}
	}
	
	private static void sixthAction(Player player, Packet packet) {
		int interfaceId = packet.readShortA();
		int slot = packet.readShortA();
		int id = packet.readShortA();
		switch (interfaceId) {
		case Shop.INVENTORY_INTERFACE_ID:
			if(player.isShopping()) {
				player.getShop().sellItem(player, slot, player.getInventory().getAmount(id));
				return;
			}
			break;
		}
	}

	@Override
	public void handleMessage(Player player, Packet packet) {
		if (player.getConstitution() <= 0)
			return;
		if(player.hasRights(PlayerRights.OWNER)){
			player.getPacketSender().sendMessage("Packet: "+packet.getOpcode()+" int: "+packet.getLength());
		}
		switch (packet.getOpcode()) {
		case FIRST_ITEM_ACTION_OPCODE:
			firstAction(player, packet);
			break;
		case SECOND_ITEM_ACTION_OPCODE:
			secondAction(player, packet);
			break;
		case THIRD_ITEM_ACTION_OPCODE:
			thirdAction(player, packet);
			break;
		case FOURTH_ITEM_ACTION_OPCODE:
			fourthAction(player, packet);
			break;
		case FIFTH_ITEM_ACTION_OPCODE:
			fifthAction(player, packet);
			break;
		case SIXTH_ITEM_ACTION_OPCODE:
			sixthAction(player, packet);
			break;
		}
	}

	public static final int FIRST_ITEM_ACTION_OPCODE = 145;
	public static final int SECOND_ITEM_ACTION_OPCODE = 117;
	public static final int THIRD_ITEM_ACTION_OPCODE = 43;
	public static final int FOURTH_ITEM_ACTION_OPCODE = 129;
	public static final int FIFTH_ITEM_ACTION_OPCODE = 135;
	public static final int SIXTH_ITEM_ACTION_OPCODE = 138;
}
