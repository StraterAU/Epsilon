package com.epsilon.world.content;


import com.epsilon.model.GameObject;
import com.epsilon.model.Position;
import com.epsilon.util.Misc;
import com.epsilon.util.Stopwatch;
import com.epsilon.world.World;
import com.epsilon.world.entity.impl.player.Player;

public class ChristmasEvent {

	
	
	private static Stopwatch timer = new Stopwatch().reset();
	public static CrashedEvent CRASHED_EVENT = null;
	private static LocationData LAST_LOCATION = null;
	
	public static class CrashedEvent {
		
		public CrashedEvent(GameObject EventObject, LocationData EventLocation) {
			this.EventObject = EventObject;
			this.EventLocation = EventLocation;
		}
		
		public GameObject EventObject;
		private LocationData EventLocation;
		
		public GameObject getEventObject() {
			return EventObject;
		}
		
		public LocationData getEventLocation() {
			return EventLocation;
		}
	}

	public static enum LocationData {
		
		LOCATION_1(new Position(3305, 3491), "at the Lumber Mill", "Varrock"),
		LOCATION_2(new Position(3167, 3301), "at the Lumbridge Flour Mill", "Lumbridge"),
		LOCATION_3(new Position(3109, 3350), "at the Draynor Mansion", "Draynor Mansion"),
		LOCATION_4(new Position(3079, 3250), "at the Draynor Market", "Draynor Market"),
		LOCATION_5(new Position(3027, 3217), "at the Port Sarim Docks", "Port Sarim"),
		LOCATION_6(new Position(2956, 3216), "at Rimmington Well", "Rimmington"),
		LOCATION_7(new Position(3275, 3164), "outside Al-Kharid Bank", "Al-Kharid"),
		LOCATION_8(new Position(3303, 3123), "at Shantay's Pass", "Shantay's Pass"),
		LOCATION_9(new Position(3008, 3478), "at Ice Mountain", "Ice Mountain"),
		LOCATION_10(new Position(2956, 3507), "at the Goblin Village", "Goblin Village"),
		LOCATION_11(new Position(2900, 3547), "at Burthorpe Castle", "Burthorpe"),
		LOCATION_12(new Position(2657, 3439), "at the Ranging Guild", "Ranging Guild"),
		LOCATION_13(new Position(2609, 3392), "at the Fishing Guild", "Fishing Guild"),
		LOCATION_14(new Position(2605, 3225), "at the Ardougne Monastary", "Ardougne Monastary"),
		LOCATION_15(new Position(2661, 3157), "at Port Khazard", "Port Khazard"),
		LOCATION_16(new Position(2459, 3090), "at Castle Wars", "Castle Wars"),
		LOCATION_17(new Position(3551, 3529), "at Fenkwnstrain's Castle", "Fenkenstrain's Castle");

		private LocationData(Position spawnPos, String clue, String playerPanelFrame) {
			this.spawnPos = spawnPos;
			this.clue = clue;
			this.playerPanelFrame = playerPanelFrame;
		}

		private Position spawnPos;
		public String clue;
		public String playerPanelFrame;
	}

	public static LocationData getRandom() {
		LocationData event = LocationData.values()[Misc.getRandom(LocationData.values().length - 1)];
		return event;
	}

	public static void sequence() {
		if(CRASHED_EVENT == null) {
				LocationData locationData = getRandom();
				if(LAST_LOCATION != null) {
					if(locationData == LAST_LOCATION) {
						locationData = getRandom();
					}
				}
				LAST_LOCATION = locationData;
				CRASHED_EVENT = new CrashedEvent(new GameObject(47748, locationData.spawnPos), locationData);
				CustomObjects.spawnGlobalObject(CRASHED_EVENT.EventObject);
				timer.reset();
			
		}
	}

	public static void despawn(boolean respawn) {
		if(respawn) {
			timer.reset(0);
		} else {
			timer.reset();
		}
		if(CRASHED_EVENT != null) {
			for(Player p : World.getPlayers()) {
				if(p == null) {
					continue;
			
				}
			CRASHED_EVENT= null;
		}
	}
}
}