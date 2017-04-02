package com.epsilon.model.input.impl;

import com.epsilon.model.input.EnterAmount;
import com.epsilon.world.content.skill.impl.crafting.Gems;
import com.epsilon.world.content.skill.impl.fletching.BoltTips;
import com.epsilon.world.entity.impl.player.Player;

public class EnterAmountOfGemsToBolt extends EnterAmount{

	@Override
	public void handleAmount(Player player, int amount) {
		if(player.getSelectedSkillingItem() > 0)
			BoltTips.cutGem(player, amount, player.getSelectedSkillingItem());
	}
}
