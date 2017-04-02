package com.epsilon.world.content.skill.impl.mining;

import com.epsilon.engine.task.Task;
import com.epsilon.engine.task.TaskManager;
import com.epsilon.model.Animation;
import com.epsilon.model.GameMode;
import com.epsilon.model.GameObject;
import com.epsilon.model.Locations;
import com.epsilon.model.Skill;
import com.epsilon.util.Misc;
import com.epsilon.world.World;
import com.epsilon.world.content.Achievements;
import com.epsilon.world.content.CustomObjects;
import com.epsilon.world.content.ShootingStar;
import com.epsilon.world.content.Sounds;
import com.epsilon.world.content.Achievements.AchievementData;
import com.epsilon.world.content.Sounds.Sound;
import com.epsilon.world.content.skill.impl.mining.MiningData.Ores;
import com.epsilon.world.content.skill.impl.smithing.SmithingData;
import com.epsilon.world.entity.impl.player.Player;

public class Mining {
	
	public static final int[] GOLDEN_MINING_SUIT = { 20787, 20788, 20789, 20790, 20791 };
	
	public static final int[] ITEMS = { 20787, 20788, 20789, 20790, 20791, 13661 };
	
	public static final int INFERNO_ADZE = 13661;

	public static void startMining(final Player player, final GameObject oreObject) {
		player.getSkillManager().stopSkilling();
		player.getPacketSender().sendInterfaceRemoval();
		if(!Locations.goodDistance(player.getPosition().copy(), oreObject.getPosition(), 1) && oreObject.getId() != 24444  && oreObject.getId() != 24445 && oreObject.getId() != 38660)
			return;
		if(player.busy() || player.getCombatBuilder().isBeingAttacked() || player.getCombatBuilder().isAttacking()) {
			player.getPacketSender().sendMessage("You cannot do that right now.");
			return;
		}
		if(player.getInventory().getFreeSlots() == 0) {
			player.getPacketSender().sendMessage("You do not have any free inventory space left.");
			return;
		}
		player.setInteractingObject(oreObject);
		player.setPositionToFace(oreObject.getPosition());
		final Ores o = MiningData.forRock(oreObject.getId());
		final boolean giveGem = o != Ores.Rune_essence && o != Ores.Pure_essence;
		final int reqCycle = o == Ores.Runite ? 6 + Misc.getRandom(2) : Misc.getRandom(o.getTicks() - 1);
		if (o != null) {
			final int pickaxe = MiningData.getPickaxe(player);
			final int miningLevel = player.getSkillManager().getCurrentLevel(Skill.MINING);
			if (pickaxe > 0) {
				if (miningLevel >= o.getLevelReq()) {
					final MiningData.Pickaxe p = MiningData.forPick(pickaxe);
					if (miningLevel >= p.getReq()) {
						player.performAnimation(new Animation(p.getAnim()));
						final int delay = o.getTicks() - MiningData.getReducedTimer(player, p);
						player.setCurrentTask(new Task(delay >= 2 ? delay : 1, player, false) {
							int cycle = 0;
							@Override
							public void execute() {
								if(player.getInteractingObject() == null || player.getInteractingObject().getId() != oreObject.getId()) {
									player.getSkillManager().stopSkilling();
									player.performAnimation(new Animation(65535));
									stop();
									return;
								}
								if(player.getInventory().getFreeSlots() == 0) {
									player.performAnimation(new Animation(65535));
									stop();
									player.getPacketSender().sendMessage("You do not have any free inventory space left.");
									return;
								}
								if (cycle != reqCycle) {
									cycle++;
									player.performAnimation(new Animation(p.getAnim()));
								}
								if(giveGem) {
									boolean onyx = (o == Ores.Runite || o == Ores.CRASHED_STAR) && Misc.getRandom(o == Ores.CRASHED_STAR ? 20000 : 5000) == 1;
									if(onyx || Misc.getRandom(o == Ores.CRASHED_STAR ? 35 : 50) == 15) {
										int gemId = onyx ? 6571 : MiningData.RANDOM_GEMS[(int)(MiningData.RANDOM_GEMS.length * Math.random())];
										player.getInventory().add(gemId, 1);
										player.getPacketSender().sendMessage("You've found a gem!");
										if(gemId == 6571) {
											String s = o == Ores.Runite ? "Runite ore" : "Crashed star";
											World.sendMessage("[@red@Shooting Star</col>]: "+player.getUsername()+" has just received an Uncut Onyx from mining a "+s+"!");
										}
									}
								}
								int random = Misc.getRandom(100);
								if(random >= 1 && random <= 3) {
									Misc.giveReward(player, ITEMS);
								} 
								if (cycle == reqCycle) {
									if(o == Ores.Iron) {
										Achievements.finishAchievement(player, AchievementData.MINE_SOME_IRON);
									} else if(o == Ores.Runite) {
										Achievements.doProgress(player, AchievementData.MINE_25_RUNITE_ORES);
										Achievements.doProgress(player, AchievementData.MINE_2000_RUNITE_ORES);
									}
									random = Misc.getRandom(100);
									if(random >= 1 && random <= 10 && player.getEquipment().contains(INFERNO_ADZE)) {
										
										for(int[] data : SmithingData.SmeltData) {
											
											int barId = data[1];
											int oreId1 = data[1];
											int oreId2 = data[1];
											
											if(oreId1 == o.getItemId() && oreId2 == -1) {
												player.getInventory().add(barId, 1);
												if (player.getEquipment().contains(INFERNO_ADZE))
													player.getPacketSender().sendMessage("You mine some ore and your inferno adze smelts it into a bar.");
												else
													player.getPacketSender().sendMessage("You mine some ore and your infernal pickaxe smelts it into a bar.");
												break;
											}
											
										}
										
									} else {
										if(o.getItemId() != -1) {
											player.getInventory().add(o.getItemId(), 1);
										}
										random = Misc.getRandom(100);
										if(random >= 1 && random <= 10) {
											
											int extra = 1;
											
											for(int id : ITEMS) {
												if(player.getEquipment().contains(id)) {
													extra++;
												}
											}
											
											if(extra > player.getInventory().getFreeSlots()) {
												extra = player.getInventory().getFreeSlots();
											}
											
											player.getInventory().add(o.getItemId(), extra);
											
										}
									}
									if(player.getGameMode() == GameMode.EXTREME) {
										player.getSkillManager().addExperience(Skill.MINING, (int) ((o.getXpAmount() * 1.4)*0.25));
									} else {
										player.getSkillManager().addExperience(Skill.MINING, (int) (o.getXpAmount() * 1.4));
									}
									
									if(o == Ores.CRASHED_STAR) {
										player.getPacketSender().sendMessage("You mine the crashed star..");
									} else {
										player.getPacketSender().sendMessage("You mine some ore.");
									}
									Sounds.sendSound(player, Sound.MINE_ITEM);
									cycle = 0;
									this.stop();
									if(o.getRespawn() > 0) {
										player.performAnimation(new Animation(65535));
										oreRespawn(player, oreObject, o);
									} else {
										if(oreObject.getId() == 38660) {
											if(ShootingStar.CRASHED_STAR == null || ShootingStar.CRASHED_STAR.getStarObject().getPickAmount() >= ShootingStar.MAXIMUM_MINING_AMOUNT) {
												player.getPacketSender().sendClientRightClickRemoval();
												player.getSkillManager().stopSkilling();
												return;
											} else {
												ShootingStar.CRASHED_STAR.getStarObject().incrementPickAmount();
											}
										} else {
											player.performAnimation(new Animation(65535));
										}
										startMining(player, oreObject);
									}
								}
							}
						});
						TaskManager.submit(player.getCurrentTask());
					} else {
						player.getPacketSender().sendMessage("You need a Mining level of at least "+p.getReq()+" to use this pickaxe.");
					}
				} else {
					player.getPacketSender().sendMessage("You need a Mining level of at least "+o.getLevelReq()+" to mine this rock.");
				}
			} else {
				player.getPacketSender().sendMessage("You don't have a pickaxe to mine this rock with.");
			}
		}
	}

	public static void oreRespawn(final Player player, final GameObject oldOre, Ores o) {
		if(oldOre == null || oldOre.getPickAmount() >= 1)
			return;
		oldOre.setPickAmount(1);
		for(Player players : player.getLocalPlayers()) {
			if(players == null)
				continue;
			if(players.getInteractingObject() != null && players.getInteractingObject().getPosition().equals(player.getInteractingObject().getPosition().copy())) {
				players.getPacketSender().sendClientRightClickRemoval();
				players.getSkillManager().stopSkilling();
			}
		}
		player.getPacketSender().sendClientRightClickRemoval();
		player.getSkillManager().stopSkilling();
		CustomObjects.globalObjectRespawnTask(new GameObject(452, oldOre.getPosition().copy(), 10, 0), oldOre, respawnTime(o));
	}
	
	private static int respawnTime(Ores o) {
		if(o == Ores.Clay) {
			return 2;
		}
		double baseTime = o.getRespawn();
		int max = 2048;
		int playersOnline = World.getPlayers().size();
		if(playersOnline >= max - 100)
			return o.getRespawn();
		int factor =  8 - (playersOnline / 250 == 0 ? 1 : playersOnline / 250);
		double timeInS = (o.getRespawn() * 600)/ 1000;
		double secondFactor = Math.sqrt(baseTime);
		for(int i = 0; i < factor; i++) {
			timeInS += secondFactor;
		}
		return (int) Math.floor((timeInS * 1000) / 600) + 5;
	}
	
}
