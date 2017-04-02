package com.epsilon.world.content;

import com.epsilon.engine.task.Task;
import com.epsilon.engine.task.TaskManager;
import com.epsilon.model.Animation;
import com.epsilon.model.GameObject;
import com.epsilon.model.Item;
import com.epsilon.util.Misc;
import com.epsilon.world.entity.impl.player.Player;

public class CrystalChest {

	public static void handleChest(final Player p, final GameObject chest) {
		if(!p.getClickDelay().elapsed(3000)) 
			return;
		if(!p.getInventory().contains(989)) {
			p.getPacketSender().sendMessage("This chest can only be opened with a Crystal key.");
			return;
		}
		p.performAnimation(new Animation(827));
		p.getInventory().delete(989, 1);
		p.getPacketSender().sendMessage("You open the chest..");
		TaskManager.submit(new Task(1, p, false) {
			int tick = 0;
			@Override
			public void execute() {
				switch(tick) {
				case 2:
					Item[] loot = itemRewards[Misc.getRandom(itemRewards.length - 1)];
					for(Item item : loot) {
						p.getInventory().add(item);
					}
					p.getPacketSender().sendMessage("..and find some items!");
					CustomObjects.objectRespawnTask(p, new GameObject(173 , chest.getPosition().copy(), 10, 0), chest, 10);
					stop();
					break;
				}
				tick++;
			}
		});
		p.getClickDelay().reset();
	}

	private static final Item[][] itemRewards =  {
			{new Item(1969, 1), new Item(995, 200000)}, //set 1 SPINACH ROLL
			{new Item(1631, 1)}, //set 2 Dragonstone only set
			{new Item(995, 100000), new Item(373, 1)}, //set 3 Swordfish set
			{new Item(554, 50), new Item(555, 50), new Item(556, 50), new Item(557, 50), new Item(558, 50), new Item(559, 50), new Item(560, 10), new Item(561, 10), new Item(562, 10), new Item(563, 10), new Item(564, 10)}, //set 4 Full rune set
			{new Item(1631, 1), new Item(454, 100)}, //set 5 Coal
			{new Item(1615, 1), new Item(1601, 1), new Item(1603, 1)}, //set 6 Cut gems
			{new Item(1631, 1), new Item(985, 1), new Item(995, 7500)}, //set 7 Crystal Key 1
			{new Item(1631, 1), new Item(2363, 1)}, //set 8 Dragon Sq Half
			{new Item(1631, 1), new Item(987, 1), new Item(995, 7500)}, //set 9 Crystal Key 2
			{new Item(1631, 1), new Item(441, 150)}, //set 10 Iron Ore
			{new Item(1631, 1), new Item(1185, 1)}, //set 11 Rune armor 1
			{new Item(1631, 1), new Item(1079, 1)}, //set 12 Rune armor 2
			{new Item(1631, 1), new Item(1093, 1)}, //set 13 Rune armor 3
			{new Item(11710, 1)}, //set 14 Godsword shard 1
			{new Item(11712, 1)}, //set 15 Godsword shard 2
			{new Item(11714, 1)}, //set 16 Godsword shard 3
			{new Item(11732, 1)}, //set 17 Dragon Boots
			{new Item(3486, 1)}, //set 18 Gilded Armor 1
			{new Item(3481, 1)}, //set 19 Gilded Armor 2
			{new Item(3483, 1)}, //set 20 Gilded Armor 3
			{new Item(3485, 1)}, //set 21 Gilded Armor 4
			{new Item(3488, 1)}, //set 22 Gilded Armor 5
			{new Item(15332, 1)}, //set 23 Overload
			{new Item(6918, 1)}, //set 24 Infinity Armor 1
			{new Item(6916, 1)}, //set 25 Infinity Armor 2
			{new Item(6924, 1)}, //set 26 Infinity Armor 3
			{new Item(6922, 1)}, //set 27 Infinity Armor 4
			{new Item(6920, 1)}, //set 28 Infinity Armor 5
			{new Item(2665, 1)}, //set 29 Saradomin Armor 1
			{new Item(2661, 1)}, //set 30 Saradomin Armor 1
			{new Item(2663, 1)}, //set 31 Saradomin Armor 1
			{new Item(2667, 1)}, //set 32 Saradomin Armor 1
			{new Item(2673, 1)}, //set 33 Guthix Armor 1
			{new Item(2669, 1)}, //set 34 Guthix Armor 1
			{new Item(2671, 1)}, //set 35 Guthix Armor 1
			{new Item(2675, 1)}, //set 36 Guthix Armor 1
			{new Item(2657, 1)}, //set 37 Zamorak Armor 1
			{new Item(2653, 1)}, //set 38 Zamorak Armor 1
			{new Item(2655, 1)}, //set 39 Zamorak Armor 1
			{new Item(2659, 1)}, //set 40 Zamorak Armor 1
			{new Item(2579, 1)}, //set 41 Ranger Boots
			{new Item(2581, 1)}, //set 42 Robin Hood Hat
			{new Item(3751, 1), new Item(1631, 1)}, //set 43 Berserker Helm
		};
	
}
