package com.epsilon.model.input.impl;

import com.epsilon.model.input.EnterAmount;
import com.epsilon.world.entity.impl.player.Player;

public class WithdrawGembagUpgrade extends EnterAmount {

	@Override
	public void handleAmount(Player player, int amount) {
		
		int size = player.getGemBagUpgrade().getGems().size();
		
		if(size > amount) {
			amount = size;
		}
		
		if(player.getInventory().getFreeSlots() > amount) {
			amount = player.getInventory().getFreeSlots();
		}
		
		if(amount > 0) {
			for(int i = 0; !player.getGemBagUpgrade().getGems().isEmpty() && i < amount; i++) {
				player.getInventory().add(player.getGemBagUpgrade().getGems().poll(), 1);
			}
			player.sendMessage(amount+" have been emptied in your inventory.");
		}
		
	}
	
}
