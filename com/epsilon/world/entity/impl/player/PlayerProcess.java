package com.epsilon.world.entity.impl.player;

import com.epsilon.model.Item;
import com.epsilon.model.RegionInstance.RegionInstanceType;
import com.epsilon.model.container.impl.Equipment;
import com.epsilon.util.Misc;
import com.epsilon.world.content.LoyaltyProgramme;
import com.epsilon.world.content.combat.pvp.BountyHunter;
import com.epsilon.world.content.skill.impl.construction.House;
import com.epsilon.world.entity.impl.GroundItemManager;

public class PlayerProcess {

	/*
	 * The player (owner) of this instance
	 */
	private Player player;

	/*
	 * The loyalty tick, once this reaches 6, the player
	 * will be given loyalty points.
	 * 6 equals 3.6 seconds.
	 */
	private int loyaltyTick;

	/*
	 * The timer tick, once this reaches 2, the player's
	 * total play time will be updated.
	 * 2 equals 1.2 seconds.
	 */
	private int timerTick;
	
	/**
	 * 
	 */
	private int avaTick;

	/*
	 * Makes sure ground items are spawned on height change
	 */
	private int previousHeight;

	public PlayerProcess(Player player) {
		this.player = player;
		this.previousHeight = player.getPosition().getZ();
	}

	public void sequence() {
		/** COMBAT **/
		player.getCombatBuilder().process();
		
		/** SKILLS **/
		if(player.shouldProcessFarming()) {
			player.getFarming().sequence();
		}

		/** MISC **/

		if(previousHeight != player.getPosition().getZ()) {
			GroundItemManager.handleRegionChange(player);
			previousHeight = player.getPosition().getZ();
		}

		if(!player.isInActive()) {
			if(loyaltyTick >= 6) {
				LoyaltyProgramme.incrementPoints(player);
				loyaltyTick = 0;
			}
			loyaltyTick++;
		}/* else {
			player.setTotalPlayTime(player.getTotalPlayTime() + player.getRecordedLogin().elapsed());
		}*/
		
		if(timerTick >= 1) {
			player.getPacketSender().sendString(39165, "@or1@Time Played:  @whi@"+Misc.getTimePlayed((player.getTotalPlayTime() + player.getRecordedLogin().elapsed())));
			timerTick = 0;
		}
		timerTick++;
		
		if(++avaTick >= 300) {
			int capeId = player.getEquipment().get(Equipment.CAPE_SLOT).getId();
			final boolean avas = capeId == 10499 || capeId == 14019 || capeId == 14022 || capeId == 14024;
			if(avas) {
				Item item = player.getEquipment().get(Equipment.AMMUNITION_SLOT);
				if(item == null || item.getId() == -1) {
					
				} else if(item.getId() == 808 || item.getId() == 865 || item.getId() == 886) {
					item.incrementAmount();
					player.getEquipment().refreshItems();
				}
			}
			avaTick = 0;
		}
		
		BountyHunter.sequence(player);
		
		if(player.getRegionInstance() != null && (player.getRegionInstance().getType() == RegionInstanceType.CONSTRUCTION_HOUSE || player.getRegionInstance().getType() == RegionInstanceType.CONSTRUCTION_DUNGEON)) {
			((House)player.getRegionInstance()).process();
		}
		
		if(player.getSandwichLady() != null) {
			player.getSandwichLady().process();
		}
		
		/*Item item = player.getEquipment().get(Equipment.WEAPON_SLOT);
		
		if(item != null && item.getId() == 10501) {
			player.getPacketSender().sendInteractionOption("Pelt", 2, true);
		} else {
			if(player.getLocation() != null) {
				player.getLocation().enter(player);
			} else {
				player.getPacketSender().sendInteractionOption("null", 2, true);
			}
		}*/
		
	}
}
