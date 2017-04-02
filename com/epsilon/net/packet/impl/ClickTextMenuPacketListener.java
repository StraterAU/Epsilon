package com.epsilon.net.packet.impl;

import com.epsilon.model.PlayerRights;
import com.epsilon.net.packet.Packet;
import com.epsilon.net.packet.PacketListener;
import com.epsilon.world.content.clan.ClanChatManager;
import com.epsilon.world.content.skill.impl.construction.sawmill.SawmillOperator;
import com.epsilon.world.entity.impl.player.Player;

public class ClickTextMenuPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {

		int interfaceId = packet.readShort();
		int menuId = packet.readByte();

		if(player.hasRights(PlayerRights.OWNER)) {
			player.getPacketSender().sendConsoleMessage("Clicked text menu: "+interfaceId+", menuId: "+menuId);
		}
		
		if(SawmillOperator.handleButtonClick(interfaceId, menuId, player)) {
			return;
		}
		
		if(interfaceId >= 29344 && interfaceId <= 29443) { // Clan chat list
			int index = interfaceId - 29344;
			ClanChatManager.handleMemberOption(player, index, menuId);
		}
		
	}

	public static final int OPCODE = 222;
}
