package com.epsilon.net.packet.impl;

import com.epsilon.model.PlayerRights;
import com.epsilon.net.packet.Packet;
import com.epsilon.net.packet.PacketListener;
import com.epsilon.world.World;
import com.epsilon.world.entity.impl.player.Player;

//CALLED EVERY 3 MINUTES OF INACTIVITY

public class IdleLogoutPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		if(player.hasRights(PlayerRights.MODERATOR) || player.hasRights(PlayerRights.ADMINISTRATOR) || player.hasRights(PlayerRights.OWNER) || player.hasRights(PlayerRights.DEVELOPER))
			return;
		/*if(player.logout() && (player.getSkillManager().getSkillAttributes().getCurrentTask() == null || !player.getSkillManager().getSkillAttributes().getCurrentTask().isRunning())) {
			World.getPlayers().remove(player);
		}*/
		//player.setInactive(true);
		//World.getPlayers().remove(player);
	}
}
