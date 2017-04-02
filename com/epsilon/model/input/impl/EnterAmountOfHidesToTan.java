package com.epsilon.model.input.impl;

import com.epsilon.model.input.EnterAmount;
import com.epsilon.world.content.skill.impl.crafting.Tanning;
import com.epsilon.world.entity.impl.player.Player;

public class EnterAmountOfHidesToTan extends EnterAmount {

	private int button;
	public EnterAmountOfHidesToTan(int button) {
		this.button = button;
	}
	
	@Override
	public void handleAmount(Player player, int amount) {
		Tanning.tanHide(player, button, amount);
	}

}
