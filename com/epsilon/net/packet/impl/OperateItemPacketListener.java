package com.epsilon.net.packet.impl;

import com.epsilon.model.Item;
import com.epsilon.net.packet.Packet;
import com.epsilon.net.packet.PacketListener;
import com.epsilon.world.entity.impl.player.Player;

public class OperateItemPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		
		int interfaceId = packet.readLEShort();
		int itemId = packet.readUnsignedShortA();
		int slot = packet.readUnsignedShortA();
		
		System.out.println(slot);
		
		if(slot < 0 || slot >= player.getEquipment().capacity()) {
			return;
		}
		
		Item item = player.getEquipment().get(slot);
		
		System.out.println(item);
		
		if(item != null) {
			switch(item.getId()) {
			case 14022:
			case 14024:
			case 14019:
				player.getPacketSender().sendInterface(60000);
				break;
			}
		}
		
	}

}
