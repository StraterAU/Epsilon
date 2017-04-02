package com.epsilon.net.packet.impl;

import com.epsilon.model.Skill;
import com.epsilon.model.definitions.ItemDefinition;
import com.epsilon.net.packet.Packet;
import com.epsilon.net.packet.PacketListener;
import com.epsilon.world.entity.impl.player.Player;

public class PrestigeSkillPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		int prestigeId = packet.readShort();
		Skill skill = Skill.forPrestigeId(prestigeId);
		if(skill == null) {
			return;
		}
		if(player.getInterfaceId() > 0) {
			player.getPacketSender().sendMessage("Please close all interfaces before doing this.");
			return;
		}
		if(skill == Skill.RANGED && player.getCannon() != null) {
			player.getPacketSender().sendMessage("Please pick up your dwarf cannon before prestiging.");
			return;
		}
		player.getSkillManager().resetSkill(skill, true);
	}

}
