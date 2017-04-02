package com.epsilon.world.content;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import com.epsilon.world.entity.impl.player.Player;

public class GiantPouch {
	
	public static final int CAPACITY = 32;
	
	public static final int RUNE_ESSENCE = 1436;
	
	public static final int PURE_ESSENCE = 7936;

	private Queue<Integer> essence = new LinkedList<>();
	
	private Player player;
	
	public GiantPouch(Player player) {
		this.player = player;
	}
	
	public void fill() {
		
		if(essence.size() == CAPACITY) {
			player.getPacketSender().sendMessage("Your giant pouch is full.");
			return;
		}
		
		int available = CAPACITY - essence.size();
		int holding = 0;
		int count = 0;
		
		holding += player.getInventory().getAmount(RUNE_ESSENCE);
		holding += player.getInventory().getAmount(PURE_ESSENCE);
		
		if(holding == 0) {
			player.getPacketSender().sendMessage("You are not holding any rune essence.");
			return;
		}
		
		if(available > holding) {
			available = holding;
		}
		
		int amount = player.getInventory().getAmount(PURE_ESSENCE);
		
		if(amount >= available) {
			amount = available;
		}
		
		for(int i = 0; i < amount; i++) {
			essence.add(PURE_ESSENCE);
		}
		
		player.getInventory().delete(PURE_ESSENCE, amount);
		
		available -= amount;
		count += amount;
		
		amount = player.getInventory().getAmount(RUNE_ESSENCE);
		
		if(amount >= available) {
			amount = available;
		}
		
		for(int i = 0; i < amount; i++) {
			essence.add(RUNE_ESSENCE);
		}
		
		player.getInventory().delete(RUNE_ESSENCE, amount);
		
		available -= amount;
		count += amount;
		
		player.getPacketSender().sendMessage(count+" (pure) rune essence have been added to your pouch.");
		
	}
	
	public void empty() {
		
		int free = player.getInventory().getFreeSlots();
		
		if(essence == null) {
			return;
		}
		
		for(int i = 0; i < free; i++) {
			if(essence.peek() != null) {
				player.getInventory().add(essence.poll(), 1);
			}
		}
		
		player.getPacketSender().sendMessage(free+" (pure) rune essence have been remove from your giant pouch.");
		
	}
	
	public int[] toArray() {
		
		int[] array = new int[essence.size()];
		int i = 0;
		
		for(Iterator<Integer> iterator = essence.iterator(); iterator.hasNext();) {
			array[i++] = iterator.next();
		}
		
		return array;
		
	}
	
	public void fill(int[] ids) {
		
		essence.clear();
		
		for(int i = 0; i < ids.length; i++) {
			essence.add(ids[i]);
		}
		
	}

	public Queue<Integer> getEssence() {
		return essence;
	}

	public void setEssence(Queue<Integer> essence) {
		this.essence = essence;
	}
	
}
