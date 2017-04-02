package com.epsilon;

import java.math.BigInteger;

import com.epsilon.model.Item;
import com.epsilon.model.Position;
import com.epsilon.net.security.ConnectionHandler;
import com.epsilon.util.Misc;

public class GameSettings {
	
	public static final Item[] HALLOWEEN_ITEMS = new Item[] {
			new Item(1959, 1),
			new Item(10724, 1),
			new Item(10725, 1),
			new Item(10726, 1),
			new Item(10727, 1),
			new Item(10728, 1)
	};
	/**
	 * Change pw reward
	 */
	public static boolean CHANGE_PASSWORD_REWARD = false;
	
	public static boolean FORUM_INT = false;
	/**
	 * Iron man
	 */
	public static final int IRON_MAN_NPC_REWARD = 1;
	public static final int HARDCORE_IRON_MAN_NPC_REWARD = 5;
	/**
	 * The game port
	 */
	public static final int GAME_PORT = 43595;

	/**
	 * The game version
	 */
	public static final int GAME_VERSION = 13;
	public static final int GAME_UID = 23;

	/**
	 * The maximum amount of players that can be logged in on a single game
	 * sequence.
	 */
	public static final int LOGIN_THRESHOLD = 50;

	/**
	 * The maximum amount of players that can be logged in on a single game
	 * sequence.
	 */
	public static final int LOGOUT_THRESHOLD = 50;
	
	/**
	 * The maximum amount of players who can receive rewards on a single game
	 * sequence.
	 */
	public static final int VOTE_REWARDING_THRESHOLD = 15;

	/**
	 * The maximum amount of connections that can be active at a time, or in
	 * other words how many clients can be logged in at once per connection.
	 * (0 is counted too)
	 */
	public static final int CONNECTION_AMOUNT = 2147000000;

	/**
	 * The throttle interval for incoming connections accepted by the
	 * {@link ConnectionHandler}.
	 */
	public static final long CONNECTION_INTERVAL = 1000;

	/**
	 * The number of seconds before a connection becomes idle.
	 */
	public static final int IDLE_TIME = 15;
	
	/**
	 * The keys used for encryption on login
	 */
	public static final BigInteger RSA_MODULUS = new BigInteger("141038977654242498796653256463581947707085475448374831324884224283104317501838296020488428503639086635001378639378416098546218003298341019473053164624088381038791532123008519201622098961063764779454144079550558844578144888226959180389428577531353862575582264379889305154355721898818709924743716570464556076517");
	public static final BigInteger RSA_EXPONENT = new BigInteger("73062137286746919055592688968652930781933135350600813639315492232042839604916461691801305334369089083392538639347196645339946918717345585106278208324882123479616835538558685007295922636282107847991405620139317939255760783182439157718323265977678194963487269741116519721120044892805050386167677836394617891073");

	/**
	 * The maximum amount of messages that can be decoded in one sequence.
	 */
	public static final int DECODE_LIMIT = 30;
	
	/** GAME **/

	/**
	 * Processing the engine
	 */
	public static final int ENGINE_PROCESSING_CYCLE_RATE = 600;//200;
	public static final int GAME_PROCESSING_CYCLE_RATE = 600;

	/**
	 * Are the MYSQL services enabled?
	 */
	public static boolean MYSQL_ENABLED = false;

	/**
	 * Is it currently bonus xp?
	 */
	public static boolean BONUS_EXP = Misc.isWeekend();
	
	public static boolean DOUBLE_LOOT = Misc.isMonday();
	
	public static boolean DOUBLE_PC_POINTS = Misc.isTuesday();
	
	public static boolean SKILLING_DAY = Misc.isWednesday();
	
	public static boolean VOTING_DAY = Misc.isThursday();
	
	public static boolean DOUBLE_SLAYER_POINTS = Misc.isFriday();
	
	/**
	 * 
	 * The default position
	 */
	public static final Position DEFAULT_POSITION = new Position(3093, 3503);

	
	public static final int MAX_STARTERS_PER_IP = 2;
	
	public static final int[] KEPT_ON_DEATH = {
			15009, 15010, 15011, 15012, 15013, 15014, 15015, 15017, 15018,
			15019, 15020, 15220
	};
	
	/**
	 * Untradeable items
	 * Items which cannot be traded or staked
	 */
	public static final int[] UNTRADEABLE_ITEMS = 
		{13661, 13262, 1419,6500,
		6529, 6950, 1464, 2996, 2677, 2678, 2679, 2680, 2682, 
		2683, 2684, 2685, 2686, 2687, 2688, 2689, 2690, 
		6570, 12158, 12159, 12160, 12163, 12161, 12162,
		19143, 19149, 19146, 19157, 19162, 19152, 4155,
		8850, 10551, 8839, 8840, 8842, 11663, 11664, 19712,
		11665, 3842, 3844, 3840, 8844, 8845, 8846, 8847, 
		8848, 8849, 8850, 10551, 7462, 7461, 7460, 
		7459, 7458, 7457, 7456, 7455, 7454, 7453, 11665, 10499, 9748, 
		9754, 9751, 9769, 9757, 9760, 9763, 9802, 9808,
		9784, 9799, 9805, 9781, 9796, 9793, 9775, 9772,
		9778, 9787, 9811, 9766, 9749, 9755, 9752, 9770, 
		9758, 9761, 9764, 9803, 9809, 9785, 9800, 9806, 
		9782, 9797, 9794, 9776, 9773, 9779, 9788, 9812, 
		9767, 9747, 9753, 9750, 9768, 9756, 9759, 9762,
		9801, 9807, 9783, 9798, 9804, 9780, 9795, 9792, 
		9774, 9771, 9777, 9786, 9810, 9765, 9948, 9949,
		9950, 12169, 12170, 12171, 20671, 14641, 14642,
		6188, 10954, 10956, 10958,
		3057, 3058, 3059, 3060, 3061,
		7594, 7592, 7593, 7595, 7596,
		14076, 14077, 14081,
		10840, 10836, 6858, 6859, 10837, 10838, 10839,
		9925, 9924, 9923, 9922, 9921,
		4565, 20046, 20044, 20045,
		14595, 14603, 14602, 14605, 	11789,
		19708, 19706, 19707,
		4860, 4866, 4872, 4878, 4884, 4896, 4890, 4896, 4902,
		4932, 4938, 4944, 4950, 4908, 4914, 4920, 4926, 4956,
		4926, 4968, 4994, 4980, 4986, 4992, 4998,
		18778, 18779, 18780, 18781,
		13450, 13444, 13405, 15502, 
		10548, 10549, 10550, 10551, 10555, 10552, 10553, 2412, 2413, 2414,
		20747, 
		18365, 18373, 18371, 15246, 12964, 12971, 12978, 14017,
		757, 8851,
		13855, 13848, 13849, 13857, 13856, 13854, 13853, 13852, 13851, 13850, 5509, 13653, 14021, 14020, 19111, 14019, 14022, 14024,
		19785, 19786, 18782, 10501, 11949
		};

	/**
	 * Unsellable items
	 * Items which cannot be sold to shops
	 */
	public static int UNSELLABLE_ITEMS[] = new int[] {
		13263, 13281, 14019, 14022, 14024, 19785, 19786, 1419, 4084, 15403, 10887,
		6199, 15501, 15441, 15442, 15443, 15444, 14004, 14005, 14006, 14007, 11848, 11850, 11856, 11854, 11852, 11846, 15018,
		15019, 15020, 15220, 14000, 14001, 14002, 14003, 2577, 19335, 15332, 19336, 19337, 19338, 19339, 19340, 9813, 20084,
		8851,6529, 14641, 14642, 14017, 2996, 10941, 10939, 14938, 10933, 14936, 10940, 18782,
		14021, 14020, 13653, 5512, 5509, 5510, 10942, 10934, 10935, 10943, 10944, 7774, 7775, 7776, 10936,
		1038, 1040,	1042, 1044, 1046, 1048, //Phats
		1053, 1055, 1057, //Hween
		1050, //Santa
		19780, //Korasi's
		20671, //Brackish
		20135, 20139, 20143, 20147, 20151, 20155, 20159, 20163, 20167, //Nex armors
		6570, //Fire cape
		19143, 19146, 19149, //God bows
		8844, 8845, 8846, 8847, 8848, 8849, 8850, 13262, //Defenders
		20135, 20139, 20143, 20147, 20151, 20155, 20159, 20163, 20167, // Torva, pernix, virtus
		13746, 13748, 13750, 13752, 13738, 13740, 13742, 13744, //Spirit shields & Sigil
		11694, 11696, 11698, 11700, 11702, 11704, 11706, 11708, 11686, 11688, 11690, 11692, 11710, 11712, 11714, //Godswords, hilts, pieces
		15486, //sol
		11730, //ss
		11718, 11720, 11722, //armadyl
		11724, 11726, 11728, //bandos
		11286, 11283, //dfs & visage
		14472, 14474, 14476, 14479, //dragon pieces and plate
		14484, //dragon claws
		13887, 13888, 13893, 13895, 13899, 13901, 13905, 13907, 13911, 13913, 13917, 13919, 13923, 13925, 13929, 13931, //Vesta's																																														
		13884, 13886, 13890, 13892, 13896, 13898, 13902, 13904, 13908, 13910, 13914, 13916, 13920, 13922, 13926, 13928, //Statius's
		13870, 13872, 13873, 13875, 13876, 13878, 13879, 13880, 13881, 13882, 13883, 13944, 13946, 13947, 13949, 13950, 13952, 13953, 13954, 13955, 13956, 13957, //Morrigan's
		13858, 13860, 13861, 13863, 13864, 13866, 13867, 13869, 13932, 13934, 13935, 13937, 13938, 13940, 13941, 13943, //Zuriel's
		20147, 20149, 20151, 20153, 20155, 20157, //Pernix
		20159, 20161, 20163, 20165, 20167, 20169, //Virtus
		20135, 20137, 20139, 20141, 20143, 20145, //Torva
		11335, //D full helm
		6731, 6733, 6735, 19111,//warrior ring, seers ring, archer ring
		962, //Christmas Cracker
		21787, 21790, 21793, //Steadfast, glaiven, ragefire
		20674,//Something something..... pvp armor, statuettes
		13958,13961,13964,13967,13970,13973,13976,13979,13982,13985,13988,13908,13914,13926,13911,13917,13923,13929,13932,13935,13938,13941,13944,13947,13950,13953,13957,13845,13846,13847,13848,13849,13850,13851,13852,13853,13854,13855,13856,13857, //Le corrupted items
		11995, 6500, 19670,
		11996, 18782,
		11997, 19712,
		12001,
		12002,
		12003,
		12005,
		12006,
		11990,
		11991,
		11992,
		11993,
		11994,
		11989,
		11988,
		11987,
		11986,
		11985,
		11984,
		11983,
		11982,
		11981,
		11979,
		10501, 11949
	};
	public static final int 
	ATTACK_TAB = 0, 
	SKILLS_TAB = 1, 
	QUESTS_TAB = 2, 
	ACHIEVEMENT_TAB = 14,
	INVENTORY_TAB = 3, 
	EQUIPMENT_TAB = 4, 
	PRAYER_TAB = 5, 
	MAGIC_TAB = 6,

	SUMMONING_TAB = 13, 
	FRIEND_TAB = 8, 
	IGNORE_TAB = 9, 
	CLAN_CHAT_TAB = 7,
	LOGOUT = 10,
	OPTIONS_TAB = 11,
	EMOTES_TAB = 12;

	public static final String SERVER_NAME = "Epsilon";
}
