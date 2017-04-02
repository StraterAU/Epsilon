package com.epsilon.model;

/**
 * Represents a player's privilege rights.
 * 
 * @author relex lawl
 */

public enum PlayerInteractingOption {

	NONE,
	CHALLENGE,
	ATTACK,
	GAMBLE;

	public static PlayerInteractingOption forName(String name) {
		if(name.toLowerCase().contains("null"))
			return NONE;
		if(name.toLowerCase().contains("pelt"))
			return ATTACK;
		for(PlayerInteractingOption option : PlayerInteractingOption.values()) {
			if(option.toString().equalsIgnoreCase(name)) {
				return option;
			}
		}
		return null;
	}
}