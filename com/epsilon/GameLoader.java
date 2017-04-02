package com.epsilon;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import mysql.MySQLController;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.util.HashedWheelTimer;

import com.epsilon.engine.GameEngine;
import com.epsilon.engine.task.TaskManager;
import com.epsilon.engine.task.impl.ServerTimeUpdateTask;
import com.epsilon.model.container.impl.Shop.ShopManager;
import com.epsilon.model.definitions.ItemDefinition;
import com.epsilon.model.definitions.NPCDrops;
import com.epsilon.model.definitions.NpcDefinition;
import com.epsilon.model.definitions.WeaponInterfaces;
import com.epsilon.net.PipelineFactory;
import com.epsilon.net.login.LoginResponses;
import com.epsilon.net.security.ConnectionHandler;
import com.epsilon.world.clip.region.RegionClipping;
import com.epsilon.world.content.CustomObjects;
import com.epsilon.world.content.Lottery;
import com.epsilon.world.content.PlayerPunishment;
import com.epsilon.world.content.ReferHandler;
import com.epsilon.world.content.Scoreboards;
import com.epsilon.world.content.WellOfGoodwill;
import com.epsilon.world.content.clan.ClanChatManager;
import com.epsilon.world.content.combat.effect.CombatPoisonEffect.CombatPoisonData;
import com.epsilon.world.content.combat.strategy.CombatStrategies;
import com.epsilon.world.content.dialogue.DialogueManager;
import com.epsilon.world.content.grandexchange.GrandExchangeOffers;
import com.epsilon.world.entity.impl.npc.NPC;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * Credit: Adam Trinity
 */
public final class GameLoader {

	private final ExecutorService serviceLoader = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("GameLoadingThread").build());
	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("GameThread").build());
	private final GameEngine engine;
	private final int port;

	protected GameLoader(int port) {
		this.port = port;
		this.engine = new GameEngine();
	}

	public void init() {
		Preconditions.checkState(!serviceLoader.isShutdown(), "The bootstrap has been bound already!");
		if(GameSettings.FORUM_INT == true) {
			LoginResponses.init();
		}
		executeServiceLoad();
		serviceLoader.shutdown();
	}

	public void finish() throws IOException, InterruptedException {
		if (!serviceLoader.awaitTermination(15, TimeUnit.MINUTES))
			throw new IllegalStateException("The background service load took too long!");
		ExecutorService networkExecutor = Executors.newCachedThreadPool();
		ServerBootstrap serverBootstrap = new ServerBootstrap (new NioServerSocketChannelFactory(networkExecutor, networkExecutor));
        serverBootstrap.setPipelineFactory(new PipelineFactory(new HashedWheelTimer()));
        serverBootstrap.bind(new InetSocketAddress(port));
		executor.scheduleAtFixedRate(engine, 0, GameSettings.ENGINE_PROCESSING_CYCLE_RATE, TimeUnit.MILLISECONDS);
		TaskManager.submit(new ServerTimeUpdateTask());
	}

	private void executeServiceLoad() {
		if (GameSettings.MYSQL_ENABLED) {
			serviceLoader.execute(() -> MySQLController.init());
		}
		serviceLoader.execute(() -> ReferHandler.load());
		serviceLoader.execute(() -> ConnectionHandler.init());
		serviceLoader.execute(() -> PlayerPunishment.init());
		serviceLoader.execute(() -> RegionClipping.init());
		serviceLoader.execute(() -> CustomObjects.init());
		serviceLoader.execute(() -> ItemDefinition.init());
		serviceLoader.execute(() -> Lottery.init());
		serviceLoader.execute(() -> GrandExchangeOffers.init());
		serviceLoader.execute(() -> Scoreboards.init());
		serviceLoader.execute(() -> WellOfGoodwill.init());
		serviceLoader.execute(() -> ClanChatManager.init());
		serviceLoader.execute(() -> CombatPoisonData.init());
		serviceLoader.execute(() -> CombatStrategies.init());
		serviceLoader.execute(() -> NpcDefinition.parseNpcs().load());
		serviceLoader.execute(() -> NPCDrops.parseDrops().load());
		serviceLoader.execute(() -> WeaponInterfaces.parseInterfaces().load());
		serviceLoader.execute(() -> ShopManager.parseShops().load());
		serviceLoader.execute(() -> DialogueManager.parseDialogues().load());
		serviceLoader.execute(() -> NPC.init());
		//serviceLoader.execute(() -> Panel.main());
	}

	public GameEngine getEngine() {
		return engine;
	}
}