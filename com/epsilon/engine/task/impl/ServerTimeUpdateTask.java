package com.epsilon.engine.task.impl;

import com.epsilon.GameSettings;
import com.epsilon.engine.task.Task;
import com.epsilon.model.Locations;
import com.epsilon.util.Misc;
import com.epsilon.world.World;
import com.epsilon.world.content.WellOfGoodwill;
import com.epsilon.world.content.minigames.impl.PestControl;
import com.epsilon.world.entity.impl.player.Player;

/**
 * @author Gabriel Hannason
 */
public class ServerTimeUpdateTask extends Task {

	public ServerTimeUpdateTask() {
		super(40);
	}

	private int tick = 0;
	Player player;

	@Override
	protected void execute() {
		World.updateServerTime();
		World.updatePlayersOnline();
		
		if(tick >= 6 && (Locations.PLAYERS_IN_WILD >= 3 || Locations.PLAYERS_IN_DUEL_ARENA >= 3 || PestControl.TOTAL_PLAYERS >= 3)) {
			if(Locations.PLAYERS_IN_WILD > Locations.PLAYERS_IN_DUEL_ARENA && Locations.PLAYERS_IN_WILD > PestControl.TOTAL_PLAYERS || Misc.getRandom(3) == 1 && Locations.PLAYERS_IN_WILD >= 2) {
				World.sendMessage("<img=10> @blu@There are currently "+Locations.PLAYERS_IN_WILD+" players roaming the Wilderness!");
			} else if(Locations.PLAYERS_IN_DUEL_ARENA > Locations.PLAYERS_IN_WILD && Locations.PLAYERS_IN_DUEL_ARENA > PestControl.TOTAL_PLAYERS) {
				World.sendMessage("<img=10> @blu@There are currently "+Locations.PLAYERS_IN_DUEL_ARENA+" players at the Duel Arena!");
			} else if(PestControl.TOTAL_PLAYERS > Locations.PLAYERS_IN_WILD && PestControl.TOTAL_PLAYERS > Locations.PLAYERS_IN_DUEL_ARENA) {
				World.sendMessage("<img=10> @blu@There are currently "+PestControl.TOTAL_PLAYERS+" players at Pest Control!");
			}
			tick = 0;
		}
		
		GameSettings.DOUBLE_LOOT = Misc.isWeekend();

		tick++;
	}
}