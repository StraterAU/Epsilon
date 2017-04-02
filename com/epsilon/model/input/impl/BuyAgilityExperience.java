package com.epsilon.model.input.impl;

import com.epsilon.model.GameMode;
import com.epsilon.model.Skill;
import com.epsilon.model.input.EnterAmount;
import com.epsilon.world.content.ExperienceModes;
import com.epsilon.world.entity.impl.player.Player;

public class BuyAgilityExperience extends EnterAmount {

	@Override
	public void handleAmount(Player player, int amount) {
		player.getPacketSender().sendInterfaceRemoval();
		int ticketAmount = player.getInventory().getAmount(2996);
		if(ticketAmount == 0) {
			player.getPacketSender().sendMessage("You do not have any tickets.");
			return;
		}
		if(ticketAmount > amount) {
			ticketAmount = amount;
		}
		
		if(player.getInventory().getAmount(2996) < ticketAmount) {
			return;
		}
			int exp = ticketAmount * 150;
			
		player.getInventory().delete(2996, ticketAmount);
		if (player.getExperienceMode() == ExperienceModes.EXTREME)
			player.getSkillManager().addExperience(Skill.AGILITY, exp * 100);
			if (player.getExperienceMode() == ExperienceModes.LEGEND)
				player.getSkillManager().addExperience(Skill.AGILITY, exp * 50);
			if (player.getExperienceMode() == ExperienceModes.IMMORTAL)
				player.getSkillManager().addExperience(Skill.AGILITY, exp * 10);
			if (player.getExperienceMode() == ExperienceModes.GRANDMASTER)
				player.getSkillManager().addExperience(Skill.AGILITY, exp * 2);
			if (player.getExperienceMode() == ExperienceModes.IRONMAN)
				player.getSkillManager().addExperience(Skill.AGILITY, exp * 25);
			if (player.getExperienceMode() == ExperienceModes.HARDCOREIRONMAN)
				player.getSkillManager().addExperience(Skill.AGILITY, exp * 5);
		
		player.getPacketSender().sendMessage("You've bought "+exp+" agility experience for "+ticketAmount+" agility ticket"+(ticketAmount == 1 ? "" : "s")+".");
	}

}
