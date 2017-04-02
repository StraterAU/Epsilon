package com.epsilon.world;

import java.util.Iterator;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

import mysql.impl.Voting;

import com.epsilon.GameSettings;
import com.epsilon.model.PlayerRights;
import com.epsilon.util.Misc;
import com.epsilon.util.Stopwatch;
import com.epsilon.world.content.ChristmasEvent;
import com.epsilon.world.content.ShootingStar;
import com.epsilon.world.content.Trivia;
import com.epsilon.world.content.WellOfGoodwill;
import com.epsilon.world.content.Trivia.Questions;
import com.epsilon.world.content.minigames.impl.FightPit;
import com.epsilon.world.content.minigames.impl.PestControl;
import com.epsilon.world.entity.Entity;
import com.epsilon.world.entity.EntityHandler;
import com.epsilon.world.entity.impl.CharacterList;
import com.epsilon.world.entity.impl.npc.NPC;
import com.epsilon.world.entity.impl.player.Player;
import com.epsilon.world.entity.impl.player.PlayerHandler;
import com.epsilon.world.entity.updating.NpcUpdateSequence;
import com.epsilon.world.entity.updating.PlayerUpdateSequence;
import com.epsilon.world.entity.updating.UpdateSequence;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * @author Gabriel Hannason
 * Thanks to lare96 for help with parallel updating system
 */
public class World {

	/** All of the registered players. */
	public static CharacterList<Player> players = new CharacterList<>(1000);

	/** All of the registered NPCs. */
	private static CharacterList<NPC> npcs = new CharacterList<>(2027);

	/** Used to block the game thread until updating has completed. */
	private static Phaser synchronizer = new Phaser(1);

	/** A thread pool that will update players in parallel. */
	private static ExecutorService updateExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactoryBuilder().setNameFormat("UpdateThread").setPriority(Thread.MAX_PRIORITY).build());

	/** The queue of {@link Player}s waiting to be logged in. **/
    private static Queue<Player> logins = new ConcurrentLinkedQueue<>();

    /**The queue of {@link Player}s waiting to be logged out. **/
    private static Queue<Player> logouts = new ConcurrentLinkedQueue<>();
    
    /**The queue of {@link Player}s waiting to be given their vote reward. **/
    private static Queue<Player> voteRewards = new ConcurrentLinkedQueue<>();
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
    
    public static void register(Entity entity) {
		EntityHandler.register(entity);
	}

	public static void deregister(Entity entity) {
		EntityHandler.deregister(entity);
	}

	public static Player getPlayerByName(String username) {
		Optional<Player> op = players.search(p -> p != null && p.getUsername().equals(Misc.formatText(username)));
		return op.isPresent() ? op.get() : null;
	}

	public static Player getPlayerByLong(long encodedName) {
		Optional<Player> op = players.search(p -> p != null && p.getLongUsername().equals(encodedName));
		return op.isPresent() ? op.get() : null;
	}

	public static void sendMessage(String message) {
		players.forEach(p -> p.getPacketSender().sendMessage(message));
	}
	
	public static void sendStaffMessage(String message) {
		players.stream().filter(p -> p != null && (p.hasRights(PlayerRights.OWNER) || p.hasRights(PlayerRights.DEVELOPER) || p.hasRights(PlayerRights.ADMINISTRATOR) || p.hasRights(PlayerRights.MODERATOR) || p.hasRights(PlayerRights.SUPPORT))).forEach(p -> p.getPacketSender().sendMessage(message));
	}
	
	public static void updateServerTime() {
		if (Player.serverInformation == true) {
		players.forEach(p -> p.getPacketSender().sendString(50052, "@or1@Server Time: @yel@[ @whi@"+Misc.getCurrentServerTime()+"@yel@ ]"));
		players.forEach(p -> p.getPacketSender().sendString(50056, "@or1@Well of Goodwill: " + (WellOfGoodwill.isActive() ? "@gre@Active" : "@red@Inactive")));
		}
	}

	public static void updatePlayersOnline() {
		if (Player.serverInformation == true) {
		players.forEach(p -> p.getPacketSender().sendString(50051, "@or1@Players Online: @whi@"+(int)(players.size())+""));
		}
		players.forEach(p -> p.getPacketSender().sendString(57003, "Players:  @gre@"+World.getPlayers().size()));
	}

	public static void savePlayers() {
		players.forEach(p -> p.save());
	}

	public static CharacterList<Player> getPlayers() {
		return players;
	}

	public static CharacterList<NPC> getNpcs() {
		return npcs;
	}
	
	public static void sequence() {
		
		 // Handle queued logins.
        for (int amount = 0; amount < GameSettings.LOGIN_THRESHOLD; amount++) {
            Player player = logins.poll();
            if (player == null)
                break;
            PlayerHandler.handleLogin(player);
        }

        // Handle queued logouts.
        int amount = 0;
        Iterator<Player> $it = logouts.iterator();
        while ($it.hasNext()) {
            Player player = $it.next();
            if (player == null || amount >= GameSettings.LOGOUT_THRESHOLD)
                break;
            if (PlayerHandler.handleLogout(player)) {
                $it.remove();
                amount++;
            }
        }
        
        // Handle queued vote rewards
        for(int i = 0; i < GameSettings.VOTE_REWARDING_THRESHOLD; i++) {
            Player player = voteRewards.poll();
            if (player == null)
                break;
            Voting.handleQueuedReward(player);
        }
        
       // Trivia.sequence();
        FightPit.sequence();
		PestControl.sequence();
		ShootingStar.sequence();
		//ChristmasEvent.sequence();
	
		
		// First we construct the update sequences.
		UpdateSequence<Player> playerUpdate = new PlayerUpdateSequence(synchronizer, updateExecutor);
		UpdateSequence<NPC> npcUpdate = new NpcUpdateSequence();
		// Then we execute pre-updating code.
		players.forEach(playerUpdate::executePreUpdate);
		npcs.forEach(npcUpdate::executePreUpdate);
		// Then we execute parallelized updating code.
		synchronizer.bulkRegister(players.size());
		players.forEach(playerUpdate::executeUpdate);
		synchronizer.arriveAndAwaitAdvance();
		// Then we execute post-updating code.
		players.forEach(playerUpdate::executePostUpdate);
		npcs.forEach(npcUpdate::executePostUpdate);
	}

	public static Queue<Player> getLoginQueue() {
		return logins;
	}
	
	public static boolean loginQueueContains(Player player) {
		synchronized(logins) {
			for(Iterator<Player> $i = logins.iterator(); $i.hasNext();) {
				if($i.next().getUsername().equalsIgnoreCase(player.getUsername())) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static Queue<Player> getLogoutQueue() {
		return logouts;
	}
	
	public static Queue<Player> getVoteRewardingQueue() {
		return voteRewards;
	}
}
