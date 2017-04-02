package com.epsilon.world.content;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import com.epsilon.model.Item;
import com.epsilon.world.entity.impl.player.Player;

public class GemBag {
	
	public static int UNCUT_SAPPHIRE = 1623;
	
	public static int UNCUT_EMERALD = 1621;
	
	public static int UNCUT_RUBY = 1619;
	
	public static int UNCUT_DIAMOND = 1617;
	
	public static int[] UNCUT_GEMS = { UNCUT_SAPPHIRE, UNCUT_EMERALD, UNCUT_RUBY, UNCUT_DIAMOND };
	
	public static int CAPACITY = 100;

	private Queue<Integer> gems = new LinkedList<>();
	
	private final Player player;
	
	public GemBag(Player player) {
		this.player = player;
	}
	
	public GemBagMessage add(int id, int slot) {
		
		boolean found = false;
		
		for(int gem : UNCUT_GEMS) {
			if(gem == id) {
				found = true;
				break;
			}
		}
		
		if(!found) {
			return GemBagMessage.NOT_A_GEM;
		}
		
		if(gems.size() == CAPACITY) {
			return GemBagMessage.FULL_BAG;
		}
		
		gems.add(id);
		
		player.sendMessage("You add the gem to your bag.");
		player.getInventory().delete(new Item(id), slot);
		
		return GemBagMessage.SUCCESS;
		
	}
	
	public void inspect() {
		
		int sapphires = amount(UNCUT_SAPPHIRE);
		int emeralds = amount(UNCUT_EMERALD);
		int rubies = amount(UNCUT_RUBY);
		int diamonds = amount(UNCUT_DIAMOND);
		
		String message = String.format("Your gem bag has %d sapphires, %d emeralds, %d rubies and %d diamonds.", sapphires, emeralds, rubies, diamonds);
		
		player.sendMessage(message);
		
	}
	
	public void empty() {
		
		int slots = player.getInventory().getFreeSlots();
		
		for(int i = 0; !gems.isEmpty() && i < slots; i++) {
			player.getInventory().add(gems.poll(), 1);
		}
		
	}
	
	private int amount(int id) {
		
		int count = 0;
		
		for(Iterator<Integer> iterator = gems.iterator(); iterator.hasNext();) {
			if(iterator.next() == id) {
				count++;
			}
		}
		
		return count;
		
	}
	
	public int[] toArray() {
		
		int[] array = new int[gems.size()];
		int i = 0;
		
		for(Iterator<Integer> iterator = gems.iterator(); iterator.hasNext();) {
			array[i++] = iterator.next();
		}
		
		return array;
		
	}
	
	public void fill(int[] ids) {
		
		gems.clear();
		
		for(int i = 0; i < ids.length; i++) {
			gems.add(ids[i]);
		}
		
	}
	
	public Queue<Integer> getGems() {
		return gems;
	}

	public void setGems(Queue<Integer> gems) {
		this.gems = gems;
	}
	
	public static enum GemBagMessage {
		
		NOT_A_GEM(null),
		
		SUCCESS(null),
		
		FULL_BAG("Your gem bag can only hold 100 uncut gems.");
		
		private final String message;
		
		GemBagMessage(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}
		
	}
	
}
