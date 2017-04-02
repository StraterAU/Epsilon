package com.epsilon.world.content.skill.impl.prayer;

public enum BonesData {
	 BONES(526, 13),
	 BAT_BONES(530, 14),
	 WOLF_BONES(2859, 15),
	 BIG_BONES(532, 26),
	 FEMUR_BONES(15182, 35),
	 BABYDRAGON_BONES(534, 48),
	 JOGRE_BONE(3125, 51),
	 ZOGRE_BONES(4812, 53),
	 LONG_BONES(10976, 55),
	 CURVED_BONE(10977, 56),
	 SHAIKAHAN_BONES(3123, 58),
	 DRAGON_BONES(536, 87),
	 FAYRG_BONES(4830, 99),
	 RAURG_BONES(4832, 99),
	 DAGANNOTH_BONES(6729, 108),
	 OURG_BONES(14793, 109),
	 FROSTDRAGON_BONES(18830, 148);
	
	BonesData(int boneId, int buryXP) {
		this.boneId = boneId;
		this.buryXP = buryXP;
	}

	private int boneId;
	private int buryXP;
	
	public int getBoneID() {
		return this.boneId;
	}
	
	public int getBuryingXP() {
		return this.buryXP;
	}
	
	public static BonesData forId(int bone) {
		for(BonesData prayerData : BonesData.values()) {
			if(prayerData.getBoneID() == bone) {
				return prayerData;
			}
		}
		return null;
	}
	
}
