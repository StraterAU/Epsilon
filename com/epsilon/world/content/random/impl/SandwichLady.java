package com.epsilon.world.content.random.impl;

import com.epsilon.GameSettings;
import com.epsilon.model.Animation;
import com.epsilon.model.GameMode;
import com.epsilon.model.Graphic;
import com.epsilon.model.GroundItem;
import com.epsilon.model.Item;
import com.epsilon.model.Position;
import com.epsilon.util.Misc;
import com.epsilon.world.World;
import com.epsilon.world.content.dialogue.Dialogue;
import com.epsilon.world.content.dialogue.DialogueExpression;
import com.epsilon.world.content.dialogue.DialogueManager;
import com.epsilon.world.content.dialogue.DialogueType;
import com.epsilon.world.content.random.RandomEvent;
import com.epsilon.world.entity.impl.GroundItemManager;
import com.epsilon.world.entity.impl.npc.NPC;
import com.epsilon.world.entity.impl.player.Player;

public class SandwichLady implements RandomEvent {
	
	public static final int NPC_ID = 8631;
	
	private int ticks = 0;
	
	private Player player;
	
	private NPC npc;
	
	private Sandwiches sandwich;
	
	private boolean attacked;
	
	public SandwichLady(Player player) {
		this.player = player;
	}

	@Override
	public void spawn() {
		
		Position position = player.getPosition().copy();
		
		if(!player.getMovementQueue().canWalk(-1, 0)) {
			if(!player.getMovementQueue().canWalk(0, -1)) {
				if(!player.getMovementQueue().canWalk(1, 0)) {
					if(!player.getMovementQueue().canWalk(0, 1)) {
						return;
					} else {
						position.add(0, 1);
					}
				} else {
					position.add(1, 0);
				}
			} else {
				position.add(0, -1);
			}
		} else {
			position.add(-1, 0);
		}
		
		sandwich = Misc.randomElement(Sandwiches.values());
		
		npc = new NPC(NPC_ID, position);
		npc.setEntityInteraction(player);
		npc.setPositionToFace(player.getPosition());
		npc.performGraphic(new Graphic(86));
		npc.forceChat("Sandwiches, "+player.getUsername()+"!");
		
		World.register(npc);
		
	}
	
	//Have a triangle sandwich for free!
	//Hey, I didn't say you could have that!!!
	//-> hits with bread, player does death anim and screen fades away, teleports to random location
	//16137 - 16143
	//Hope that fills you up!
	
	@Override
	public void process() {
		
		if(player == null || npc == null || sandwich == null) {
			return;
		}
		
		if(!attacked) {
			switch(ticks++) {
			case 50:
				npc.forceChat("All types of sandwiches, "+player.getUsername()+".");
				break;
			case 100:
				npc.forceChat("Did you hear me, "+player.getUsername()+"?");
				break;
			case 150:
				npc.forceChat("Come on "+player.getUsername()+", I made these specially!!");
				break;
			case 200:
				attackPlayer("Take that, "+player.getUsername()+"!");
				break;
			}
		} else {
			switch(ticks++) {
			case 1:
				player.performAnimation(new Animation(0x900));
				break;
			case 2:
				logout();
				break;
			}
		}
		
	}
	
	public void handleButton(int component) {
		
		if(player == null || npc == null || sandwich == null) {
			return;
		}
		
		component -= 16137;
		
		if(component >= 0 && component < Sandwiches.values().length) {
			
			Sandwiches selection = Sandwiches.values()[component];
			
			if(selection == sandwich) {
				player.getPacketSender().sendInterfaceRemoval();
				DialogueManager.start(player, CORRECT_DIALOGUE);
				reward();
			} else {
				attackPlayer("Hey, I didn't say you could have that!!!");
			}
			
		}
		
	}
	
	private void reward() {
		if(player.getInventory().getFreeSlots() > 0) {
			player.getInventory().add(sandwich.getItemId(), 1);
		} else {
			GroundItemManager.spawnGroundItem(player, new GroundItem(new Item(sandwich.getItemId()), player.getPosition().copy(), player.getUsername(), player.getHostAddress(), false, 80, false, 80));
		}
	}
	
	public void startDialogue() {
		
		if(player == null || npc == null || sandwich == null) {
			return;
		}
		DialogueManager.start(player, START_DIALOGUE);
		
	}
	
	public void openTray() {
		player.getPacketSender().sendInterface(16135);
		player.getPacketSender().sendString(16145, "Have a "+sandwich.toString()+" for free!");
	}
	
	public void end() {
		sandwich = null;
		player.getPacketSender().sendInterfaceRemoval();
		World.deregister(npc);
		npc = null;
		player.setSandwichLady(null);
		player = null;
	}
	
	public void attackPlayer(String message) {
		player.getPacketSender().sendInterfaceRemoval();
		npc.forceChat(message);
		npc.performAnimation(new Animation(386));
		attacked = true;
		ticks = 0;
	}
	
	public void logout() {
		player.moveTo(Misc.randomElement(Locations.values()).getPosition().copy());
		end();
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public Sandwiches getSandwich() {
		return sandwich;
	}

	public void setSandwich(Sandwiches sandwich) {
		this.sandwich = sandwich;
	}

	public Dialogue START_DIALOGUE = new Dialogue() {

		@Override
		public DialogueType type() {
			return DialogueType.NPC_STATEMENT;
		}

		@Override
		public DialogueExpression animation() {
			return DialogueExpression.NORMAL;
		}

		@Override
		public int npcId() {
			return NPC_ID;
		}
		
		@Override
		public String[] dialogue() {
			return new String[] { "You look hungry to me. I tell you what", "have a "+sandwich.toString()+" on me." };
		}
		
		@Override
		public Dialogue nextDialogue() {
			return new Dialogue() {

				@Override
				public DialogueType type() {
					return null;
				}

				@Override
				public DialogueExpression animation() {
					return null;
				}

				@Override
				public String[] dialogue() {
					return null;
				}
				
				@Override
				public void specialAction() {
					player.getPacketSender().sendInterfaceRemoval();
					openTray();
				}
				
			};
		}
		
	};
	
	public Dialogue CORRECT_DIALOGUE = new Dialogue() {

		@Override
		public DialogueType type() {
			return DialogueType.NPC_STATEMENT;
		}

		@Override
		public DialogueExpression animation() {
			return DialogueExpression.NORMAL;
		}

		@Override
		public int npcId() {
			return NPC_ID;
		}
		
		@Override
		public String[] dialogue() {
			return new String[] { "Hope that fills you up!" };
		}
		
		@Override
		public Dialogue nextDialogue() {
			return new Dialogue() {

				@Override
				public DialogueType type() {
					return null;
				}

				@Override
				public DialogueExpression animation() {
					return null;
				}

				@Override
				public String[] dialogue() {
					return null;
				}
				
				@Override
				public void specialAction() {
					end();
				}
				
			};
		}
		
	};
	
	public static enum Sandwiches {
		
		BAGUETTE(6961),
		
		TRIANGLE_SANDWICH(6962),
		
		SQUARE_SANDWICH(6965),
		
		ROLL(6963),
		
		MEAT_PIE(2327),
		
		KEBAB(1971),
		
		CHOCOLATE_BAR(1973);
		
		private final int itemId;
		
		Sandwiches(int itemId) {
			this.itemId = itemId;
		}

		public int component() {
			return ordinal() + 16137;
		}

		@Override
		public String toString() {
			return name().toLowerCase().replaceAll("_", " ");
		}

		public int getItemId() {
			return itemId;
		}
		
	}
	
	public static enum Locations {
		
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
		LOCATION_17(new Position(3551, 3529), "at Fenkwnstrain's Castle", "");
		
		private final Position position;
		
		private final String at;
		
		private final String city;
		
		Locations(Position position, String at, String city) {
			this.position = position;
			this.at = at;
			this.city = city;
		}

		public Position getPosition() {
			return position;
		}
		
	}
	
}