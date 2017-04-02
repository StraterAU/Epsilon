package com.epsilon.world.content.combat.magic;

import java.util.Optional;

import com.epsilon.model.Animation;
import com.epsilon.model.Graphic;
import com.epsilon.model.GraphicHeight;
import com.epsilon.model.Item;
import com.epsilon.model.Skill;
import com.epsilon.model.Locations.Location;
import com.epsilon.world.entity.impl.Character;
import com.epsilon.world.entity.impl.player.Player;

/**
 * Holds data for all no-combat spells
 * @author Gabriel Hannason
 */
public enum MagicSpells {
	
	BONES_TO_BANANAS(new Spell() {

		@Override
		public int spellId() {
			return 1159;
		}

		@Override
		public int levelRequired() {
			return 15;
		}

		@Override
		public int baseExperience() {
			return 650;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] {new Item(561), new Item(555, 2), new Item(557, 2)});
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public void startCast(Character cast, Character castOn) {
			
			
		}
		
	}),
	LOW_ALCHEMY(new Spell() {

		@Override
		public int spellId() {
			return 1162;
		}

		@Override
		public int levelRequired() {
			return 21;
		}

		@Override
		public int baseExperience() {
			return 4000;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] {new Item(554, 3), new Item(561)});
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}
		
		@Override
		public void startCast(Character cast, Character castOn) {
			
			
		}
		
	}),
	TELEKINETIC_GRAB(new Spell() {

		@Override
		public int spellId() {
			return 1168;
		}

		@Override
		public int levelRequired() {
			return 33;
		}

		@Override
		public int baseExperience() {
			return 3988;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] {new Item(563), new Item(556)});
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}
		
		@Override
		public void startCast(Character cast, Character castOn) {
			
			
		}
		
	}),
	SUPERHEAT_ITEM(new Spell() {

		@Override
		public int spellId() {
			return 1173;
		}

		@Override
		public int levelRequired() {
			return 43;
		}

		@Override
		public int baseExperience() {
			return 6544;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] {new Item(554, 4), new Item(561)});
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}
		
		@Override
		public void startCast(Character cast, Character castOn) {
			
			
		}
		
	}),
	HIGH_ALCHEMY(new Spell() {

		@Override
		public int spellId() {
			return 1178;
		}

		@Override
		public int levelRequired() {
			return 55;
		}

		@Override
		public int baseExperience() {
			return 55000;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] {new Item(554, 5), new Item(561)});
		}
		/**
		 * Mudar para x100 + o cache
		 * @Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] {new Item(554, 500), new Item(561,100)});
		}*/

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public void startCast(Character cast, Character castOn) {
			
			
		}
		
	}),
	BONES_TO_PEACHES(new Spell() {

		@Override
		public int spellId() {
			return 15877;
		}

		@Override
		public int levelRequired() {
			return 60;
		}

		@Override
		public int baseExperience() {
			return 4121;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] {new Item(561, 2), new Item(555, 4), new Item(557, 4)});
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public void startCast(Character cast, Character castOn) {
			
			
		}
		
	}),
	BAKE_PIE(new Spell() {

		@Override
		public int spellId() {
			return 30017;
		}

		@Override
		public int levelRequired() {
			return 65;
		}

		@Override
		public int baseExperience() {
			return 5121;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] {new Item(9075, 1), new Item(554, 5), new Item(555, 4)});
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			
			return Optional.empty();
		}

		@Override
		public void startCast(Character cast, Character castOn) {
			
			
		}
	}),
	PLANK_MAKE(new Spell() {

		@Override
		public int spellId() {
			return 30242;
		}

		@Override
		public int levelRequired() {
			return 86;
		}

		@Override
		public int baseExperience() {
			return 90;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] {new Item(561, 1), new Item(9075, 5), new Item(557, 15)});
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public void startCast(Character cast, Character castOn) {
			
		}
	}),
	VENGEANCE_OTHER(new Spell() {

		@Override
		public int spellId() {
			return 30298;
		}

		@Override
		public int levelRequired() {
			return 93;
		}

		@Override
		public int baseExperience() {
			return 10000;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] {new Item(9075, 3), new Item(557, 10), new Item(560, 2)});
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public void startCast(Character cast, Character castOn) {
			
			
		}
	}),
	VENGEANCE(new Spell() {

		@Override
		public int spellId() {
			return 30306;
		}

		@Override
		public int levelRequired() {
			return 94;
		}

		@Override
		public int baseExperience() {
			return 14000;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[]{new Item(9075, 4), new Item(557, 10), new Item(560, 2)});
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public void startCast(Character cast, Character castOn) {
			
			
		}
	});
	
	MagicSpells(Spell spell) {
		this.spell = spell;
	}
	
	private Spell spell;
	
	public Spell getSpell() {
		return spell;
	}
	
	public static MagicSpells forSpellId(int spellId) {
		for(MagicSpells spells : MagicSpells.values()) {
			if(spells.getSpell().spellId() == spellId)
				return spells;
		}
		return null;
	}
	
	@SuppressWarnings("incomplete-switch")
	public static boolean handleMagicSpells(Player p, int buttonId) {
		MagicSpells spell = forSpellId(buttonId);
		if(!spell.getSpell().canCast(p, false))
			return true;
		switch(spell) {
		case BONES_TO_PEACHES:
		case BONES_TO_BANANAS:
			Item item = new Item(526);
			String sa = !item.getDefinition().getName().endsWith("s") ? "s" : "";
			if(!p.getInventory().contains(item.getId())) {
				p.getPacketSender().sendMessage("You do not have any "+item.getDefinition().getName()+""+sa+" in your inventory.");
				return true;
			}
			p.getInventory().delete(557, spell == BONES_TO_PEACHES ? 4 : 2).delete(555, spell == BONES_TO_PEACHES ? 4 : 2).delete(561, spell == BONES_TO_PEACHES ? 2 : 1);
			int i = 0;
			for(Item invItem : p.getInventory().getValidItems()) {
				if(invItem.getId() == item.getId()) {
					p.getInventory().delete(item.getId(), 1).add(spell == BONES_TO_PEACHES ? 6883 : 1963, 1);
					i++;
				}
			}
			p.performGraphic(new Graphic(141, GraphicHeight.MIDDLE));
			p.performAnimation(new Animation(722));
			p.getSkillManager().addExperience(Skill.MAGIC, spell.getSpell().baseExperience() * i);
			break;
		case VENGEANCE:
			if(!p.getLocation().isAidingAllowed() || p.getLocation() == Location.DUEL_ARENA) {
				p.getPacketSender().sendMessage("This spell cannot be cast here.");
				return true;
			}
			if(p.hasVengeance()) {
				p.getPacketSender().sendMessage("You already have Vengeance's effect.");
				return true;
			}
			if(!p.getLastVengeance().elapsed(30000)) {
				p.getPacketSender().sendMessage("This spell can only be cast once every 30 seconds.");
				return true;
			}
			p.getInventory().deleteItemSet(spell.getSpell().itemsRequired(p));
			p.performAnimation(new Animation(4410));
			p.performGraphic(new Graphic(726, GraphicHeight.HIGH));
			p.getLastVengeance().reset();
			p.setHasVengeance(true);
			p.getPacketSender().sendMessage("<col=FF0000>Your vengeance is now active!");
			break;
		}
		return true;
	}
}
