package com.epsilon.net.packet.impl;

import com.epsilon.model.Flag;
import com.epsilon.net.packet.Packet;
import com.epsilon.net.packet.PacketListener;
import com.epsilon.world.entity.impl.player.Player;

public class ItemColorCustomization implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		
		int itemId = packet.readUnsignedShort();
		int size = packet.readUnsignedByte();
		
		System.out.println(itemId);
		
		switch(itemId) {
		case 14022:
		case 14024:
		case 14019:
			int[] colors = new int[size];
			
			for(int i = 0; i < size; i++) {
				colors[i] = packet.readInt();
			}
			
			player.setMaxCapeColors(colors);
			player.getUpdateFlag().flag(Flag.APPEARANCE);
			player.getPacketSender().sendInterfaceRemoval();
			
			break;
			
		}
		
	}

}
