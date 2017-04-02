package com.epsilon.model.input.impl;

import com.epsilon.model.container.impl.Bank.BankSearchAttributes;
import com.epsilon.model.input.Input;
import com.epsilon.world.entity.impl.player.Player;

public class EnterSyntaxToBankSearchFor extends Input {

	@Override
	public void handleSyntax(Player player, String syntax) {
		boolean searchingBank = player.isBanking() && player.getBankSearchingAttribtues().isSearchingBank();
		if(searchingBank)
			BankSearchAttributes.beginSearch(player, syntax);
	}
}
