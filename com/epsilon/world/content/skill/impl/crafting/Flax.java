package com.epsilon.world.content.skill.impl.crafting;

import com.epsilon.engine.task.Task;
import com.epsilon.engine.task.TaskManager;
import com.epsilon.model.Animation;
import com.epsilon.model.GameMode;
import com.epsilon.model.Skill;
import com.epsilon.model.input.impl.EnterAmountToSpin;
import com.epsilon.world.entity.impl.player.Player;

public class Flax {
	
	private static final int FLAX_ID = 1779;
	
	public static void showSpinInterface(Player player) {
		player.getPacketSender().sendInterfaceRemoval();
		player.getSkillManager().stopSkilling();
		if(!player.getInventory().contains(1779)) {
			player.getPacketSender().sendMessage("You do not have any Flax to spin.");
			return;
		}
		player.setInputHandling(new EnterAmountToSpin());
		player.getPacketSender().sendString(2799, "Flax").sendInterfaceModel(1746, FLAX_ID, 150).sendChatboxInterface(4429);
		player.getPacketSender().sendString(2800, "How many would you like to make?");
	}
	
	public static void spinFlax(final Player player, final int amount) {
		if(amount <= 0)
			return;
		player.getSkillManager().stopSkilling();
		player.getPacketSender().sendInterfaceRemoval();
		player.setCurrentTask(new Task(2, player, true) {
			int amountSpan = 0;
			@Override
			public void execute() {
				if(!player.getInventory().contains(FLAX_ID)) {
					stop();
					return;
				}
				if(player.getGameMode() == GameMode.EXTREME) {
					player.getSkillManager().addExperience(Skill.CRAFTING, (int) (324*0.25));
				} else {
					player.getSkillManager().addExperience(Skill.CRAFTING, 324);
				}
				
				player.performAnimation(new Animation(896));
				player.getInventory().delete(FLAX_ID, 1);
				player.getInventory().add(1777, 1);
				amountSpan++;
				if(amountSpan >= amount)
					stop();
			}
		});
		TaskManager.submit(player.getCurrentTask());
	}
}