package com.epsilon.world.content.dialogue.impl;

import com.epsilon.model.Skill;
import com.epsilon.model.Locations.Location;
import com.epsilon.model.container.impl.Equipment;
import com.epsilon.model.definitions.WeaponAnimations;
import com.epsilon.model.definitions.WeaponInterfaces;
import com.epsilon.world.content.BonusManager;
import com.epsilon.world.content.combat.prayer.CurseHandler;
import com.epsilon.world.content.combat.prayer.PrayerHandler;
import com.epsilon.world.content.dialogue.Dialogue;
import com.epsilon.world.content.dialogue.DialogueExpression;
import com.epsilon.world.content.dialogue.DialogueManager;
import com.epsilon.world.content.dialogue.DialogueType;
import com.epsilon.world.content.skill.SkillManager;
import com.epsilon.world.entity.impl.player.Player;

public class SirTiffy {
	
	public static final int NPC_ID = 11512;

	public static Dialogue get(Player player, int id, Object... arguments) {
		switch(id) {
		case 0:
			return new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public int npcId() {
					return NPC_ID;
				}

				@Override
				public String[] dialogue() {
					return new String[] { "Hello there, I'm sir Tiffy. How can I help you?" };
				}

				@Override
				public Dialogue nextDialogue() {
					return get(player, 1);
				}
				
			};
		case 1:
			player.setDialogueActionId(190);
			return new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.OPTION;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[] { "Can you reset one of my skills?", "Nevermind" };
				}
				
			};
		case 2:
			return new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.PLAYER_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[] { "Can you reset one of my skills?" };
				}

				@Override
				public Dialogue nextDialogue() {
					return get(player, 3);
				}
				
			};
		case 3:
			return new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public int npcId() {
					return NPC_ID;
				}

				@Override
				public String[] dialogue() {
					return new String[] { "Very well. Please be aware that this cannot be undone", "and your skill will be reset back to level 1." };
				}

				@Override
				public Dialogue nextDialogue() {
					return get(player, 4);
				}
				
			};
		case 4:
			player.setDialogueActionId(191);
			return new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.OPTION;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[] { "Attack", "Defence", "Strength", "Ranged", "Next" };
				}
				
			};
		case 5:
			player.setDialogueActionId(192);
			return new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.OPTION;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[] { "Prayer", "Magic", "Hitpoints", "Summoning", "Nevermind" };
				}
				
			};
		case 6:
			return new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[] { "Your "+((Skill) arguments[0]).name().toLowerCase()+" level has been reset to "+SkillManager.getLevelForExperience(player.getSkillManager().getExperience((Skill) arguments[0]))+"." };
				}
				
			};
		case 7:
			return new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public int npcId() {
					return NPC_ID;
				}

				@Override
				public String[] dialogue() {
					return new String[] { "Please, unequip all of your items before continueing." };
				}

				@Override
				public Dialogue nextDialogue() {
					return get(player, 4);
				}
				
			};
		}
		return null;
	}
	
	public static void reset(Player player, Skill skill) {
		if(player.getEquipment().getFreeSlots() != player.getEquipment().capacity()) {
			DialogueManager.start(player, get(player, 7));
			return;
		}
		player.getSkillManager().setCurrentLevel(skill, skill == Skill.PRAYER ? 10 : skill == Skill.CONSTITUTION ? 100 : 1).setMaxLevel(skill, skill == Skill.PRAYER ? 10 : skill == Skill.CONSTITUTION ? 100 : 1).setExperience(skill, SkillManager.getExperienceForLevel(skill == Skill.CONSTITUTION ? 10 : 1));
		PrayerHandler.deactivateAll(player); 
		CurseHandler.deactivateAll(player); 
		BonusManager.update(player);
		WeaponInterfaces.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
		WeaponAnimations.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
		DialogueManager.start(player, get(player, 6, skill));
	}
	
}
