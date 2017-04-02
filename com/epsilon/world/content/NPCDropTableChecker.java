package com.epsilon.world.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.epsilon.model.Item;
import com.epsilon.model.definitions.NPCDrops;
import com.epsilon.model.definitions.NPCDrops.DropChance;
import com.epsilon.model.definitions.NPCDrops.NpcDropItem;
import com.epsilon.model.definitions.NpcDefinition;
import com.epsilon.world.entity.impl.player.Player;

/**
 * NPCDropTableChecker
 * Handles the NPC drop table checker interface actions.
 */
public class NPCDropTableChecker {

	/**
	 * The single instance of the NPCDropTableChecker class.
	 */
	private static final NPCDropTableChecker SINGLETON = new NPCDropTableChecker();

	/**
	 * A {@link List} of {@link Integer}s holding all the NPC identifiers in
	 * alphabetical order of their respected NPC name.
	 */
	private final List<Integer> dropTableNpcIds = new ArrayList<Integer>();

	/**
	 * A {@link List} of {@link String}s holding all the NPC names in
	 * alphabetical order.
	 */
	private final List<String> dropTableNpcNames = new ArrayList<String>();

	/**
	 * Creates a new NPCDropTableChecker instance.
	 */
	public NPCDropTableChecker() {
		initialize();
	}

	/**
	 * Initializes this class by grabbing all the required data from the
	 * {@link NPCDropManager} class.
	 */
	private void initialize() {
		
		for(int npcId : NPCDrops.getDrops().keySet()) {
			if(npcId < 0) {
				continue;
			}
			boolean addToList = true;
			String name = getNPCName(npcId);
			for (int i : dropTableNpcIds) {
				if (getNPCName(i).equalsIgnoreCase(name) || name.equals("null")) {
					addToList = false;
					break;
				}
			}
			if (addToList) {
				dropTableNpcIds.add(npcId);
			}
		}
		Collections.sort(dropTableNpcIds, (Integer s1, Integer s2) -> getNPCName(s1).compareTo(getNPCName(s2)));
		for (int npcId : dropTableNpcIds) {
			dropTableNpcNames.add(getNPCName(npcId));
		}
	}

	/**
	 * Refreshed the drop table names on the specified {@link Player}s drop
	 * table checker interface.
	 * 
	 * @param player
	 *            The player performing this action.
	 */
	public void refreshDropTableChilds(final Player player) {
		int childId = 37651;
		for (String npcName : dropTableNpcNames) {
			player.getPacketSender().sendFrame126(npcName, childId++);
		}
		for (int child = childId; child < 37821; child++) {
			player.getPacketSender().sendFrame126("", child);
		}
	}

	/**
	 * Handles the action buttons clicked in the npc drop table checker.
	 * 
	 * @param player
	 *            The player performing this actions.
	 * @param actionId
	 *            The actionId of the button that has been clicked.
	 * @return If the actionId is one of the action buttons in the npc drop
	 *         table checker interface. If true, it will stop further actions in
	 *         the action button method.
	 */
	public boolean handleButtonClick(final Player player, final int actionId) {
		if (actionId >= -27885 && actionId <= -27716) {
			/*if (!player.isDonator() && player.playerRights != PlayerRights.OWNER) {
				player.getActionAssistant().sendMessage("You need to be a donator to use this feature.");
				return true;
			}*/
			if (System.currentTimeMillis() - player.npcDropTableDelay < 1200) {
				int secondLeft = (int) (1200 - (System.currentTimeMillis() - player.npcDropTableDelay)) / 1000;
				player.getPacketSender().sendMessage("You need to wait "+secondLeft+" second(s) before checking another drop table.");
				return true;
			}
			int index = actionId + 27885;
			//System.out.println("index: " + index);
			if (index >= dropTableNpcIds.size() || dropTableNpcIds.get(index) == null) {
				return true;
			}
			
			//System.out.println(index);
			
			showNPCDropTable(player, dropTableNpcIds.get(index));
			player.getPacketSender().sendFrame126("@or1@You are now viewing the @whi@"
					+ dropTableNpcNames.get(index) + "@or1@ drop table.", 37605);
			player.npcDropTableDelay = System.currentTimeMillis();
			return true;
		}
		return false;
	}
	
	public static int arrayMax(int[] arr) {
		int max = Integer.MIN_VALUE;

	    for(int cur: arr)
	        max = Math.max(max, cur);

	    return max;
	}

	/**
	 * Opens the drop table checker interface and shows the contents of the
	 * specified npc.
	 * 
	 * @param player
	 *            The player performing this action.
	 * @param npcId
	 *            The npcId of which the drop table is viewed.
	 */
	public void showNPCDropTable(final Player player, final int npcId) {
		
		player.getPacketSender().resetItemsOnInterface(37915, 60);
		player.getPacketSender().resetItemsOnInterface(37916, 60);
		player.getPacketSender().resetItemsOnInterface(37917, 60);
		player.getPacketSender().resetItemsOnInterface(37918, 60);
		player.getPacketSender().resetItemsOnInterface(37919, 60);
		player.getPacketSender().resetItemsOnInterface(37920, 60);
		player.getPacketSender().resetItemsOnInterface(37921, 60);
		
		Map<DropChance, List<Item>> dropTables = new HashMap<>();
		NPCDrops drops = NPCDrops.forId(npcId);
		NpcDropItem[] items = drops.getDropList();
		
		for(NpcDropItem item : items) {
			
			DropChance chance = item.getChance();
			List<Item> table = dropTables.get(chance);
			
			if(chance == DropChance.ALMOST_ALWAYS || chance == DropChance.COMMON || chance == DropChance.VERY_COMMON) {
				chance = DropChance.COMMON;
			}
			
			if(chance == DropChance.NOTTHATRARE || chance == DropChance.RARE) {
				chance = DropChance.RARE;
			}
			
			if(chance == DropChance.LEGENDARY || chance == DropChance.LEGENDARY_2) {
				chance = DropChance.LEGENDARY;
			}
			
			if(chance == DropChance.LEGENDARY_3 || chance == DropChance.LEGENDARY_4 || chance == DropChance.LEGENDARY_5) {
				chance = DropChance.LEGENDARY_3;
			}
			
			if(table == null) {
				dropTables.put(chance, table = new ArrayList<Item>());
			}
			
			boolean found = false;
			
			for(Item drop : table) {
				if(drop != null && drop.getId() == item.getId()) {
					found = true;
					break;
				}
			}
			
			if(!found) {
				
				int amount = arrayMax(item.getCount());
				
				if(amount >= 1 && amount <= Integer.MAX_VALUE) {
					table.add(new Item(item.getId(), amount));
				}
				
			}
			
		}
		
		for(Entry<DropChance, List<Item>> entry : dropTables.entrySet()) {
			
			DropChance chance = entry.getKey();
			List<Item> dropTable = entry.getValue();
			
			if (dropTable.size() == 0) {
				continue;
			}
			
			if (chance == DropChance.ALWAYS) {
				player.getPacketSender().sendItemsOnInterface(37915, 60,
						dropTable, false);
			} else if (chance == DropChance.COMMON) {
				player.getPacketSender().sendItemsOnInterface(37916, 60,
						dropTable, false);
			} else if (chance == DropChance.UNCOMMON) {
				player.getPacketSender().sendItemsOnInterface(37917, 60,
						dropTable, false);
			} else if (chance == DropChance.RARE) {
				player.getPacketSender().sendItemsOnInterface(37918, 60,
						dropTable, false);
			} else if (chance == DropChance.LEGENDARY) {
				player.getPacketSender().sendItemsOnInterface(37919, 60,
						dropTable, false);
			} else if (chance == DropChance.LEGENDARY_3) {
				player.getPacketSender().sendItemsOnInterface(37920, 60,
						dropTable, false);
			} else if (chance == DropChance.LEGENDARY_5) {
				player.getPacketSender().sendItemsOnInterface(37921, 60,
						dropTable, false);
			}
			
		}
		player.getPacketSender().sendInterface(37600);
	}

	/**
	 * Grabs the NPC name from the NPCDefinitions.
	 * 
	 * @param npcId
	 *            The npcId that you want to get the name from.
	 * @return The NPC name of the specified npcId.
	 */
	private String getNPCName(int npcId) {
		NpcDefinition def = NpcDefinition.forId(npcId);
		if(def == null) {
			return "null";
		}
		return def.getName();
	}

	/**
	 * Gets the single instance of this class.
	 * 
	 * @return The single instance of this class.
	 */
	public static NPCDropTableChecker getSingleton() {
		return SINGLETON;
	}

}