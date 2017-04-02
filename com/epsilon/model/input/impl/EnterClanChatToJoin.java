package com.epsilon.model.input.impl;

import com.epsilon.model.input.Input;
import com.epsilon.world.content.clan.ClanChatManager;
import com.epsilon.world.entity.impl.player.Player;

public class EnterClanChatToJoin extends Input {

	@Override
	public void handleSyntax(Player player, String syntax) {
		if(syntax.length() <= 1) {
			player.getPacketSender().sendMessage("Invalid syntax entered.");
			return;
		}
		ClanChatManager.join(player, syntax);
	}
}
