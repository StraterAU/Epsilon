package com.epsilon.world.entity.impl.player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.epsilon.engine.task.impl.FamiliarSpawnTask;
import com.epsilon.model.GameMode;
import com.epsilon.model.Gender;
import com.epsilon.model.Item;
import com.epsilon.model.MagicSpellbook;
import com.epsilon.model.PlayerRights;
import com.epsilon.model.Position;
import com.epsilon.model.Prayerbook;
import com.epsilon.model.PlayerRelations.PrivateChatStatus;
import com.epsilon.model.container.impl.Bank;
import com.epsilon.net.login.LoginResponses;
import com.epsilon.world.content.DropLog;
import com.epsilon.world.content.KillsTracker;
import com.epsilon.world.content.DropLog.DropLogEntry;
import com.epsilon.world.content.ExperienceModes;
import com.epsilon.world.content.KillsTracker.KillsEntry;
import com.epsilon.world.content.LoyaltyProgramme.LoyaltyTitles;
import com.epsilon.world.content.combat.magic.CombatSpells;
import com.epsilon.world.content.combat.weapon.FightType;
import com.epsilon.world.content.grandexchange.GrandExchangeOffer;
import com.epsilon.world.content.grandexchange.GrandExchangeSlot;
import com.epsilon.world.content.grandexchange.GrandExchangeSlotState;
import com.epsilon.world.content.skill.SkillManager.Skills;
import com.epsilon.world.content.skill.impl.construction.ConstructionSave;
import com.epsilon.world.content.skill.impl.slayer.SlayerMaster;
import com.epsilon.world.content.skill.impl.slayer.SlayerTasks;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class PlayerLoading {
	
	public static int getResult(Player player) {

		// Create the path and file objects.
		Path path = Paths.get("./data/saves/characters/", player.getUsername() + ".json");
		File file = path.toFile();

		// If the file doesn't exist, we're logging in for the first
		// time and can skip all of this.
		if (!file.exists()) {
			return LoginResponses.NEW_ACCOUNT;
		}

		// Now read the properties from the json parser.
		try (FileReader fileReader = new FileReader(file)) {
			JsonParser fileParser = new JsonParser();
			Gson builder = new GsonBuilder().create();
			JsonObject reader = (JsonObject) fileParser.parse(fileReader);


			if (reader.has("total-play-time-ms")) {
				player.setTotalPlayTime(reader.get("total-play-time-ms").getAsLong());
			}

			if (reader.has("username")) {
				player.setUsername(reader.get("username").getAsString());
			}

			if (reader.has("password")) {
				String password = reader.get("password").getAsString();
				if (!player.getPassword().equals(password)) {
					return LoginResponses.LOGIN_INVALID_CREDENTIALS;
				}
				player.setPassword(password);
			}

			if (reader.has("email")) {
				player.setEmailAddress(reader.get("email").getAsString());
			}
			
			if (reader.has("staff-rights")) {
				if(player.getRights() == null) {
					player.setRights(new ArrayList<>());
				} else {
					player.getRights().clear();
				}
				player.getRights().add(PlayerRights.valueOf(reader.get("staff-rights").getAsString()));
			}

			if (reader.has("player-rights")) {
				int[] ordinals = builder.fromJson(reader.get("player-rights").getAsJsonArray(), int[].class);
				if(player.getRights() == null) {
					player.setRights(new ArrayList<>());
				} else {
					player.getRights().clear();
				}
				for(int ordinal : ordinals) {
					player.getRights().add(PlayerRights.values()[ordinal]);
				}
			}
			
			if(reader.has("yell-title")) {
				player.setYellTitle(reader.get("yell-title").getAsString());
				if(player.getYellTitle() != null && player.getYellTitle().equals("null")) {
					player.setYellTitle(null);
				}
			}
			
			if (reader.has("game-mode")) {
				player.setGameMode(GameMode.valueOf(reader.get("game-mode").getAsString()));
			}
			
			if (reader.has("experience-mode")) {
				player.setExperienceMode(ExperienceModes.valueOf(reader.get("experience-mode").getAsString()));
			}
			

			if (reader.has("loyalty-title")) {
				player.setLoyaltyTitle(LoyaltyTitles.valueOf(reader.get("loyalty-title").getAsString()));
			}

			if (reader.has("position")) {
				player.getPosition().setAs(builder.fromJson(reader.get("position"), Position.class));
			}
			
			if(reader.has("online-status")) {
				player.getRelations().setStatus(PrivateChatStatus.valueOf(reader.get("online-status").getAsString()), false);
			}
			
			if (reader.has("hcilives")) {
				player.setHCILives(reader.get("hcilives").getAsInt());
			}

			if (reader.has("money-pouch")) {
				player.setMoneyInPouch(reader.get("money-pouch").getAsLong());
			}
			if (reader.has("pw-changed")) {
				player.setChangedPassword(reader.get("pw-changed").getAsBoolean());
			}
			
			if (reader.has("inferno-smelt")) {
				player.setInfernoSmelt(reader.get("inferno-smelt").getAsBoolean());
			}
			
			if (reader.has("use-snow")) {
				player.setUseSnow(reader.get("use-snow").getAsBoolean());
			}

			if (reader.has("given-starter")) {
				player.setReceivedStarter(reader.get("given-starter").getAsBoolean());
			}

			if (reader.has("donated")) {
				player.incrementAmountDonated(reader.get("donated").getAsInt());
			}
			
			
			if(reader.has("minutes-bonus-exp")) {
				player.setMinutesBonusExp(reader.get("minutes-bonus-exp").getAsInt(), false);
			}

			if (reader.has("total-gained-exp")) {
				player.getSkillManager().setTotalGainedExp(reader.get("total-gained-exp").getAsInt());
			}

			if (reader.has("dung-tokens")) {
				player.getPointsHandler().setDungeoneeringTokens(reader.get("dung-tokens").getAsInt(), false);
			}

			if(reader.has("prestige-points")) {
				player.getPointsHandler().setPrestigePoints(reader.get("prestige-points").getAsInt(), false);
			}
			
			if(reader.has("achievement-points")) {
				player.getPointsHandler().setAchievementPoints(reader.get("achievement-points").getAsInt(), false);
			}
			
			if(reader.has("commendations")) {
				player.getPointsHandler().setCommendations(reader.get("commendations").getAsInt(), false);
			}
			
			if(reader.has("loyalty-points")) {
				player.getPointsHandler().setLoyaltyPoints(reader.get("loyalty-points").getAsInt(), false);
			}
			
			if (reader.has("total-loyalty-points")) {
				player.getAchievementAttributes().incrementTotalLoyaltyPointsEarned(reader.get("total-loyalty-points").getAsDouble());
			}

			if(reader.has("voting-points")) {
				player.getPointsHandler().setVotingPoints(reader.get("voting-points").getAsInt(), false);
			}
			if(reader.has("trivia-points")) {
                   player.getPointsHandler().setTriviaPoints(reader.get("trivia-points").getAsInt());
			}
			if(reader.has("slayer-points")) {
				player.getPointsHandler().setSlayerPoints(reader.get("slayer-points").getAsInt(), false);
			}
			if(reader.has("im-points")) {
				player.getPointsHandler().setIronManPoints(reader.get("im-points").getAsInt(), false);
			}
		    if(reader.has("refeer-friend")) {
			player.getPointsHandler().setRefeerFriendPoints(reader.get("refeer-friend").getAsInt(), false);
		    }
		    if(reader.has("refered-friend")) {
			player.setReferedAFriend(reader.get("refered-friend").getAsBoolean());
		}
		if(reader.has("received-br-reward")) {
			player.setReceivedBrReward(reader.get("received-br-reward").getAsBoolean());
		}
			if(reader.has("pk-points")) {
				player.getPointsHandler().setPkPoints(reader.get("pk-points").getAsInt(), false);
			}

			if(reader.has("player-kills")) {
				player.getPlayerKillingAttributes().setPlayerKills(reader.get("player-kills").getAsInt());
			}

			if(reader.has("player-killstreak")) {
				player.getPlayerKillingAttributes().setPlayerKillStreak(reader.get("player-killstreak").getAsInt());
			}

			if(reader.has("player-deaths")) {
				player.getPlayerKillingAttributes().setPlayerDeaths(reader.get("player-deaths").getAsInt());
			}

			if(reader.has("target-percentage")) {
				player.getPlayerKillingAttributes().setTargetPercentage(reader.get("target-percentage").getAsInt());
			}

			if(reader.has("bh-rank")) {
				player.getAppearance().setBountyHunterSkull(reader.get("bh-rank").getAsInt());
			}

			if (reader.has("gender")) {
				player.getAppearance().setGender(Gender.valueOf(reader.get("gender").getAsString()));
			}
			if (reader.has("date-joined")) {
				player.getPointsHandler().setDateJoined(reader.get("date-joined").getAsString());
			}
			if (reader.has("spell-book")) {
				player.setSpellbook(MagicSpellbook.valueOf(reader.get("spell-book").getAsString()));
			}

			if (reader.has("prayer-book")) {
				player.setPrayerbook(Prayerbook.valueOf(reader.get("prayer-book").getAsString()));
			}
			if (reader.has("running")) {
				player.setRunning(reader.get("running").getAsBoolean());
			}
			if (reader.has("run-energy")) {
				player.setRunEnergy(reader.get("run-energy").getAsInt());
			}
			if (reader.has("music")) {
				player.setMusicActive(reader.get("music").getAsBoolean());
			}
			if (reader.has("sounds")) {
				player.setSoundsActive(reader.get("sounds").getAsBoolean());
			}
			if (reader.has("auto-retaliate")) {
				player.setAutoRetaliate(reader.get("auto-retaliate").getAsBoolean());
			}
			if (reader.has("xp-locked")) {
				player.setExperienceLocked(reader.get("xp-locked").getAsBoolean());
			}
			if (reader.has("veng-cast")) {
				player.setHasVengeance(reader.get("veng-cast").getAsBoolean());
			}
			if (reader.has("last-veng")) {
				player.getLastVengeance().reset(reader.get("last-veng").getAsLong());
			}
			if (reader.has("fight-type")) {
				player.setFightType(FightType.valueOf(reader.get("fight-type").getAsString()));
			}
			if(reader.has("sol-effect")) {
				player.setStaffOfLightEffect(Integer.valueOf(reader.get("sol-effect").getAsInt()));
			}
			if (reader.has("skull-timer")) {
				player.setSkullTimer(reader.get("skull-timer").getAsInt());
			}
			if (reader.has("accept-aid")) {
				player.setAcceptAid(reader.get("accept-aid").getAsBoolean());
			}
			if (reader.has("poison-damage")) {
				player.setPoisonDamage(reader.get("poison-damage").getAsInt());
			}
			if (reader.has("poison-immunity")) {
				player.setPoisonImmunity(reader.get("poison-immunity").getAsInt());
			}
			if (reader.has("overload-timer")) {
				player.setOverloadPotionTimer(reader.get("overload-timer").getAsInt());
			}
			if (reader.has("fire-immunity")) {
				player.setFireImmunity(reader.get("fire-immunity").getAsInt());
			}
			if (reader.has("fire-damage-mod")) {
				player.setFireDamageModifier(reader.get("fire-damage-mod").getAsInt());
			}
			if (reader.has("overload-timer")) {
				player.setOverloadPotionTimer(reader.get("overload-timer").getAsInt());
			}
			if (reader.has("prayer-renewal-timer")) {
				player.setPrayerRenewalPotionTimer(reader.get("prayer-renewal-timer").getAsInt());
			}
			if (reader.has("teleblock-timer")) {
				player.setTeleblockTimer(reader.get("teleblock-timer").getAsInt());
			}
			if (reader.has("special-amount")) {
				player.setSpecialPercentage(reader.get("special-amount").getAsInt());
			}
			
			if(reader.has("entered-gwd-room")) {
				player.getMinigameAttributes().getGodwarsDungeonAttributes().setHasEnteredRoom(reader.get("entered-gwd-room").getAsBoolean());
			}
			
			if(reader.has("gwd-altar-delay")) {
				player.getMinigameAttributes().getGodwarsDungeonAttributes().setAltarDelay(reader.get("gwd-altar-delay").getAsLong());
			}
			
			if(reader.has("gwd-killcount")) {
				player.getMinigameAttributes().getGodwarsDungeonAttributes().setKillcount(builder.fromJson(reader.get("gwd-killcount"), int[].class));
			}
			
			if(reader.has("effigy")) {
				player.setEffigy(reader.get("effigy").getAsInt());
			}
			
			if (reader.has("summon-npc")) {
				int npc = reader.get("summon-npc").getAsInt();
				if(npc > 0)
					player.getSummoning().setFamiliarSpawnTask(new FamiliarSpawnTask(player)).setFamiliarId(npc);
			}
			if (reader.has("summon-death")) {
				int death = reader.get("summon-death").getAsInt();
				if(death > 0 && player.getSummoning().getSpawnTask() != null)
					player.getSummoning().getSpawnTask().setDeathTimer(death);
			}
			if (reader.has("process-farming")) {
				player.setProcessFarming(reader.get("process-farming").getAsBoolean());
			}

			if (reader.has("clanchat")) {
				String clan = reader.get("clanchat").getAsString();
				if(!clan.equals("null"))
					player.setClanChatName(clan);
			}
			if (reader.has("autocast")) {
				player.setAutocast(reader.get("autocast").getAsBoolean());
			}
			if (reader.has("autocast-spell")) {
				int spell = reader.get("autocast-spell").getAsInt();
				if(spell != -1)
					player.setAutocastSpell(CombatSpells.getSpell(spell));
			}

			if (reader.has("dfs-charges")) {
				player.incrementDfsCharges(reader.get("dfs-charges").getAsInt());
			}
			if (reader.has("kills")) {
				KillsTracker.submit(player, builder.fromJson(reader.get("kills").getAsJsonArray(), KillsEntry[].class));
			}
			
			if (reader.has("drops")) {
				DropLog.submit(player, builder.fromJson(reader.get("drops").getAsJsonArray(), DropLogEntry[].class));
			}

			if (reader.has("coins-gambled")) {
				player.getAchievementAttributes().setCoinsGambled(reader.get("coins-gambled").getAsInt());
			}
			
			if (reader.has("slayer-master")) {
				player.getSlayer().setSlayerMaster(SlayerMaster.valueOf(reader.get("slayer-master").getAsString()));
			}

			if (reader.has("slayer-task")) {
				player.getSlayer().setSlayerTask(SlayerTasks.valueOf(reader.get("slayer-task").getAsString()));
			}

			if (reader.has("prev-slayer-task")) {
				player.getSlayer().setLastTask(SlayerTasks.valueOf(reader.get("prev-slayer-task").getAsString()));
			}

			if (reader.has("task-amount")) {
				player.getSlayer().setAmountToSlay(reader.get("task-amount").getAsInt());
			}

			if (reader.has("task-streak")) {
				player.getSlayer().setTaskStreak(reader.get("task-streak").getAsInt());
			}

			if (reader.has("duo-partner")) {
				String partner = reader.get("duo-partner").getAsString();
				player.getSlayer().setDuoPartner(partner.equals("null") ? null : partner);
			}

			if (reader.has("double-slay-xp")) {
				player.getSlayer().doubleSlayerXP = reader.get("double-slay-xp").getAsBoolean();
			}
			
			if (reader.has("recoil-deg")) {
				player.setRecoilCharges(reader.get("recoil-deg").getAsInt());
			}
			
			if(reader.has("brawler-deg")) {
				int[] array = builder.fromJson(reader.get("brawler-deg").getAsJsonArray(), int[].class);
				int[] charges = new int[90];
				for(int i = 0; i < array.length; i++) {
					charges[i] = array[i];
				}
				player.setBrawlerCharges(charges);
			}

			if (reader.has("killed-players")) {
				List<String> list = new ArrayList<String>();
				String[] killed_players = builder.fromJson(reader.get("killed-players").getAsJsonArray(), String[].class);
				for(String s : killed_players)
					list.add(s);
				player.getPlayerKillingAttributes().setKilledPlayers(list);
			}

			if (reader.has("killed-gods")) {
				player.getAchievementAttributes().setGodsKilled(builder.fromJson(reader.get("killed-gods").getAsJsonArray(), boolean[].class));
			}

			if (reader.has("barrows-brother")) {
				player.getMinigameAttributes().getBarrowsMinigameAttributes().setBarrowsData(builder.fromJson(reader.get("barrows-brother").getAsJsonArray(), int[][].class));
			}

			if (reader.has("random-coffin")) {
				player.getMinigameAttributes().getBarrowsMinigameAttributes().setRandomCoffin((reader.get("random-coffin").getAsInt()));
			}

			if (reader.has("barrows-killcount")) {
				player.getMinigameAttributes().getBarrowsMinigameAttributes().setKillcount((reader.get("barrows-killcount").getAsInt()));
			}

			if (reader.has("nomad")) {
				player.getMinigameAttributes().getNomadAttributes().setQuestParts(builder.fromJson(reader.get("nomad").getAsJsonArray(), boolean[].class));
			}

			if (reader.has("recipe-for-disaster")) {
				player.getMinigameAttributes().getRecipeForDisasterAttributes().setQuestParts(builder.fromJson(reader.get("recipe-for-disaster").getAsJsonArray(), boolean[].class));
			}

			if (reader.has("recipe-for-disaster-wave")) {
				player.getMinigameAttributes().getRecipeForDisasterAttributes().setWavesCompleted((reader.get("recipe-for-disaster-wave").getAsInt()));
			}
			
			if (reader.has("dung-items-bound")) {
				player.getMinigameAttributes().getDungeoneeringAttributes().setBoundItems(builder.fromJson(reader.get("dung-items-bound").getAsJsonArray(), int[].class));
			}

			if (reader.has("rune-ess")) {
				player.setStoredRuneEssence((reader.get("rune-ess").getAsInt()));
			}

			if (reader.has("pure-ess")) {
				player.setStoredPureEssence((reader.get("pure-ess").getAsInt()));
			}

			if (reader.has("bank-pin")) {
				player.getBankPinAttributes().setBankPin(builder.fromJson(reader.get("bank-pin").getAsJsonArray(), int[].class));
			}

			if (reader.has("has-bank-pin")) {
				player.getBankPinAttributes().setHasBankPin(reader.get("has-bank-pin").getAsBoolean());
			}
			if (reader.has("last-pin-attempt")) {
				player.getBankPinAttributes().setLastAttempt(reader.get("last-pin-attempt").getAsLong());
			}
			if (reader.has("invalid-pin-attempts")) {
				player.getBankPinAttributes().setInvalidAttempts(reader.get("invalid-pin-attempts").getAsInt());
			}

			if (reader.has("bank-pin")) {
				player.getBankPinAttributes().setBankPin(builder.fromJson(reader.get("bank-pin").getAsJsonArray(), int[].class));
			}

			if (reader.has("appearance")) {
				player.getAppearance().set(builder.fromJson(
						reader.get("appearance").getAsJsonArray(), int[].class));
			}

			if (reader.has("agility-obj")) {
				player.setCrossedObstacles(builder.fromJson(
						reader.get("agility-obj").getAsJsonArray(), boolean[].class));
			}

			if (reader.has("skills")) {
				player.getSkillManager().setSkills(builder.fromJson(
						reader.get("skills"), Skills.class));
			}
			if (reader.has("inventory")) {
				player.getInventory().setItems(builder.fromJson(reader.get("inventory").getAsJsonArray(), Item[].class));
			}
			if (reader.has("equipment")) {
				player.getEquipment().setItems(builder.fromJson(reader.get("equipment").getAsJsonArray(), Item[].class));
			}

			/** BANK **/
			for(int i = 0; i < 9; i++) {
				if(reader.has("bank-"+i+""))
					player.setBank(i, new Bank(player)).getBank(i).addItems(builder.fromJson(reader.get("bank-"+i+"").getAsJsonArray(), Item[].class), false);
			}
			
			if(reader.has("bank-0")) {
				player.setBank(0, new Bank(player)).getBank(0).addItems(builder.fromJson(reader.get("bank-0").getAsJsonArray(), Item[].class), false);
			}
			if(reader.has("bank-1")) {
				player.setBank(1, new Bank(player)).getBank(1).addItems(builder.fromJson(reader.get("bank-1").getAsJsonArray(), Item[].class), false);
			}
			if(reader.has("bank-2")) {
				player.setBank(2, new Bank(player)).getBank(2).addItems(builder.fromJson(reader.get("bank-2").getAsJsonArray(), Item[].class), false);
			}
			if(reader.has("bank-3")) {
				player.setBank(3, new Bank(player)).getBank(3).addItems(builder.fromJson(reader.get("bank-3").getAsJsonArray(), Item[].class), false);
			}
			if(reader.has("bank-4")) {
				player.setBank(4, new Bank(player)).getBank(4).addItems(builder.fromJson(reader.get("bank-4").getAsJsonArray(), Item[].class), false);
			}
			if(reader.has("bank-5")) {
				player.setBank(5, new Bank(player)).getBank(5).addItems(builder.fromJson(reader.get("bank-5").getAsJsonArray(), Item[].class), false);
			}
			if(reader.has("bank-6")) {
				player.setBank(6, new Bank(player)).getBank(6).addItems(builder.fromJson(reader.get("bank-6").getAsJsonArray(), Item[].class), false);
			}
			if(reader.has("bank-7")) {
				player.setBank(7, new Bank(player)).getBank(7).addItems(builder.fromJson(reader.get("bank-7").getAsJsonArray(), Item[].class), false);
			}
			if(reader.has("bank-8")) {
				player.setBank(8, new Bank(player)).getBank(8).addItems(builder.fromJson(reader.get("bank-8").getAsJsonArray(), Item[].class), false);
			}
			
			if (reader.has("ge-slots")) {
				GrandExchangeSlot[] slots = builder.fromJson(reader.get("ge-slots").getAsJsonArray(), GrandExchangeSlot[].class);
				player.setGrandExchangeSlots(slots);
			}
			
			if (reader.has("store")) {
				Item[] validStoredItems = builder.fromJson(reader.get("store").getAsJsonArray(), Item[].class);
				if(player.getSummoning().getSpawnTask() != null)
					player.getSummoning().getSpawnTask().setValidItems(validStoredItems);
			}
			
			if (reader.has("charm-imp")) {
				int[] charmImpConfig = builder.fromJson(reader.get("charm-imp").getAsJsonArray(), int[].class);
				player.getSummoning().setCharmimpConfig(charmImpConfig);
			}
			
			if (reader.has("friends")) {
				long[] friends = builder.fromJson(
						reader.get("friends").getAsJsonArray(), long[].class);

				for (long l : friends) {
					player.getRelations().getFriendList().add(l);
				}
			}
			if (reader.has("ignores")) {
				long[] ignores = builder.fromJson(
						reader.get("ignores").getAsJsonArray(), long[].class);

				for (long l : ignores) {
					player.getRelations().getIgnoreList().add(l);
				}
			}

			if (reader.has("loyalty-titles")) {
				player.setUnlockedLoyaltyTitles(builder.fromJson(reader.get("loyalty-titles").getAsJsonArray(), boolean[].class));
			}

			if (reader.has("achievements-completion")) {
				player.getAchievementAttributes().setCompletion(builder.fromJson(reader.get("achievements-completion").getAsJsonArray(), boolean[].class));
			}
			
			if (reader.has("achievements-progress")) {
				player.getAchievementAttributes().setProgress(builder.fromJson(reader.get("achievements-progress").getAsJsonArray(), int[].class));
			}
			
			if (reader.has("gem-bag")) {
				int[] ids = builder.fromJson(reader.get("gem-bag").getAsJsonArray(), int[].class);
				player.getGemBag().fill(ids);
			}
			
			if (reader.has("gem-bag-upgrade")) {
				int[] ids = builder.fromJson(reader.get("gem-bag-upgrade").getAsJsonArray(), int[].class);
				player.getGemBagUpgrade().fill(ids);
			}
			
			if (reader.has("giant-pouch")) {
				int[] ids = builder.fromJson(reader.get("giant-pouch").getAsJsonArray(), int[].class);
				player.getGiantPouch().fill(ids);
			}
			
			if (reader.has("max-cape-colors")) {
				int[] colors = builder.fromJson(reader.get("max-cape-colors").getAsJsonArray(), int[].class);
				player.setMaxCapeColors(colors);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return LoginResponses.LOGIN_SUCCESSFUL;
		}
		try {
			
			Path path2 = Paths.get("./data/saves/construction/", player.getUsername() + ".obj");
			File file2 = path2.toFile();
			
			if(file2.exists()) {
				FileInputStream fileIn = new FileInputStream(file2);
				ObjectInputStream in = new ObjectInputStream(fileIn);
				ConstructionSave save = (ConstructionSave) in.readObject();
				player.setHouseRooms(save.getHouseRooms());
				player.setHouseFurtinture(save.getHouseFurniture());
				player.setHousePortals(save.getHousePortals());
				in.close();
				fileIn.close();
			}
			
		} catch(Throwable t) {
			t.printStackTrace();
		}
		return LoginResponses.LOGIN_SUCCESSFUL;
	}
}