package com.epsilon.net.packet.impl;

import com.epsilon.model.CombatIcon;
import com.epsilon.model.GameMode;
import com.epsilon.model.Graphic;
import com.epsilon.model.GroundItem;
import com.epsilon.model.Hit;
import com.epsilon.model.Hitmask;
import com.epsilon.model.Item;
import com.epsilon.net.packet.Packet;
import com.epsilon.net.packet.PacketListener;
import com.epsilon.world.content.PlayerLogs;
import com.epsilon.world.content.Sounds;
import com.epsilon.world.content.Sounds.Sound;
import com.epsilon.world.content.skill.impl.dungeoneering.ItemBinding;
import com.epsilon.world.content.skill.impl.summoning.SummoningData;
import com.epsilon.world.entity.impl.GroundItemManager;
import com.epsilon.world.entity.impl.player.Player;

/**
 * This packet listener is called when a player drops an item they
 * have placed in their inventory.
 * 
 * @author relex lawl
 */

public class DropItemPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		int id = packet.readUnsignedShortA();
		@SuppressWarnings("unused")
		int interfaceIndex = packet.readUnsignedShort();
		int itemSlot = packet.readUnsignedShortA();
		if (player.getConstitution() <= 0 || player.getInterfaceId() > 0)
			return;
		if(itemSlot < 0 || itemSlot > player.getInventory().capacity())
			return;
		if(player.getConstitution() <= 0 || player.isTeleporting())
			return;
		Item item = player.getInventory().getItems()[itemSlot];
		if(item.getId() != id) {
			return;
		}
		player.getPacketSender().sendInterfaceRemoval();
		player.getCombatBuilder().cooldown(false);
		if (item != null && item.getId() != -1 && item.getAmount() >= 1) {
			if(item.tradeable() && !ItemBinding.isBoundItem(item.getId())) {
				player.getInventory().setItem(itemSlot, new Item(-1, 0)).refreshItems();
				if(item.getId() == 4045) {
					player.dealDamage(new Hit((player.getConstitution() - 1) == 0 ? 1 : player.getConstitution() - 1, Hitmask.CRITICAL, CombatIcon.BLUE_SHIELD));
					player.performGraphic(new Graphic(1750));
					player.getPacketSender().sendMessage("The potion explodes in your face as you drop it!");
				} else {
					GroundItemManager.spawnGroundItem(player, new GroundItem(item, player.getPosition().copy(), player.getUsername(), player.getHostAddress(), false, 80, player.getGameMode() == GameMode.LEGEND && player.getPosition().getZ() >= 0 && player.getPosition().getZ() < 4 ? true : false, 80));
					PlayerLogs.log(player.getUsername(), "Player dropping item: "+item.getId()+", amount: "+item.getAmount());
				}
				Sounds.sendSound(player, Sound.DROP_ITEM);
			} else if (!SummoningData.isPouch(player, item.getId(), 3))
				destroyItemInterface(player, item);
		}
	}

	public static void destroyItemInterface(Player player, Item item) {//Destroy item created by Remco
		player.setUntradeableDropItem(item);
		String[][] info = {//The info the dialogue gives
				{ "Are you sure you want to discard this item?", "14174" },
				{ "Yes.", "14175" }, { "No.", "14176" }, { "", "14177" },
				{"This item will vanish once it hits the floor.", "14182" }, {"You cannot get it back if discarded.", "14183" },
				{ item.getDefinition().getName(), "14184" } };
		player.getPacketSender().sendItemOnInterface(14171, item.getId(), 0, item.getAmount());
		for (int i = 0; i < info.length; i++)
			player.getPacketSender().sendString(Integer.parseInt(info[i][1]), info[i][0]);
		player.getPacketSender().sendChatboxInterface(14170);
	}
}
