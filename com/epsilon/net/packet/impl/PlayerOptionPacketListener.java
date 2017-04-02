
package com.epsilon.net.packet.impl;

import com.epsilon.engine.task.Task;
import com.epsilon.engine.task.TaskManager;
import com.epsilon.model.Animation;
import com.epsilon.model.Flag;
import com.epsilon.model.Graphic;
import com.epsilon.model.Item;
import com.epsilon.model.Projectile;
import com.epsilon.model.Locations.Location;
import com.epsilon.model.container.impl.Equipment;
import com.epsilon.net.packet.Packet;
import com.epsilon.net.packet.PacketListener;
import com.epsilon.world.World;
import com.epsilon.world.content.combat.CombatFactory;
import com.epsilon.world.entity.impl.player.Player;

/**
 * This packet listener is called when a player has clicked on another player's
 * menu actions.
 * 
 * @author relex lawl
 */

public class PlayerOptionPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		if (player.getConstitution() <= 0)
			return;
		if(player.isTeleporting())
			return;
		switch(packet.getOpcode()) {
		case 153:
			attack(player, packet);
			break;
		case 128:
			option1(player, packet);
			break;
		case 37:
			option2(player, packet);
			break;
		case 227:
			option3(player, packet);
			break;
		}
	}

	private static void attack(Player player, Packet packet) {
		int index = packet.readLEShort();
		if(index > World.getPlayers().capacity() || index < 0)
			return;
		final Player attacked = World.getPlayers().get(index);
		
		if(attacked == null || attacked.getConstitution() <= 0 || attacked.equals(player)) {
			player.getMovementQueue().reset();
			return;
		}

		if(player.getLocation() == Location.DUEL_ARENA && player.getDueling().duelingStatus == 0) {
			//player.getDueling().challengePlayer(attacked);
			player.getGamble().requestTrade(attacked);
			return;
		}
		
		Item item = player.getEquipment().get(Equipment.WEAPON_SLOT);
		
		if(item != null && item.getId() == 10501) {
			
			if(!player.getPosition().isWithinDistance(attacked.getPosition())) {
				return;
			}
			
			player.getEquipment().delete(10501, 1);
			if(!player.getEquipment().contains(10501)) {
				player.getUpdateFlag().flag(Flag.APPEARANCE);
			}
			player.setPositionToFace(attacked.getPosition());
			player.performAnimation(new Animation(7530));
			TaskManager.submit(new Task(1, player, false) {
				
				int ticks = 0;
				
				public void execute() {
					switch(ticks++) {
					case 0:
						new Projectile(player, attacked, 1281, 20, 3, 21, 21, 0).sendProjectile();
						break;
					case 1:
						attacked.performGraphic(new Graphic(1282));
						this.stop();
						break;
					}	
				}
			});
			player.getMovementQueue().reset();
			
			return;
			
		}
		
		if(player.getCombatBuilder().getStrategy() == null) {
			player.getCombatBuilder().determineStrategy();
		}
		if (CombatFactory.checkAttackDistance(player, attacked)) {
			//System.out.println("test1");
			player.getMovementQueue().reset();
		} else {
			//System.out.println("test2");
		}
		
		player.getCombatBuilder().attack(attacked);
	}

	/**
	 * Manages the first option click on a player option menu.
	 * @param player	The player clicking the other entity.
	 * @param packet	The packet to read values from.
	 */
	private static void option1(Player player, Packet packet) {
		int id = packet.readShort() & 0xFFFF;
		if(id < 0 || id > World.getPlayers().capacity())
			return;
		Player victim = World.getPlayers().get(id);
		if (victim == null)
			return;
		/*GameServer.getTaskScheduler().schedule(new WalkToTask(player, victim.getPosition(), new FinalizedMovementTask() {
			@Override
			public void execute() {
				//do first option here
			}
		}));*/
	}

	/**
	 * Manages the second option click on a player option menu.
	 * @param player	The player clicking the other entity.
	 * @param packet	The packet to read values from.
	 */
	private static void option2(Player player, Packet packet) {
		int id = packet.readShort() & 0xFFFF;
		if(id < 0 || id > World.getPlayers().capacity())
			return;
		Player victim = World.getPlayers().get(id);
		if (victim == null)
			return;
		/*GameServer.getTaskScheduler().schedule(new WalkToTask(player, victim.getPosition(), new FinalizedMovementTask() {
			@Override
			public void execute() {
				//do second option here
			}
		}));*/
	}

	/**
	 * Manages the third option click on a player option menu.
	 * @param player	The player clicking the other entity.
	 * @param packet	The packet to read values from.
	 */
	private static void option3(Player player, Packet packet) {
		int id = packet.readLEShortA() & 0xFFFF;
		if(id < 0 || id > World.getPlayers().capacity())
			return;
		Player victim = World.getPlayers().get(id);
		if (victim == null)
			return;
		/*GameServer.getTaskScheduler().schedule(new WalkToTask(player, victim.getPosition(), new FinalizedMovementTask() {
			@Override
			public void execute() {
				//do third option here
			}
		}));*/
	}
}
