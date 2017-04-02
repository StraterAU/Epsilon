package com.epsilon.world.content;


import java.util.ArrayList;
import java.util.List;

import com.epsilon.util.Misc;
import com.epsilon.util.Stopwatch;
import com.epsilon.world.World;
import com.epsilon.world.entity.impl.player.Player;

public class Trivia {

    private static final int TIME = 350000;
	public static Questions LAST_QUESTION;
	private static Stopwatch timer = new Stopwatch().reset();
	private static List<Player> winners = new ArrayList<>(3);
	

	public Questions questions;
	public Questions getQuestions() {
		return questions;
	}
	
	public String getAnswer() {
		return questions.correctAnswer;
	}
	
	private static int[][][] rewards = new int[][][] {
		{ { 995, 300000 } }, //{ 14484, 3 } }, // first winner rewards
		{ { 995, 200000 } }, //{ 14484, 2 } }, // second winner rewards
		{ { 995, 100000 } }, //{ 14484, 1 } } // third winner rewards
	};
	
	/**
	 * LAST_QUESTION gets filled in by the random selection thing. Whenever this method is called, it will simply check if the answer parameter equals the answer parameter in the selected question
	 * is the player answer equal (ignoring upper/lower case) to the correct answer of the current question 
	 * 
	 * How would I go about adding a check if it's already been answered? 
	 * 
	 * @param player
	 * @param playerAnswer
	 * @return
	 */
	public static ResponseState answer(Player p, String playerAnswer) {
		
		if(winners.size() == 3 || winners.contains(p)) {
			return ResponseState.ALREADY_ANSWERED;
		}
		
		ResponseState resp = LAST_QUESTION.correctAnswer.equalsIgnoreCase(playerAnswer) ? ResponseState.CORRECT : ResponseState.INCORRECT;
		if(resp == ResponseState.CORRECT) {
			int[][] row = rewards[winners.size()];
			int[] reward = row[Misc.getRandom(row.length - 1)];
			p.getInventory().add(reward[0], reward[1]);
			//p.getInventory().add(995, 100000 * (winners.size() + 1));
			p.getPointsHandler().setTriviaPoints(winners.size() + 1, true);
			winners.add(p);
		}
		return resp; 
	}
	
	public enum ResponseState {
		CORRECT, INCORRECT, ALREADY_ANSWERED;
	}
	
	public enum Questions{
		
		QUESTION_1("<col=ae0000>[Runescape]</col>What's the sub-type of creature that Nex belongs to?", "Nihil"),
		QUESTION_2("<col=ae0000>[Runescape]</col>What Woodcutting level does it take to cut magic trees (number)?", "75"),
		QUESTION_3("<col=ae0000>[Runescape]</col>What Summoning level does it take to summon a steel titan (number)?","99"),
		QUESTION_4("<col=ae0000>[Runescape]</col>Which level does it take to create overloads?","96"),
		QUESTION_5("<col=ae0000>[Runescape]</col>What prayer level is turmoil? (number)","95"),
		QUESTION_6("<col=ae0000>[Runescape]</col>Where can you fight other players?","Wilderness"),
		QUESTION_7("<col=ae0000>[Runescape]</col>How many achievements are there?(number)","101"),
		QUESTION_8("<col=ae0000>[Runescape]</col>What's the best type of gloves after completing RFD?","Barrows"),
		QUESTION_9("<col=ae0000>[Runescape]</col>Which runescape minigame focus on strategy over combat?","Mobilising armies"),
        QUESTION_10("<col=ae0000>[Runescape]</col>How many chaotic weapons are there?(number)","5"),
       QUESTION_11("<col=ae0000>[Runescape]</col>How many God Wars gods are there(number)","5"),
       QUESTION_12("<col=ae0000>[Runescape]</col>What's the kill count required for Godwards?(number)","20"),
        QUESTION_13("<col=ae0000>[Runescape]</col>What mining level for a rune pickaxe?(number)","41"),
         QUESTION_14("<col=ae0000>[Runescape]</col>Whats the magic level for high alch?(number)","55"),
                  QUESTION_15("<col=ae0000>[Runescape]</col>What herblore level to farm torstols?(number)","85"),
                   QUESTION_16("<col=ae0000>[Runescape]</col>What cures plants in herblore?","plant cure"),
                QUESTION_20("<col=ae0000>[Runescape]</col>What is General Graardor the god of?","bandos"),
         QUESTION_17("<col=ae0000>[Runescape]</col>What defence level for DFS(number)","75"),
         QUESTION_18("<col=ae0000>[Runescape]</col>Dragon Defenders require defence and what skill?","attack"),
         QUESTION_19("<col=ae0000>[Runescape]</col>What level for Dragon armour?(number)","60"),
         QUESTION_21("<col=ae0000>[Runescape]</col>What's the RC requirement for nature runes?(number)","44"),
         QUESTION_22("<col=ae0000>[Runescape]</col>What level to fish sharks?(number)","76"),
         QUESTION_23("<col=ae0000>[Runescape]</col>What cooking level to cook sharks?(number)","80"),
         QUESTION_24("<col=ae0000>[Runescape]</col>What level to cut maples??(number)","45"),
         QUESTION_25("<col=ae0000>[Runescape]</col>Agility level for the Gnome Stronghold? (number)","1"),
         QUESTION_26("<col=ae0000>[Runescape]</col>Who's the sailor that makes rope for you at Draynor village?","Ned"),
		QUESTION_27("<col=ae0000>[Runescape]</col>What's the name of Osman's daughter?","Leela"),
		QUESTION_28("<col=ae0000>[Runescape]</col>What potency of spell must Chronozon be hit with to die?","Blast");
		private Questions(String question, String answer) {
			this.question = question;
			this.correctAnswer = answer;
		}
		public String question;
		public String correctAnswer;
		
		public String getAnswer() {
			return correctAnswer;
		}
		
	}
	
	
	public static Questions getRandom() {
		Questions question = Questions.values()[Misc.getRandom(Questions.values().length - 1)];
		return question;
	}
	
	public static void sequence() {
		if(timer.elapsed(TIME)) {
			Questions question = getRandom();
			timer.reset();
			if(question == LAST_QUESTION) {
				question = getRandom();
			}
			LAST_QUESTION = question;
			winners = new ArrayList<>(3);
			//	if (player.triviaEnabled == true) {
			World.sendMessage("<img=10> <col=0066FF><shad=222222>[Trivia Bot]"  + question.question);
			World.sendMessage("<img=10> <col=0066FF><shad=222222>[Trivia Bot] Use ::answer (your answer) to answer the trivia question!");
			//}
			}
		
	}

	public static List<Player> getWinners() {
		return winners;
	}

	public static void setWinners(List<Player> winners) {
		Trivia.winners = winners;
	}
}
