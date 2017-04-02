package com.epsilon.model.input.impl;

import com.epsilon.model.input.EnterAmount;
import com.epsilon.world.content.WellOfGoodwill;
import com.epsilon.world.entity.impl.player.Player;

public class DonateToWell extends EnterAmount {

	@Override
	public void handleAmount(Player player, int amount) {
		WellOfGoodwill.donate(player, amount);
		player.getPacketSender().sendString(50056, "@or1@Well of Goodwill: " + (WellOfGoodwill.isActive() ? "@gre@Active" : "@red@Inactive"));
	}

}
