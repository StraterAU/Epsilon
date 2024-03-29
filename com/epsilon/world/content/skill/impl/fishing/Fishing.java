package com.epsilon.world.content.skill.impl.fishing;

import com.epsilon.engine.task.Task;
import com.epsilon.engine.task.TaskManager;
import com.epsilon.model.Animation;
import com.epsilon.model.Direction;
import com.epsilon.model.GameMode;
import com.epsilon.model.Skill;
import com.epsilon.model.definitions.ItemDefinition;
import com.epsilon.util.Misc;
import com.epsilon.world.content.Achievements;
import com.epsilon.world.content.Achievements.AchievementData;
import com.epsilon.world.entity.impl.player.Player;

public class Fishing {

	public enum Spot {
		
		LURE(318, new int[]{335, 331}, 309, 314, new int[]{20, 30}, true, new int[]{1550, 2120}, 623),
		
		CAGE(2030, new int[]{377}, 301, -1, new int[]{40}, false, new int[]{2980}, 619),
		
		BIGNET(2031, new int[]{353, 341, 363}, 305, -1, new int[]{16, 23, 46}, false, new int[]{780, 1130, 2120}, 620),
		
		BIGNET2(2029, new int[]{395, 389}, 305, -1, new int[]{79, 81}, false, new int[]{9000, 10122}, 620),
		
		SMALLNET(2028, new int[]{317, 321}, 303, -1, new int[]{1, 15}, false, new int[]{290, 550}, 621),
		
		MONK_FISH(2026, new int[]{7944}, 305, -1, new int[]{62}, false, new int[]{6985}, 621),
		
		HARPOON(2030, new int[]{359, 371}, 311, -1, new int[]{35, 50}, true, new int[]{2530, 5100}, 618),
		
		HARPOON2(2031, new int[]{383}, 311, -1, new int[]{76}, true, new int[]{8547}, 618),
		
		BAIT(2028, new int[]{327, 345}, 307, 313, new int[]{5, 10}, true, new int[]{650, 980}, 623),
		
		ROCKTAIL(10091, new int[]{15270}, 309, 15263, new int[]{91}, false, new int[]{18010}, 623);

		int npcId, equipment, bait, anim;
		int[] rawFish, fishingReqs, xp;
		boolean second;
		private Spot(int npcId, int[] rawFish, int equipment, int bait, int[] fishingReqs, boolean second, int[] xp, int anim) {
			this.npcId = npcId;
			this.rawFish = rawFish;
			this.equipment = equipment;
			this.bait = bait;
			this.fishingReqs = fishingReqs;
			this.second = second;
			this.xp = xp;
			this.anim = anim;
		}

		public int getNPCId() {
			return npcId;
		}

		public int[] getRawFish() {
			return rawFish;
		}

		public int getEquipment() {
			return equipment;
		}

		public int getBait() {
			return bait;
		}

		public int[] getLevelReq() {
			return fishingReqs;
		}

		public boolean getSecond() {
			return second;
		}

		public int[] getXp() {
			return xp;
		}

		public int getAnim() {
			return anim;
		}
	}

	public static Spot forSpot(int npcId, boolean secondClick) {
		for (Spot s : Spot.values()) {
			if (secondClick) {
				if (s.getSecond()) {
					if (s.getNPCId() == npcId) {
						if (s != null) {
							return s;
						}
					}
				}
			} else {
				if (s.getNPCId() == npcId && !s.getSecond()) {
					if (s != null) {
						return s;
					}
				}
			}
		}
		return null;
	}

	public static void setupFishing(Player p, Spot s) {
		if(s == null)
			return;
		if(p.getInventory().getFreeSlots() <= 0) {
			p.getPacketSender().sendMessage("You do not have any free inventory space.");
			p.getSkillManager().stopSkilling();
			return;
		}
		if (p.getSkillManager().getCurrentLevel(Skill.FISHING) >= s.getLevelReq()[0]) {
			if (p.getInventory().contains(s.getEquipment())) {
				if (s.getBait() != -1) {
					if (p.getInventory().contains(s.getBait())) {
						startFishing(p, s);
					} else {
						String baitName = ItemDefinition.forId(s.getBait()).getName();
						if(baitName.contains("Feather") || baitName.contains("Living Minerals"))
							baitName += "s";
						p.getPacketSender().sendMessage("You need some "+baitName+" to fish here.");
						p.performAnimation(new Animation(65535));
					}
				} else {
					startFishing(p, s);
				}
			} else {
				String def = ItemDefinition.forId(s.getEquipment()).getName().toLowerCase();
				p.getPacketSender().sendMessage("You need "+Misc.anOrA(def)+" "+def+" to fish here.");
			}
		} else {
			p.getPacketSender().sendMessage("You need a fishing level of at least "+s.getLevelReq()[0]+" to fish here.");
		}
	}
//OBject fishing spot facing
	public static void startFishing(final Player p, final Spot s) {
		p.getSkillManager().stopSkilling();
		final int fishIndex = Misc.getRandom(100) >= 70 ? getMax(p, s.fishingReqs) : (getMax(p, s.fishingReqs) != 0 ? getMax(p, s.fishingReqs) - 1 : 0);
		if(p.getInteractingObject() != null && p.getInteractingObject().getId() != 2028)
		p.setDirection(s == Spot.BIGNET2 ? Direction.NORTH : Direction.NORTH);
		if(p.getInteractingObject() != null && p.getInteractingObject().getId() != 2029)
		p.setDirection(s == Spot.BIGNET2 ? Direction.EAST : Direction.EAST);
		if(p.getInteractingObject() != null && p.getInteractingObject().getId() != 2030)
		p.setDirection(s == Spot.CAGE ? Direction.SOUTH : Direction.SOUTH);
		if(p.getInteractingObject() != null && p.getInteractingObject().getId() != 2026)
		p.setDirection(s == Spot.HARPOON2 ? Direction.EAST : Direction.EAST);
		if(p.getInteractingObject() != null && p.getInteractingObject().getId() != 2031)
		p.setDirection(s == Spot.MONK_FISH ? Direction.SOUTH : Direction.SOUTH);
		p.performAnimation(new Animation(s.getAnim()));
		p.setCurrentTask(new Task(2, p, false) {
			int cycle = 0, reqCycle = Fishing.getDelay(s.getLevelReq()[fishIndex]);
			@Override
			public void execute() {
				if(p.getInventory().getFreeSlots() == 0) {
					p.getPacketSender().sendMessage("You have run out of inventory space.");
					stop();
					return;
				}
				if(!p.getInventory().contains(s.getBait())) {
					stop();
					return;
				}
				cycle++;
				p.performAnimation(new Animation(s.getAnim()));
				if (cycle >= Misc.getRandom(1) + reqCycle) {
					String def = ItemDefinition.forId(s.getRawFish()[fishIndex]).getName();
					if(def.endsWith("s"))
						def = def.substring(0, def.length()-1);
					p.getPacketSender().sendMessage("You catch "+Misc.anOrA(def)+" "+def.toLowerCase().replace("_", " ")+".");
					if (s.getBait() != -1)
						p.getInventory().delete(s.getBait(), 1);
					p.getInventory().add(s.getRawFish()[fishIndex], 1);
					if(s.getRawFish()[fishIndex] == 331) {
						Achievements.finishAchievement(p, AchievementData.FISH_A_SALMON);
					} else if(s.getRawFish()[fishIndex] == 15270) {
						Achievements.doProgress(p, AchievementData.FISH_25_ROCKTAILS);
						Achievements.doProgress(p, AchievementData.FISH_2000_ROCKTAILS);
					}
					if(p.getGameMode() == GameMode.EXTREME) {
						p.getSkillManager().addExperience(Skill.FISHING, (int) (s.getXp()[fishIndex]*0.25));
					} else {
						p.getSkillManager().addExperience(Skill.FISHING, s.getXp()[fishIndex]);
					}
					
					setupFishing(p, s);
					setEventRunning(false);
				}
			}
			@Override
			public void stop() {
				setEventRunning(false);
				p.performAnimation(new Animation(65535));
			}
		});
		
		TaskManager.submit(p.getCurrentTask());
	}

	public static int getMax(Player p, int[] reqs) {
		int tempInt = -1;
		for (int i : reqs) {
			if (p.getSkillManager().getCurrentLevel(Skill.FISHING) >= i) {
				tempInt++;
			}
		}
		return tempInt;
	}

	private static int getDelay(int req) {
		int timer = 1;
		timer += (int) req * 0.08;
		return timer;
	}

}
