package com.epsilon.world.content;

import java.util.Optional;

import com.epsilon.GameSettings;
import com.epsilon.engine.task.Task;
import com.epsilon.model.Animation;
import com.epsilon.model.GameMode;
import com.epsilon.model.Graphic;
import com.epsilon.model.Item;
import com.epsilon.model.Skill;
import com.epsilon.world.content.combat.magic.MagicSpells;
import com.epsilon.world.content.combat.magic.Spell;
import com.epsilon.world.entity.impl.player.Player;

public class PlankMake {
	
	private static final int REQUIRED_LEVEL = 86;
	private static final int EXPERIENCE = 90;

	private static final int LOGS = 1511;
	private static final int PLANK = 960;

	private static final int OAK_LOG = 1521;
	private static final int OAK_PLANK = 8778;

	private static final int TEAK_LOG = 6333;
	private static final int TEAK_PLANK = 8780;

	private static final int MAHOGANY_LOG = 6332;
	private static final int MAHOGANY_PLANK = 8782;
	
	private static final Animation ANIM = new Animation(6298);
	private static final Graphic GFX = new Graphic(1063, (100 << 16));
	
	private static boolean correctLog(int item) {
		return item == LOGS || item == OAK_LOG || item == TEAK_LOG
			|| item == MAHOGANY_LOG;
	}

	private static final int[][] CONVERT = { { LOGS, PLANK },
		{ OAK_LOG, OAK_PLANK }, { TEAK_LOG, TEAK_PLANK },
		{ MAHOGANY_LOG, MAHOGANY_PLANK }, };
	
	private static final Spell SPELL = MagicSpells.PLANK_MAKE.getSpell();

	public static void handle(Player p, int itemId, int slot) {
		
		if (p.getSkillManager().getCurrentLevel(Skill.MAGIC) < REQUIRED_LEVEL) {
			p.getPacketSender().sendMessage("You don't have the required magic level to cast this spell!");
			p.getPacketSender().sendMessage("You need a magic level of " + REQUIRED_LEVEL + " to cast this spell.");
			return;
		}
		
		if (!correctLog(itemId)) {
			p.getPacketSender().sendMessage("You can't turn that into a plank!");
			return;
		}
		
		if(p.getInventory().contains(itemId)) {
			
			Optional<Item[]> items = SPELL.itemsRequired(p);
			Item[] runes = items.get();
			
			for(Item rune : runes) {
				p.getInventory().delete(rune);
			}
			
			p.performAnimation(ANIM);
			p.performGraphic(GFX);
			
			for(int[] element : CONVERT) {
				if(itemId == element[0]) {
					p.getInventory().delete(new Item(element[0]));
					p.getInventory().add(new Item(element[1]));
					p.getPacketSender().sendMessage("You make the log into a plank!");
					if (p.getExperienceMode() == ExperienceModes.EXTREME)
						p.getSkillManager().addExperience(Skill.MAGIC, EXPERIENCE * 100);
						if (p.getExperienceMode() == ExperienceModes.LEGEND)
							p.getSkillManager().addExperience(Skill.MAGIC, EXPERIENCE * 50);
						if (p.getExperienceMode() == ExperienceModes.IMMORTAL)
							p.getSkillManager().addExperience(Skill.MAGIC, EXPERIENCE * 10);
						if (p.getExperienceMode() == ExperienceModes.GRANDMASTER)
							p.getSkillManager().addExperience(Skill.MAGIC, EXPERIENCE * 2);
						if (p.getExperienceMode() == ExperienceModes.IRONMAN)
							p.getSkillManager().addExperience(Skill.MAGIC, EXPERIENCE * 25);
						if (p.getExperienceMode() == ExperienceModes.HARDCOREIRONMAN)
							p.getSkillManager().addExperience(Skill.MAGIC, EXPERIENCE * 5);
					
					
					
					p.setCurrentTask(new Task(3) {
						@Override
						protected void execute() {
							super.stop();
							p.getPacketSender().sendTab(GameSettings.MAGIC_TAB);
						}
					});
					break;
				}
			}
		}
		
	}
	
}
