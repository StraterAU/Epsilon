package com.epsilon.model.input.impl;

import com.epsilon.model.input.EnterAmount;
import com.epsilon.world.content.skill.impl.fletching.Bolts;
import com.epsilon.world.entity.impl.player.Player;

public class EnterAmountOfTipsToBolt extends EnterAmount{

	@Override
	public void handleAmount(Player player, int amount) {
		if(player.getSelectedSkillingItem() > 0){
			Bolts.cutGem(player, amount, player.getSelectedSkillingItem());
			//player.getPacketSender().sendMessage("Chega no handle de ammount");
		}
			
	}
}
