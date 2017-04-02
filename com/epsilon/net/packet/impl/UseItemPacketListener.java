package com.epsilon.net.packet.impl;

import com.epsilon.engine.task.impl.WalkToTask;
import com.epsilon.engine.task.impl.WalkToTask.FinalizedMovementTask;
import com.epsilon.model.Animation;
import com.epsilon.model.GameMode;
import com.epsilon.model.GameObject;
import com.epsilon.model.Graphic;
import com.epsilon.model.Item;
import com.epsilon.model.PlayerRights;
import com.epsilon.model.Position;
import com.epsilon.model.Skill;
import com.epsilon.model.Locations.Location;
import com.epsilon.model.definitions.GameObjectDefinition;
import com.epsilon.model.definitions.ItemDefinition;
import com.epsilon.net.packet.Packet;
import com.epsilon.net.packet.PacketListener;
import com.epsilon.util.Misc;
import com.epsilon.world.World;
import com.epsilon.world.clip.region.RegionClipping;
import com.epsilon.world.content.ItemForging;
import com.epsilon.world.content.GemBag.GemBagMessage;
import com.epsilon.world.content.GemBagUpgrade.GemBagUpgradeMessage;
import com.epsilon.world.content.dialogue.DialogueManager;
import com.epsilon.world.content.minigames.impl.WarriorsGuild;
import com.epsilon.world.content.skill.impl.cooking.Cooking;
import com.epsilon.world.content.skill.impl.cooking.CookingData;
import com.epsilon.world.content.skill.impl.crafting.Gems;
import com.epsilon.world.content.skill.impl.crafting.LeatherMaking;
import com.epsilon.world.content.skill.impl.firemaking.Firemaking;
import com.epsilon.world.content.skill.impl.fletching.BoltTips;
import com.epsilon.world.content.skill.impl.fletching.Bolts;
import com.epsilon.world.content.skill.impl.fletching.Fletching;
import com.epsilon.world.content.skill.impl.herblore.Herblore;
import com.epsilon.world.content.skill.impl.herblore.PotionCombinating;
import com.epsilon.world.content.skill.impl.herblore.WeaponPoison;
import com.epsilon.world.content.skill.impl.prayer.BonesOnAltar;
import com.epsilon.world.content.skill.impl.prayer.Prayer;
import com.epsilon.world.content.skill.impl.slayer.SlayerDialogues;
import com.epsilon.world.content.skill.impl.slayer.SlayerTasks;
import com.epsilon.world.content.skill.impl.smithing.EquipmentMaking;
import com.epsilon.world.entity.impl.player.Player;

/**
 * This packet listener is called when a player 'uses' an item on another
 * entity.
 * 
 * @author relex lawl
 */

public class UseItemPacketListener implements PacketListener {

	/**
	 * The PacketListener logger to debug information and print out errors.
	 */
	// private final static Logger logger =
	// Logger.getLogger(UseItemPacketListener.class);

	private static void useItem(Player player, Packet packet) {
		if (player.isTeleporting() || player.getConstitution() <= 0)
			return;
		int interfaceId = packet.readLEShortA();
		int slot = packet.readShortA();
		int id = packet.readLEShort();
	}

	private static void itemOnItem(Player player, Packet packet) {
		int usedWithSlot = packet.readUnsignedShort();
		int itemUsedSlot = packet.readUnsignedShortA();
		if (usedWithSlot < 0 || itemUsedSlot < 0
				|| itemUsedSlot > player.getInventory().capacity()
				|| usedWithSlot > player.getInventory().capacity())
			return;
		Item usedWith = player.getInventory().getItems()[usedWithSlot];
		Item itemUsedWith = player.getInventory().getItems()[itemUsedSlot];
		if(usedWith.getId() == 18338 || itemUsedWith.getId() == 18338) {
			int other = usedWith.getId() == 18338 ? itemUsedWith.getId() : usedWith.getId();
			int other2 = usedWith.getId() == 18338 ? itemUsedSlot : usedWithSlot;
			GemBagMessage message = player.getGemBag().add(other, other2);
			if(message != null) {
				if(message.getMessage() != null) {
					player.sendMessage(message.getMessage());
				}
				return;
			}
		}
		if(usedWith.getId() == 14023 || itemUsedWith.getId() == 14023) {
			int other = usedWith.getId() == 14023 ? itemUsedWith.getId() : usedWith.getId();
			int other2 = usedWith.getId() == 14023 ? itemUsedSlot : usedWithSlot;
			GemBagUpgradeMessage message = player.getGemBagUpgrade().add(other, other2);
			if(message != null) {
				if(message.getMessage() != null) {
					player.sendMessage(message.getMessage());
				}
				return;
			}
		}
		if(usedWith.getId() == 6573 || itemUsedWith.getId() == 6573) {
			player.getPacketSender().sendMessage("To make an Amulet of Fury, you need to put an onyx in a furnace.");
			//return;
		}
		WeaponPoison.execute(player, itemUsedWith.getId(), usedWith.getId());
		if (itemUsedWith.getId() == 590 || usedWith.getId() == 590)
			Firemaking.lightFire(player, itemUsedWith.getId() == 590 ? usedWith.getId() : itemUsedWith.getId(), false, 1);
		if (itemUsedWith.getDefinition().getName().contains("(") && usedWith.getDefinition().getName().contains("("))
			PotionCombinating.combinePotion(player, usedWith.getId(), itemUsedWith.getId());
		if (usedWith.getId() == Herblore.VIAL || itemUsedWith.getId() == Herblore.VIAL){
			if (Herblore.makeUnfinishedPotion(player, usedWith.getId()) || Herblore.makeUnfinishedPotion(player, itemUsedWith.getId()))
				return;
		}
		if (Herblore.finishPotion(player, usedWith.getId(), itemUsedWith.getId()) || Herblore.finishPotion(player, itemUsedWith.getId(), usedWith.getId()))
			return;
		if (usedWith.getId() == 946 || itemUsedWith.getId() == 946)
			Fletching.openSelection(player, usedWith.getId() == 946 ? itemUsedWith.getId() : usedWith.getId());
		if (usedWith.getId() == 1777 || itemUsedWith.getId() == 1777)
			Fletching.openBowStringSelection(player, usedWith.getId() == 1777 ? itemUsedWith.getId() : usedWith.getId());
		if (usedWith.getId() == 53 || itemUsedWith.getId() == 53 || usedWith.getId() == 52 || itemUsedWith.getId() == 52)
			Fletching .makeArrows(player, usedWith.getId(), itemUsedWith.getId());
		if (itemUsedWith.getId() == 1755 || usedWith.getId() == 1755){
			if(Gems.selectionInterface(player, usedWith.getId() == 1755 ? itemUsedWith.getId() : usedWith.getId())) {
				return;
			}
			if(BoltTips.selectionInterface(player, usedWith.getId() == 1755 ? itemUsedWith.getId() : usedWith.getId())) {
				return;
			}
		}
		/**
		 * UsedWith é o item que é usado pelo outro
		 */
		if(itemUsedWith.getId() >= Bolts.jade_BoltTip && itemUsedWith.getId() <= Bolts.onyx_BoltTip ){
			player.getPacketSender().sendMessage("executa na Raiz?");
			if (usedWith.getId() == Bolts.red_Topaz_BoltTip || itemUsedWith.getId() == Bolts.red_Topaz_BoltTip ){
			Bolts.selectionInterface(player, itemUsedWith.getId());
			}
			if (usedWith.getId() == Bolts.jade_BoltTip || itemUsedWith.getId() == Bolts.jade_BoltTip ){
			Bolts.selectionInterface(player, itemUsedWith.getId());
			}
			if (usedWith.getId() == Bolts.diamond_BoltTip || itemUsedWith.getId() == Bolts.diamond_BoltTip ){
			Bolts.selectionInterface(player, itemUsedWith.getId());
			}
			if (usedWith.getId() == Bolts.emerald_BoltTip || itemUsedWith.getId() == Bolts.emerald_BoltTip ){
			Bolts.selectionInterface(player, itemUsedWith.getId());
			}
			if (usedWith.getId() == Bolts.dragonstone_BoltTip || itemUsedWith.getId() == Bolts.dragonstone_BoltTip ){
			Bolts.selectionInterface(player, itemUsedWith.getId());
			}
			if (usedWith.getId() == Bolts.ruby_BoltTip || itemUsedWith.getId() == Bolts.ruby_BoltTip ){
			Bolts.selectionInterface(player, itemUsedWith.getId());
			}
			if (usedWith.getId() == Bolts.sapphire_BoltTip || itemUsedWith.getId() == Bolts.sapphire_BoltTip ){
				Bolts.selectionInterface(player, itemUsedWith.getId());
				}
			if (usedWith.getId() == Bolts.onyx_BoltTip || itemUsedWith.getId() == Bolts.onyx_BoltTip ){
				Bolts.selectionInterface(player, itemUsedWith.getId());
				}
		}
		if (usedWith.getId() == 1733 || itemUsedWith.getId() == 1733)
			LeatherMaking.craftLeatherDialogue(player, usedWith.getId(), itemUsedWith.getId());
		Herblore.handleSpecialPotion(player, itemUsedWith.getId(), usedWith.getId());
		ItemForging.forgeItem(player, itemUsedWith.getId(), usedWith.getId());
		if (player.hasRights(PlayerRights.OWNER))
			player.getPacketSender().sendMessage(
					"ItemOnItem - [usedItem, usedWith] : [" + usedWith.getId()
					+ ", " + itemUsedWith + "]");
	}

	@SuppressWarnings("unused")
	private static void itemOnObject(Player player, Packet packet) {
		@SuppressWarnings("unused")
		int interfaceType = packet.readShort();
		final int objectId = packet.readShort();
		final int objectY = packet.readLEShortA();
		final int itemSlot = packet.readLEShort();
		final int objectX = packet.readLEShortA();
		final int itemId = packet.readShort();
		
		if (itemSlot < 0 || itemSlot > player.getInventory().capacity())
			return;
		final Item item = player.getInventory().getItems()[itemSlot];
		if (item == null)
			return;
		final GameObject gameObject = new GameObject(objectId, new Position(
				objectX, objectY, player.getPosition().getZ()));
		if(objectId > 0 && objectId != 6 && !RegionClipping.objectExists(gameObject)) {
			//	player.getPacketSender().sendMessage("An error occured. Error code: "+id).sendMessage("Please report the error to a staff member.");
			return;
		}
		player.setInteractingObject(gameObject);
		player.setWalkToTask(new WalkToTask(player, gameObject.getPosition().copy(),
				gameObject.getSize(), new FinalizedMovementTask() {
			@Override
			public void execute() {
				if (CookingData.forFish(item.getId()) != null && CookingData.isRange(objectId)) {
					player.setPositionToFace(gameObject.getPosition());
					Cooking.selectionInterface(player, CookingData.forFish(item.getId()));
					return;
				}
				if (Prayer.isBone(itemId) && objectId == 409) {
					BonesOnAltar.openInterface(
							player, itemId);
					return;
				}
				if (player.getFarming().plant(itemId, objectId,
						objectX, objectY))
					return;
				if (player.getFarming().useItemOnPlant(itemId,
						objectX, objectY))
					return;
				if (objectId == 15621) { // Warriors guild
					// animator
					if (!WarriorsGuild.itemOnAnimator(player,
							item, gameObject))
						player.getPacketSender()
						.sendMessage(
								"Nothing interesting happens..");
					return;
				}
				if(player.getGameMode() == GameMode.HARDCORE_IRONMAN) {
					if(GameObjectDefinition.forId(objectId) != null) {
						GameObjectDefinition def = GameObjectDefinition.forId(objectId);
						if(def.name != null && def.name.toLowerCase().contains("bank") && def.actions != null && def.actions[0] != null && def.actions[0].toLowerCase().contains("use")) {
							ItemDefinition def1 = ItemDefinition.forId(itemId);
							ItemDefinition def2;
							int newId = def1.isNoted() ? itemId-1 : itemId+1;
							def2 = ItemDefinition.forId(newId);
							if(def2 != null && def1.getName().equals(def2.getName())) {
								int amt = player.getInventory().getAmount(itemId);
								if(!def2.isNoted()) {
									if(amt > player.getInventory().getFreeSlots())
										amt = player.getInventory().getFreeSlots();
								}
								if(amt == 0) {
									player.getPacketSender().sendMessage("You do not have enough space in your inventory to do that.");
									return;
								}
								player.getInventory().delete(itemId, amt).add(newId, amt);
								
							} else {
								player.getPacketSender().sendMessage("You cannot do this with that item.");
							}
							return;
						}
					}
				}
				switch(objectId) {
				case 6189:
					if(player.getSkillManager().getCurrentLevel(Skill.CRAFTING) < 80) {
						player.getPacketSender().sendMessage("You need a Crafting level of at least 80 to make that item.");
						return;
					}
					if(player.getInventory().contains(6573)) {
						if(player.getInventory().contains(1597)) {
							if(player.getInventory().contains(1759)) {
								player.performAnimation(new Animation(896));
								player.getInventory().delete(new Item(1759)).delete(new Item(6573)).add(new Item(6585));
								player.getPacketSender().sendMessage("You put the items into the furnace to forge an Amulet of Fury.");
							} else {
								player.getPacketSender().sendMessage("You need some Ball of Wool to do this.");
							}
						} else {
							player.getPacketSender().sendMessage("You need a Necklace mould to do this.");
						}
					}
					break;
				case 7836:
				case 7808:
					if(itemId == 6055) {
						int amt = player.getInventory().getAmount(6055);
						if(amt > 0) {
							player.getInventory().delete(6055, amt);
							player.getPacketSender().sendMessage("You put the weed in the compost bin.");
							player.getSkillManager().addExperience(Skill.FARMING, 20*amt);
						}
					}
					break;
				case 4306:
					EquipmentMaking.handleAnvil(player);
					break;
				}
			}
		}));
	}

	private static void itemOnNpc(final Player player, Packet packet) {
		int id = packet.readShortA();
		int index = packet.readShortA();
		final int slot = packet.readLEShort();
	}

	@SuppressWarnings("unused")
	private static void itemOnPlayer(Player player, Packet packet) {
		int interfaceId = packet.readUnsignedShortA();
		int targetIndex = packet.readUnsignedShort();
		int itemId = packet.readUnsignedShort();
		int slot = packet.readLEShort();
		if (slot < 0 || slot > player.getInventory().capacity() || targetIndex > World.getPlayers().capacity())
			return;
		Player target = World.getPlayers().get(targetIndex);
		if(target == null)
			return;
		switch (itemId) {
		case 962:
			if(!player.getInventory().contains(962) || player.hasRights(PlayerRights.ADMINISTRATOR))
				return;
			player.setPositionToFace(target.getPosition());
			player.performGraphic(new Graphic(1006));
			player.performAnimation(new Animation(451));
			player.getPacketSender().sendMessage("You pull the Christmas cracker...");
			target.getPacketSender().sendMessage(""+player.getUsername()+" pulls a Christmas cracker on you..");
			player.getInventory().delete(962, 1);
			player.getPacketSender().sendMessage("The cracker explodes and you receive a Party hat!");
			player.getInventory().add(1038 + Misc.getRandom(10), 1);			
			target.getPacketSender().sendMessage(""+player.getUsername()+" has received a Party hat!");
			/*	if(Misc.getRandom(1) == 1) {
				target.getPacketSender().sendMessage("The cracker explodes and you receive a Party hat!");
				target.getInventory().add((1038 + Misc.getRandom(5)*2), 1);
				player.getPacketSender().sendMessage(""+target.getUsername()+" has received a Party hat!");
			} else {
				player.getPacketSender().sendMessage("The cracker explodes and you receive a Party hat!");
				player.getInventory().add((1038 + Misc.getRandom(5)*2), 1);			
				target.getPacketSender().sendMessage(""+player.getUsername()+" has received a Party hat!");
			}*/
			break;
		case 4155:
			if (player.getSlayer().getDuoPartner() != null) {
				player.getPacketSender().sendMessage(
						"You already have a duo partner.");
				return;
			}
			if (player.getSlayer().getSlayerTask() != SlayerTasks.NO_TASK) {
				player.getPacketSender().sendMessage(
						"You already have a Slayer task. You must reset it first.");
				return;
			}
			Player duoPartner = World.getPlayers().get(targetIndex);
			if (duoPartner != null) {
				if (duoPartner.getSlayer().getDuoPartner() != null) {
					player.getPacketSender().sendMessage(
							"This player already has a duo partner.");
					return;
				}
				if(duoPartner.getSlayer().getSlayerTask() != SlayerTasks.NO_TASK) {
					player.getPacketSender().sendMessage("This player already has a Slayer task.");
					return;
				}
				if(duoPartner.getSlayer().getSlayerMaster() != player.getSlayer().getSlayerMaster()) {
					player.getPacketSender().sendMessage("You do not have the same Slayer master as that player.");
					return;
				}
				if (duoPartner.busy() || duoPartner.getLocation() == Location.WILDERNESS) {
					player.getPacketSender().sendMessage(
							"This player is currently busy.");
					return;
				}
				DialogueManager.start(duoPartner,
						SlayerDialogues.inviteDuo(duoPartner, player));
				player.getPacketSender().sendMessage(
						"You have invited " + duoPartner.getUsername()
						+ " to join your Slayer duo team.");
			}
			break;
		}
	}

	@Override
	public void handleMessage(Player player, Packet packet) {
		if (player.getConstitution() <= 0)
			return;
		switch (packet.getOpcode()) {
		case ITEM_ON_ITEM:
			itemOnItem(player, packet);
			break;
		case USE_ITEM:
			useItem(player, packet);
			break;
		case ITEM_ON_OBJECT:
			itemOnObject(player, packet);
			break;
		case ITEM_ON_GROUND_ITEM:
			// TODO
			break;
		case ITEM_ON_NPC:
			itemOnNpc(player, packet);
			break;
		case ITEM_ON_PLAYER:
			itemOnPlayer(player, packet);
			break;
		}
	}

	public final static int USE_ITEM = 122;

	public final static int ITEM_ON_NPC = 57;

	public final static int ITEM_ON_ITEM = 53;

	public final static int ITEM_ON_OBJECT = 192;

	public final static int ITEM_ON_GROUND_ITEM = 25;

	public static final int ITEM_ON_PLAYER = 14;
}
