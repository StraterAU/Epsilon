package com.epsilon.net.packet.impl;

import com.epsilon.engine.task.impl.WalkToTask;
import com.epsilon.engine.task.impl.WalkToTask.FinalizedMovementTask;
import com.epsilon.model.Locations;
import com.epsilon.model.Locations.Location;
import com.epsilon.net.packet.Packet;
import com.epsilon.net.packet.PacketListener;
import com.epsilon.world.World;
import com.epsilon.world.entity.impl.player.Player;

/**
 * This packet listener is called when a player accepts a trade offer,
 * whether it's from the chat box or through the trading player menu.
 * 
 * @author Taylan Selvi
 */

public class GamblingPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		if (player.getConstitution() <= 0)
			return;
		if(player.isTeleporting())
			return;
		player.getSkillManager().stopSkilling();
		if(player.getLocation() == Location.FIGHT_PITS || player.getLocation() == Location.DUNGEONEERING) {
			player.getPacketSender().sendMessage("You cannot trade other players here.");
			return;
		}
		long totalPlayTime = (player.getTotalPlayTime() + player.getRecordedLogin().elapsed());
		int sec = (int) (totalPlayTime / 1000), h = sec / 3600, m = sec / 60 % 60;
		if(!(h > 0 || m >= 30)) {
			player.getPacketSender().sendMessage("You must have at least 30 minutes of play time to trade another player.");
			return;
		}
		int index = packet.getOpcode() == GAMBLE_OPCODE ? (packet.readShort() & 0xFF) : packet.readLEShort();
		if(index < 0 || index > World.getPlayers().capacity())
			return;
		Player target = World.getPlayers().get(index);
		if (target == null || !Locations.goodDistance(player.getPosition(), target.getPosition(), 13)) 
			return;
		player.setWalkToTask(new WalkToTask(player, target.getPosition(), target.getSize(), new FinalizedMovementTask() {
			@Override
			public void execute() {
				if(target.getIndex() != player.getIndex()) {
					long totalPlayTime = (target.getTotalPlayTime() + target.getRecordedLogin().elapsed());
					int sec = (int) (totalPlayTime / 1000), h = sec / 3600, m = sec / 60 % 60;
					if(!(h > 0 || m >= 30)) {
						player.getPacketSender().sendMessage(target.getUsername()+" must have at least 30 minutes of play time to trade you.");
						return;
					}
					player.getGamble().requestTrade(target);
				}
			}
		}));
	}

	public static final int GAMBLE_OPCODE = 39;
	public static final int CHATBOX_GAMBLE_OPCODE = 139;
}
