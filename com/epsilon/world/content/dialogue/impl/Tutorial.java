package com.epsilon.world.content.dialogue.impl;

import com.epsilon.GameSettings;
import com.epsilon.engine.task.Task;
import com.epsilon.engine.task.TaskManager;
import com.epsilon.model.Direction;
import com.epsilon.model.GameMode;
import com.epsilon.model.Position;
import com.epsilon.net.security.ConnectionHandler;
import com.epsilon.world.content.dialogue.Dialogue;
import com.epsilon.world.content.dialogue.DialogueExpression;
import com.epsilon.world.content.dialogue.DialogueManager;
import com.epsilon.world.content.dialogue.DialogueType;
import com.epsilon.world.entity.impl.player.Player;

/**
 * Represents a Dungeoneering party invitation dialogue
 * 
 * @author Gabriel Hannason
 */

public class Tutorial {

	public static Dialogue get(Player p, int stage) {
		Dialogue dialogue = null;
		switch(stage) {
		case -1:
			break;
		case 0:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"Would you like a tour around the wonderful", "world of Epsilon?"};
				};

				@Override
				public int npcId() {
					return 6139;
				}

				@Override
				public Dialogue nextDialogue() {
					//return get(p, stage + 1);
					p.setDialogueActionId(132);
					return DialogueManager.getDialogues().get(132);
				}
			};
			break;
		case 1:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"There are plenty of ways to earn money in Epsilon.", "At the moment we're standing at the thieving stalls at home,", "here you can steal gold from stalls to make a fair earning."};
				};

				@Override
				public int npcId() {
					return 6139;
				}

				@Override
				public void specialAction() {
					p.moveTo(new Position(3096, 3508));
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 2:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"This area of our home contains the shops, these", "contain basic essentials that any player may find useful", "on his journey on Epsilon."};
				};

				@Override
				public int npcId() {
					return 6139;
				}

				@Override
				public void specialAction() {
					p.moveTo(new Position(3082, 3511));
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 3:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.CONFUSED;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"This is the bank of our home, here you can store", "your precious belongings or make trades with other", " players by accessing the Grand Exchange."};
				};

				@Override
				public int npcId() {
					return 6139;
				}

				@Override
				public void specialAction() {
					p.moveTo(new Position(3092, 3491));
					p.setDirection(Direction.EAST);
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 4:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"The next important thing you need to learn is navigating.", "All important teleports can be found at the top of the", "Spellbook. Take a look, I've opened it for you!"};
				};

				@Override
				public int npcId() {
					return 6139;
				}

				@Override
				public void specialAction() {
					p.moveTo(GameSettings.DEFAULT_POSITION.copy());
					p.setDirection(Direction.SOUTH);
					p.getPacketSender().sendTab(GameSettings.MAGIC_TAB);
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 5:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"If you wish to navigate to a skill's training location,", "simply press the on the respective skill in the skill tab."};
				};

				@Override
				public int npcId() {
					return 6139;
				}
				
				@Override
				public void specialAction() {
					p.getPacketSender().sendTab(GameSettings.SKILLS_TAB);
					finish(p);
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		/*case 6:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"Enough of the boring stuff, let's show you some creatures!", "There are a bunch of bosses to fight in "+GameSettings.SERVER_NAME+".", "Every boss drops unique and good gear when killed.", "One example is the mighty Pohenix!"};
				};

				@Override
				public int npcId() {
					return 6139;
				}

				@Override
				public void specialAction() {
					p.moveTo(new Position(2833, 9560));
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 7:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"Ah.. The Ghost Town..", "Here, you can find a bunch of revenants.", "You can also fight other players."};
				};

				@Override
				public int npcId() {
					return 6139;
				}

				@Override
				public void specialAction() {
					p.moveTo(new Position(3666, 3486));
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 8:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{GameSettings.SERVER_NAME+" also has a lot of enjoyable minigames.", "This is the Graveyard Arena, an area that's been run over", "by Zombies. Your job is to simply to kill them all.", "Sounds like fun, don't you think?"};
				};

				@Override
				public int npcId() {
					return 6139;
				}

				@Override
				public void specialAction() {
					p.moveTo(new Position(3503, 3569));
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 9:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"This is the member's zone.", "Players who have a Member rank can teleport here", "and take advantage of the resources that it has." };
				};

				@Override
				public int npcId() {
					return 6139;
				}

				@Override
				public void specialAction() {
					p.moveTo(new Position(3423, 2914));
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 10:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"To receive a member rank, you'd need to claim", "scrolls worth at least $20 (for the Bronze Member rank).", "Scrolls and other items can be purchased from the store", "which can be opened using the ::store command." };
				};

				@Override
				public int npcId() {
					return 6139;
				}

				@Override
				public void specialAction() {
					p.moveTo(new Position(3423, 2914));
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 11:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{GameSettings.SERVER_NAME+" is a competitive game. Next to you is a scoreboard", "which you can use to track other players and their progress."};
				};

				@Override
				public int npcId() {
					return 6139;
				}

				@Override
				public void specialAction() {
					p.moveTo(new Position(3086, 3510));
					p.setDirection(Direction.WEST);
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 12:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"That was almost all.", "I just want to remind you to vote for us on various", "gaming toplists. To do so, simply use the ::vote command.", "You will be rewarded!"};
				};

				@Override
				public int npcId() {
					return 6139;
				}

				@Override
				public void specialAction() {
					p.moveTo(GameSettings.DEFAULT_POSITION.copy());
					p.setDirection(Direction.SOUTH);
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 13:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"If you have any more questions, simply use the ::help", "command and a staff member should get back to you.", "You can also join the clanchat channel 'help' and ask", "other players for help there too. Have fun playing "+GameSettings.SERVER_NAME+"!"};
				};

				@Override
				public int npcId() {
					return 6139;
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 14:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"If you have any more questions, simply use the ::help", "command and a staff member should get back to you.", "You can also join the clanchat channel 'help' and ask", "other players for help there too. Have fun playing "+GameSettings.SERVER_NAME+"!"};
				};

				@Override
				public int npcId() {
					return 6139;
				}

				@Override
				public void specialAction() {
					p.setNewPlayer(false);
					if(ConnectionHandler.getStarters(p.getHostAddress()) <= GameSettings.MAX_STARTERS_PER_IP) {
						if(p.getGameMode() != GameMode.NORMAL) {
							p.getInventory().add(995, 25000).add(16693, 1).add(17241, 1).add(16671, 1).add(16385, 1).add(17351, 1).add(841, 1).add(882, 50).add(579, 1).add(577, 1).add(1011, 1).add(1379, 1).add(556, 50).add(558, 50).add(557, 50).add(555, 50).add(1712, 1).add(11118, 1).add(330, 200).add(16349, 1).add(16283, 1).add(1419, 1).add(6199, 2).add(19670, 1).add(9470, 1);
						} else {
							p.getInventory().add(995, 500000).add(9470, 1).add(17239, 1).add(16669, 1).add(17341, 1).add(16691, 1).add(16935, 1).add(16273, 1).add(16339, 1).add(1704, 1).add(4675, 1).add(3842, 1).add(565, 200).add(560, 400).add(555, 600).add(392, 100).add(5698, 1).add(2497, 1).add(4587, 1).add(6199, 2).add(19670, 1).add(1419, 1).add(9470, 1);
						}
						p.getPacketSender().sendMessage("<col=FF0066>You've been given a Scythe! It is untradeable and you will keep it on death.");
						
					p.getPacketSender().sendMessage("<col=FF0066>additionally, you've been given a Vote Scroll and 2 Mystery Boxes for joining!.");
						ConnectionHandler.addStarter(p.getHostAddress(), true);
						p.setReceivedStarter(true); //Incase they want to change game mode, regives starter items
					} else {
						p.getPacketSender().sendMessage("Your connection has received enough starting items.");
					}
					p.getPacketSender().sendInterface(3559);
					p.getAppearance().setCanChangeAppearance(true);
					p.setPlayerLocked(false);
					TaskManager.submit(new Task(20, p, false) {
						@Override
						protected void execute() {
							if(p != null && p.isRegistered()) {
								p.getPacketSender().sendMessage("<img=10> @blu@Want to go player killing? Mandrith now sells premade PvP sets.");
							}
							stop();
						}
					});
					p.save();
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;*/
		}
		return dialogue;
	}

	public static void finish(Player p) {
		p.setNewPlayer(false);
		if(ConnectionHandler.getStarters(p.getHostAddress()) <= GameSettings.MAX_STARTERS_PER_IP) {
			if(p.getGameMode() != GameMode.LEGEND && p.getGameMode() != GameMode.EXTREME) {
				p.getInventory().add(995, 25000).add(1153, 1).add(1115, 1).add(1067, 1).add(1323, 1).add(1191, 1).add(841, 1).add(882, 50).add(579, 1).add(577, 1).add(1011, 1).add(1379, 1).add(556, 50).add(558, 50).add(557, 50).add(555, 50).add(1712, 1).add(11118, 1).add(330, 200).add(16349, 1).add(16283, 1).add(1419, 1).add(6199, 2).add(19670, 1).add(9470, 1);
			} else {
				p.getInventory().add(995, 500000).add(1153, 1).add(1115, 1).add(1067, 1).add(1323, 1).add(1191, 1).add(1704, 1).add(4675, 1).add(3842, 1).add(565, 200).add(560, 400).add(555, 600).add(392, 100).add(5698, 1).add(2497, 1).add(4587, 1).add(6199, 2).add(19670, 1).add(1419, 1).add(9470, 1);
			}
			p.getPacketSender().sendMessage("You've been given a Scythe! It is untradeable and you will keep it on death.");
			
		p.getPacketSender().sendMessage("Ddditionally, you've been given a Vote Scroll and 2 Mystery Boxes for joining!.");
			ConnectionHandler.addStarter(p.getHostAddress(), true);
			p.setReceivedStarter(true); //Incase they want to change game mode, regives starter items
		} else {
			p.getPacketSender().sendMessage("Your connection has received enough starting items.");
		}
		p.getPacketSender().sendInterface(3559);
		p.getAppearance().setCanChangeAppearance(true);
		p.setPlayerLocked(false);
		TaskManager.submit(new Task(20, p, false) {
			@Override
			protected void execute() {
				if(p != null && p.isRegistered()) {
					p.getPacketSender().sendMessage("<img=10> @blu@Want to go player killing? Mandrith now sells premade PvP sets.");
				}
				stop();
			}
		});
		p.save();
	}
}