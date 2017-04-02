package com.epsilon.model.input.impl;

import com.epsilon.model.input.EnterAmount;
import com.epsilon.world.content.grandexchange.GrandExchange;
import com.epsilon.world.entity.impl.player.Player;

public class EnterGePricePerItem extends EnterAmount {

	@Override
	public void handleAmount(Player player, int amount) {
		GrandExchange.setPricePerItem(player, amount);
	}

}
