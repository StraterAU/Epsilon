package com.epsilon.world.content.combat.strategy.impl;

import com.epsilon.world.content.combat.CombatContainer;
import com.epsilon.world.content.combat.CombatType;
import com.epsilon.world.content.combat.strategy.CombatStrategy;
import com.epsilon.world.entity.impl.Character;

public class Kraken implements CombatStrategy {

	@Override
	public boolean canAttack(Character entity, Character victim) {
		return false;
	}

	@Override
	public CombatContainer attack(Character entity, Character victim) {
		return null;
	}

	@Override
	public boolean customContainerAttack(Character entity, Character victim) {
		return false;
	}

	@Override
	public int attackDelay(Character entity) {
		return 0;
	}

	@Override
	public int attackDistance(Character entity) {
		return 0;
	}

	@Override
	public CombatType getCombatType() {
		return null;
	}
}
