package com.epsilon.model.input.impl;

import com.epsilon.model.input.EnterAmount;
import com.epsilon.world.content.skill.impl.herblore.Herblore;
import com.epsilon.world.entity.impl.player.Player;

public class EnterAmountofUnfinishedPotions extends EnterAmount{

	@Override
	public void handleAmount(Player player, int amount) {
		System.out.println(player.getSelectedSkillingItem());
		if(player.getSelectedSkillingItem() > 0) {
			Herblore.makeUnfinishedPotionX(player, player.getSelectedSkillingItem(), amount);
		}
	}
}
