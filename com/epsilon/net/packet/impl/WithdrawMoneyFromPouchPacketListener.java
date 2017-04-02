package com.epsilon.net.packet.impl;

import com.epsilon.net.packet.Packet;
import com.epsilon.net.packet.PacketListener;
import com.epsilon.world.content.MoneyPouch;
import com.epsilon.world.entity.impl.player.Player;

public class WithdrawMoneyFromPouchPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		int amount = packet.readInt();
		boolean tickets = packet.readByte() == 1;
		if(tickets) {
			
			int available = 0;
			long total = player.getMoneyInPouch();
			
			for(;;) {
				if(total >= Integer.MAX_VALUE) {
					available++;
					total -= Integer.MAX_VALUE;
				} else {
					break;
				}
			}
			
			if(amount >= available) {
				amount = available;
			}
			
			if(player.getInventory().getFreeSlots() > 0 || player.getInventory().contains(5020)) {
				player.getInventory().add(5020, amount);
				player.setMoneyInPouch(player.getMoneyInPouch() - (long) Integer.MAX_VALUE * amount);
				player.getPacketSender().sendString(8135, ""+player.getMoneyInPouch());
			}
			
		}  else {
			MoneyPouch.withdrawMoney(player, amount);
		}
	}

}
