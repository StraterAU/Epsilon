package com.epsilon.world.content;

import com.epsilon.model.Animation;
import com.epsilon.model.GameObject;
import com.epsilon.model.Graphic;
import com.epsilon.model.PlayerRights;
import com.epsilon.model.Locations.Location;
import com.epsilon.model.movement.MovementQueue;
import com.epsilon.util.Misc;
import com.epsilon.world.content.clan.ClanChatManager;
import com.epsilon.world.content.dialogue.DialogueManager;
import com.epsilon.world.entity.impl.npc.NPC;
import com.epsilon.world.entity.impl.player.Player;

public class Gambling {

	public static void rollDice(Player player) {
		if(!player.getHighestRights().isStaff() && !player.getHighestRights().isMember()) {
			player.getPacketSender().sendMessage("You need to be a donator to use this item.");
			return;
		}
		if(player.getLocation() != Location.VARROCK) {
			player.getPacketSender().sendMessage("").sendMessage("This dice can only be used in the gambling area!").sendMessage("To get there, talk to the gambler.");
			return;
		}
		if(player.getClanChatName() == null) {
			player.getPacketSender().sendMessage("You need to be in a clanchat channel to roll a dice.");
			return;
		} else if(player.getClanChatName().equalsIgnoreCase("help")) {
			player.getPacketSender().sendMessage("You can't roll a dice in this clan chat channel!");
			return;
		}
		if(!player.getClickDelay().elapsed(5000)) {
			player.getPacketSender().sendMessage("You must wait 5 seconds between each dice cast.");
			return;
		}
		player.getMovementQueue().reset();
		player.performAnimation(new Animation(11900));
		player.performGraphic(new Graphic(2075));
		ClanChatManager.sendMessage(player.getCurrentClanChat(), "@bla@[ClanChat] @whi@"+player.getUsername()+" just rolled @bla@" +Misc.getRandom(100)+ "@whi@ on the percentile dice.");
		player.getClickDelay().reset();
	}

	public static void plantSeed(Player player) {
		if(player.getLocation() != Location.VARROCK) {
			player.getPacketSender().sendMessage("").sendMessage("This seed can only be planted in the gambling area").sendMessage("To get there, talk to the gambler.");
			return;
		}
		if(!player.getClickDelay().elapsed(500))
			return;
		for(NPC npc : player.getLocalNpcs()) {
			if(npc != null && npc.getPosition().equals(player.getPosition())) {
				player.getPacketSender().sendMessage("You cannot plant a seed right here.");
				return;
			}
		}

		if(CustomObjects.objectExists(player.getPosition().copy())) {
			player.getPacketSender().sendMessage("You cannot plant a seed right here.");
			return;
		}
		
		
		
		FlowersData flowers = FlowersData.generate();
		final GameObject flower = new GameObject(flowers.objectId, player.getPosition().copy());
		player.getMovementQueue().reset();
		player.getInventory().delete(299, 1);
		player.performAnimation(new Animation(827));
		player.getPacketSender().sendMessage("You plant the seed..");
		player.setPlants(player.getPlants() + 1);
		player.getMovementQueue().reset();
		player.setDialogueActionId(42);
		player.setInteractingObject(flower);
		if (player.getPlants() != 5) {
			player.setPlayerLocked(true);
			}
		DialogueManager.start(player, 78);
		MovementQueue.stepAway(player);
		CustomObjects.globalObjectRemovalTask(flower, 90);
		player.setPositionToFace(flower.getPosition());
		player.getClickDelay().reset();
		if (player.getPlants() == 5) {
			player.setPlayerLocked(false);
				player.setPlants(0);
			}
	}

	public enum FlowersData {
		PASTEL_FLOWERS(2980, 2460),
		RED_FLOWERS(2981, 2462),
		BLUE_FLOWERS(2982, 2464),
		YELLOW_FLOWERS(2983, 2466),
		PURPLE_FLOWERS(2984, 2468),
		ORANGE_FLOWERS(2985, 2470),
		RAINBOW_FLOWERS(2986, 2472),

		WHITE_FLOWERS(2987, 2474),
		BLACK_FLOWERS(2988, 2476);
		FlowersData(int objectId, int itemId) {
			this.objectId = objectId;
			this.itemId = itemId;
		}

		public int objectId;
		public int itemId;
		
		public static FlowersData forObject(int object) {
			for(FlowersData data : FlowersData.values()) {
				if(data.objectId == object)
					return data;
			}
			return null;
		}
		
		public static FlowersData generate() {
			double RANDOM = (java.lang.Math.random() * 100);
			if(RANDOM >= 1) {
				return values()[Misc.getRandom(6)];
			} else {
				return Misc.getRandom(3) == 1 ? WHITE_FLOWERS : BLACK_FLOWERS;
			}
		}
	}
}
