package com.epsilon.net.packet.impl;

import com.epsilon.net.packet.Packet;
import com.epsilon.net.packet.PacketListener;
import com.epsilon.util.Misc;
import com.epsilon.world.content.PlayerPunishment;
import com.epsilon.world.content.clan.ClanChatManager;
import com.epsilon.world.content.dialogue.DialogueManager;
import com.epsilon.world.entity.impl.player.Player;

public class SendClanChatMessagePacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		String clanMessage = Misc.readString(packet.getBuffer());
		if(clanMessage == null || clanMessage.length() < 1)
			return;
		if(PlayerPunishment.muted(player.getUsername()) || PlayerPunishment.IPMuted(player.getHostAddress())) {
			player.getPacketSender().sendMessage("You are muted and cannot chat.");
			return;
		}
		long totalPlayTime = (player.getTotalPlayTime() + player.getRecordedLogin().elapsed());
		int sec = (int) (totalPlayTime / 1000), h = sec / 3600, m = sec / 60 % 60;
		if(!(h > 0 || m >= 10) || player.getSkillManager().getTotalLevel() < 150) {
			player.getPacketSender().sendMessage("You need at least 10 minutes of game time and a total level of 150 to use");
			player.getPacketSender().sendMessage("use the clan chat.");
			return;
		}
		ClanChatManager.sendMessage(player, clanMessage);
	}

}
