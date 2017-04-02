package com.epsilon.world.content.dialogue.impl;

import com.epsilon.world.content.dialogue.Dialogue;
import com.epsilon.world.content.dialogue.DialogueExpression;
import com.epsilon.world.content.dialogue.DialogueManager;
import com.epsilon.world.content.dialogue.DialogueType;
import com.epsilon.world.entity.impl.player.Player;

public class Banker {

	public static Dialogue get(Player player, int id) {
		switch(id) {
		case 0:
			player.setDialogueActionId(144);
			return DialogueManager.getDialogues().get(144);
		}
		return null;
	}
	
}
