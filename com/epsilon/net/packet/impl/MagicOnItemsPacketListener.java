package com.epsilon.net.packet.impl;

import com.epsilon.GameSettings;
import com.epsilon.model.Animation;
import com.epsilon.model.Graphic;
import com.epsilon.model.GraphicHeight;
import com.epsilon.model.Item;
import com.epsilon.model.Skill;
import com.epsilon.net.packet.Packet;
import com.epsilon.net.packet.PacketListener;
import com.epsilon.world.content.PlankMake;
import com.epsilon.world.content.combat.magic.MagicSpells;
import com.epsilon.world.content.combat.magic.Spell;
import com.epsilon.world.content.skill.impl.smithing.Smelting;
import com.epsilon.world.entity.impl.player.Player;

/**
 * Handles magic on items. 
 * @author Gabriel Hannason
 */
public class MagicOnItemsPacketListener implements PacketListener {

	@SuppressWarnings("unused")
	@Override
	public void handleMessage(Player player, Packet packet) {
		if(packet.getOpcode() == MAGIC_ON_GROUNDITEMS) {
			final int itemY = packet.readLEShort();
			final int itemId = packet.readShort();
			final int itemX = packet.readLEShort();
			final int spellId = packet.readUnsignedShortA();
			final MagicSpells spell = MagicSpells.forSpellId(spellId);
			if(spell == null)
				return;
			player.getMovementQueue().reset();
			//switch(spell) {}
		} else if(packet.getOpcode() == MAGIC_ON_ITEMS) {
			int slot = packet.readShort();
			int itemId = packet.readShortA();
			int childId = packet.readShort();
			int spellId = packet.readShortA();
			if(!player.getClickDelay().elapsed(1300))
				return;
			if(slot < 0 || slot > player.getInventory().capacity())
				return;
			if(player.getInventory().getItems()[slot].getId() != itemId)
				return;
			Item item = new Item(itemId);
			switch(spellId) {
			case 1155: //Lvl-1 enchant sapphire
			case 1165: //Lvl-2 enchant emerald
			case 1176: //Lvl-3 enchant ruby
			case 1180: //Lvl-4 enchant diamond
			case 1187: //Lvl-5 enchant dragonstone
			case 6003: //Lvl-6 enchant onyx
				enchantBolt(player, spellId, item.getId(), 28);
				break;
			}
			MagicSpells magicSpell = MagicSpells.forSpellId(spellId);
			if(magicSpell == null)
				return;
			Spell spell = magicSpell.getSpell();
			switch(magicSpell) {
			case PLANK_MAKE:
				if(spell == null || !spell.canCast(player, true)) {
					return;
				}
				PlankMake.handle(player, itemId, slot);
				break;
			case LOW_ALCHEMY:
			case HIGH_ALCHEMY:
				if(!item.tradeable() || !item.sellable() || item.getId() == 995) {
					player.getPacketSender().sendMessage("This spell can not be cast on this item.");
					return;
				}
				if(spell == null || !spell.canCast(player, true))
					return;
				player.getInventory().delete(itemId, 1).add(995,  200 + (int) (item.getDefinition().getValue() * (magicSpell == MagicSpells.HIGH_ALCHEMY ? 1 : 0.8)));
				player.performAnimation(new Animation(712));
				player.performGraphic(new Graphic(magicSpell == MagicSpells.HIGH_ALCHEMY ? 113 : 112, GraphicHeight.LOW));
				player.getSkillManager().addExperience(Skill.MAGIC, spell.baseExperience());
				player.getPacketSender().sendTab(GameSettings.MAGIC_TAB);
				break;
			case SUPERHEAT_ITEM:
				for(int i = 0; i < ORE_DATA.length; i++) {
					if(item.getId() == ORE_DATA[i][0]) {
						if(player.getInventory().getAmount(ORE_DATA[i][2]) < ORE_DATA[i][3]) {
							player.getPacketSender().sendMessage("You do not have enough "+new Item(ORE_DATA[i][2]).getDefinition().getName()+"s for this spell.");
							return;
						}
						if(spell == null || !spell.canCast(player, true))
							return;
						player.getInventory().delete(item.getId(), 1);
						for(int k = 0; k < ORE_DATA[i][3]; k++)
							player.getInventory().delete(ORE_DATA[i][2], 1);
						player.performAnimation(new Animation(725));
						player.performGraphic(new Graphic(148, GraphicHeight.HIGH));
						player.getInventory().add(ORE_DATA[i][4], 1);
						player.getPacketSender().sendTab(GameSettings.MAGIC_TAB);
						player.getSkillManager().addExperience(Skill.MAGIC, spell.baseExperience());
						player.getSkillManager().addExperience(Skill.SMITHING, Smelting.getExperience(ORE_DATA[i][4]));
						return;
					}		
				}
				player.getPacketSender().sendMessage("This spell can only be cast on Mining ores.");
				break;
			case BAKE_PIE:
				if (itemId == 2317 || itemId == 2319 || itemId == 2321) {
					player.getSkillManager().addExperience(Skill.MAGIC, spell.baseExperience());
					player.performAnimation(new Animation(4413));
					player.performGraphic(new Graphic(746, GraphicHeight.HIGH));
					player.getInventory().delete(item.getId(), 1);
					player.getPacketSender().sendMessage("You bake the pie");
					player.getInventory().add(itemId == 2317 ? 2323 : itemId == 2319 ? 2327 : itemId == 2321 ? 2325 : -1, 1);
				} else
					player.getPacketSender().sendMessage("This spell can only be cast on an uncooked pie.");
				break;
			default:
				break;
			}
			player.getClickDelay().reset();
			player.getInventory().refreshItems();
		}
	}

	final static int[][] ORE_DATA = {
		{436, 1, 438, 1, 2349, 53}, // TIN
		{438, 1, 436, 1, 2349, 53}, // COPPER
		{440, 1, -1, -1, 2351, 53}, // IRON ORE
		{442, 1, -1, -1, 2355, 53}, // SILVER ORE
		{444, 1, -1, -1, 2357, 23}, // GOLD BAR
		{447, 1, 453, 4, 2359, 30}, // MITHRIL ORE
		{449, 1, 453, 6, 2361, 38}, // ADDY ORE
		{451, 1, 453, 8, 2363, 50}, // RUNE ORE
	};

	public static final int MAGIC_ON_GROUNDITEMS = 181;
	public static final int MAGIC_ON_ITEMS = 237;
	
	public static int[][] boltData = { { 1155, 879, 9, 9236 }, { 1155, 9337, 17, 9240 }, { 1165, 9335, 19, 9237 },
			{ 1165, 880, 29, 9238 }, { 1165, 9338, 37, 9241 }, { 1176, 9336, 39, 9239 }, { 1176, 9339, 59, 9242 },
			{ 1180, 9340, 67, 9243 }, { 1187, 9341, 78, 9244 }, { 6003, 9342, 97, 9245 } };

	public static int[][] runeData = { { 1155, 555, 1, -1, -1 }, { 1165, 556, 3, -1, -1 }, { 1176, 554, 5, -1, -1 },
			{ 1180, 557, 10, -1, -1 }, { 1187, 555, 15, 557, 15 }, { 6003, 554, 20, 557, 20 } };

	public static void enchantBolt(Player player, int spell, int bolt, int amount) {
		for (int i = 0; i < boltData.length; i++) {
			if (spell == boltData[i][0]) {
				if (bolt == boltData[i][1]) {
					for (int a = 0; a < runeData.length; a++) {
						if (spell == runeData[a][0]) {
							if (!player.getInventory().contains(564)
									|| player.getInventory().getAmount(runeData[a][1]) < runeData[a][2]
									|| (player.getInventory().getAmount(runeData[a][3]) < runeData[a][4]
											&& runeData[a][3] > 0)) {
								player.sendMessage("You do not have the required runes to cast this spell!");
								return;
							}
							player.getInventory().delete(new Item(564, 1), player.getInventory().getSlot(564));
							player.getInventory().delete(new Item(runeData[a][1], runeData[a][2]), player.getInventory().getSlot(runeData[a][1]));
							if (runeData[a][3] > 0)
								player.getInventory().delete(new Item(runeData[a][3], runeData[a][4]), player.getInventory().getSlot(runeData[a][3]));
						}
					}
					if(player.getInventory().getAmount(boltData[i][1]) <= amount)
						amount = player.getInventory().getAmount(bolt);
					player.getInventory().delete(new Item(boltData[i][1], amount), player.getInventory().getSlot(boltData[i][1]));
					player.getSkillManager().addExperience(Skill.MAGIC, boltData[i][2] * amount);
					player.getInventory().add(boltData[i][3], amount);
					return;
				}
			}
		}
	}
	
}
