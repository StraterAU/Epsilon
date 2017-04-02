package com.epsilon.world.content.dialogue.impl;

import com.epsilon.world.content.dialogue.Dialogue;
import com.epsilon.world.content.dialogue.DialogueExpression;
import com.epsilon.world.content.dialogue.DialogueType;
import com.epsilon.world.entity.impl.player.Player;

public class DiceBag {

	public static final String ATTRIBUTE = "DICE_BAG_ID";
	
	public static Dialogue getDialogue(Player player, int id) {
		
		switch(id) {
		case 0:
			return new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.OPTION;
				}

				@Override
				public DialogueExpression animation() {
					return null;
				}

				@Override
				public String[] dialogue() {
					return new String[] { "One 6-sided die", "Two 6-sided dice",
							"One 4-sided die", "One 8-sided die", "More..." };
				}
				
				@Override
				public void specialAction() {
					player.setDialogueActionId(147);
				}
				
			};
		case 1:
			return new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.OPTION;
				}

				@Override
				public DialogueExpression animation() {
					return null;
				}

				@Override
				public String[] dialogue() {
					return new String[] { "One 10-sided die", "Two 12-sided dice",
							"One 20-sided die", "Two 10-sided dice for 1-100",
							"Back..." };
				}
				
				@Override
				public void specialAction() {
					player.setDialogueActionId(148);
				}
				
			};
		}
		
		return null;
		
	}
	
	public static void replaceDice(Player player, int newDice) {
		
		int current = player.getAttribute(ATTRIBUTE, -1);
		
		player.getPacketSender().sendInterfaceRemoval();
		
		if(current == -1) {
			return;
		}
		
		player.getInventory().delete(current, 1);
		player.getInventory().add(newDice, 1);
		
	}
	
}
