package com.epsilon.model.input.impl;

import com.epsilon.model.input.EnterAmount;
import com.epsilon.world.content.skill.impl.summoning.PouchMaking;
import com.epsilon.world.entity.impl.player.Player;

public class EnterAmountToInfuse extends EnterAmount {

	@Override
	public void handleAmount(Player player, int amount) {
		if(player.getInterfaceId() != 63471) {
			player.getPacketSender().sendInterfaceRemoval();
			return;
		}
		PouchMaking.infusePouches(player, amount);
	}

}
