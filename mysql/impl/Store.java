package mysql.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import com.epsilon.GameServer;
import com.epsilon.world.entity.impl.player.Player;

public class Store {

	private static final String SECRET = "Tryhard12345"; //YOUR SECRET KEY!
	
	@SuppressWarnings("deprecation")
	public void claim(Player player){
		if(player.getInventory().getFreeSlots() < 10) {
			player.getPacketSender().sendMessage("You need at least 10 free inventory slots to claim purchased items.");
			return;
		}
		if(player.getSqlTimer().elapsed() <= 30000) {
			player.getPacketSender().sendMessage("You can only do this once every 30 seconds.");
			return;
		}
		GameServer.getLoader().getEngine().submit(() -> {
			try{
				player.getSqlTimer().reset();
				URL url = new URL("http://runeunity.org/store/callback.php?secret="+SECRET+"&username="+URLEncoder.encode(player.getUsername().toLowerCase().replaceAll(" ", "_")));
				BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
				String string = null;
				loop: while((string = reader.readLine()) != null) {
					switch(string.toUpperCase()) {
					case "ERROR":
						player.getPacketSender().sendMessage("An error occured while claiming your items.");
						break loop;
					case "NO_RESULTS":
						player.getPacketSender().sendMessage("There were no results found while claiming items.");
						break loop;
					default:
						String[] split = string.split(",");
						if(split.length != 2) {
							return;
						}
						int quantity = Integer.parseInt(split[1]);
						switch(split[0]) {
						/** MEMBER SCROLLS **/
						case "$10_scroll":
							player.getInventory().add(10942, quantity);
							break;
						case "$20_scroll":
							player.getInventory().add(10934, quantity);
							break;
						case "$50_scroll":
							player.getInventory().add(10935, quantity);
							break;
						case "$100_scroll":
							player.getInventory().add(10943, quantity);
							break;
	
							/** TORVA **/
						case "torva_armour_set":
							player.getInventory().add(14008, quantity).add(14009, quantity).add(14010, quantity);
							break;
	
							/** PERNIX **/
						case "pernix_armour_set":
							player.getInventory().add(14011, quantity).add(14012, quantity).add(14013, quantity);
							break;
	
							/** VIRTUS **/
						case "virtus_armour_set":
							player.getInventory().add(14014, quantity).add(14015, quantity).add(14016, quantity);
							break;
	
							/** SPIRIT SHIELDS **/
						case "divine_spirit_shield":
							player.getInventory().add(13740, quantity);
							break;
						case "elysian_spirit_shield":
							player.getInventory().add(13742, quantity);
							break;
						case "arcane_spirit_shield":
							player.getInventory().add(13738, quantity);
							break;
						case "spectral_spirit_shield":
							player.getInventory().add(13744, quantity);
							break;
	
	
							/** CHAOTIC KITE **/
						case "chaotic_kiteshield":
							player.getInventory().add(18359, quantity);
							break;
	
							/** PVP ARMOUR **/
						case "statius_armour_set":
							player.getInventory().add(13896, quantity).add(13884, quantity).add(13890, quantity).add(13902, quantity);
							break;
						case "vestas_armour_set":
							player.getInventory().add(13899, quantity).add(13887, quantity).add(13893, quantity);
							break;
						case "morrigans_armour_set":
							player.getInventory().add(13876, quantity).add(13870, quantity).add(13873, quantity).add(13883, 50 * quantity);
							break;
						case "zuriels_armour_set":
							player.getInventory().add(13864, quantity).add(13858, quantity).add(13861, quantity).add(13867, quantity);
							break;
							/** BANDOS **/
						case "bandos_armour_set":
							player.getInventory().add(11724, quantity).add(11726, quantity).add(11728, quantity);
							break;
	
							/** ARMADYL **/
						case "armadyl_armour_set":
							player.getInventory().add(11718, quantity).add(11720, quantity).add(11722, quantity);
							break;
	
						case "amulet_of_fury":
							player.getInventory().add(6585, quantity);
							break;
						case "berserker_ring_i":
							player.getInventory().add(15220, quantity);
							break;
						case "archer_ring_i":
							player.getInventory().add(15019, quantity);
							break;
						case "seer_ring_i":
							player.getInventory().add(15018, quantity);
							break;
							/** KATANA **/
						case "katana":
							player.getInventory().add(14018, quantity);
							break;
	
						case "korasis_sword":
							player.getInventory().add(19780, quantity);
							break;
	
							/** D CLAWS **/
						case "dragon_claws":
							player.getInventory().add(14484, quantity);
							break;
	
							/** godswords **/
						case "armadyl_godsword":
							player.getInventory().add(11694, quantity);
							break;
						case "saradomin_godsword":
							player.getInventory().add(11698, quantity);
							break;
						case "bandos_godsword":
							player.getInventory().add(11696, quantity);
							break;
						case "zamorak_godsword":
							player.getInventory().add(11700, quantity);
							break;
	
						case "vestas_longsword":
							player.getInventory().add(13899, quantity);
							break;
						case "statius_warhammer":
							player.getInventory().add(13902, quantity);
							break;
						case "hand_cannon":
							player.getInventory().add(15241, quantity);
							break;
						case "dark_bow":
							player.getInventory().add(11235, quantity);
							break;
						case "abyssal_whip":
							player.getInventory().add(4151, quantity);
							break;
							
						case "Black_Dragonhide":
							player.getInventory().add(1748, 500);
							break;
						case "dragon_bone":
							player.getInventory().add(537, 500);
							break;
						case "frost_dragon_bone":
							player.getInventory().add(18831, 100);
							break;
						case "rocktail":
							player.getInventory().add(15273, 500);
							break;
							
						case "ring_of_wealth":
							player.getInventory().add(2572, quantity);
							break;
							/** CHAOTICS **/
	
							/** CHAOTIC RAPIER **/
						case "chaotic_rapier":
							player.getInventory().add(18349, quantity);
							break;
	
							/** CHAOTIC LONGSWORD **/
						case "chaotic_longsword":
							player.getInventory().add(18351, quantity);
							break;
	
							/** CHAOTIC MAUL **/
						case "chaotic_maul":
							player.getInventory().add(18353, quantity);
							break;
	
							/** CHAOTIC CROSSBOW **/
						case "chaotic_crossbow":
							player.getInventory().add(18357, quantity);
							break;
	
							/** CHAOTIC STAFF **/
						case "chaotic_staff":
							player.getInventory().add(18355, quantity);
							break;
	
							/** CHRISTMAS CRACKER **/
						case "christmas_cracker":
							player.getInventory().add(962, quantity);
							break;
	
							/** BLUE PHAT **/
						case "blue_partyhat":
							player.getInventory().add(1042, quantity);
							break;
	
							/** WHITE PHAT **/
						case "white_partyhat":
							player.getInventory().add(1048, quantity);
							break;
	
							/** RED PHAT **/
						case "red_partyhat":
							player.getInventory().add(1038, quantity);
							break;
	
							/** GREEN PARTYHAT **/
						case "green_partyhat":
							player.getInventory().add(1044, quantity);
							break;
	
							/** YELLOW PARTYHAT **/
						case "yellow_partyhat":
							player.getInventory().add(1040, quantity);
							break;
	
							/** PURPLE PARTYHAT **/
						case "purple_partyhat":
							player.getInventory().add(1046, quantity);
							break;
	
							/** SANTA HAT **/
						case "santa_hat":
							player.getInventory().add(1050, quantity);
							break;
	
							/** RED HWEEN MASK **/
						case "red_halloween_mask":
							player.getInventory().add(1057, quantity);
							break;
	
							/** BLUE HWEEN MASK **/
						case "blue_halloween_mask":
							player.getInventory().add(1055, quantity);
							break;
	
							/** GREEN HWEEN MASK **/
						case "green_halloween_mask":
							player.getInventory().add(1053, quantity);
							break;
	
							/** 3RD AGE MELEE SET **/
						case "third-age_melee_set":
							player.getInventory().add(11858, quantity);
							break;
	
							/** 3RD AGE MAGE SET **/
						case "third-age_mage_set":
							player.getInventory().add(11862, quantity);
							break;
	
							/** 3RD AGE RANGE SET **/
						case "third-age_range_set":
							player.getInventory().add(11860, quantity);
							break;
	
							/** 3RD AGE DRUIDIC SET **/
						case "third-age_druidic_set":
							player.getInventory().add(19308, quantity).add(19317, quantity).add(19320, quantity).add(19314, quantity);
							break;
	
							/** PARTYHAT SET **/
						case "partyhat_set":
							for(int phat : new int[]{1048, 1038, 1044, 1040, 1046, 1042}) {
								player.getInventory().add(phat, quantity);
							}
							break;
	
							/** THIRD AGE SET **/
						case "third-age_set":
							int[] thirdAgeSet = new int[]{11858, 11860, 11862, 19308, 19317, 19320, 19341};
							for(int tA : thirdAgeSet)
								player.getInventory().add(tA, quantity);
							break;
	
							/** SPIRIT SHIELD SET **/
						case "spirit_shield_set":
							int[] shields = new int[]{13740, 13742, 13738, 13744};
							for(int s : shields)
								player.getInventory().add(s, quantity);
							break;
	
							/** NEX ARMOURs SET **/
						case "nex_armour_sets":
							int[] nexSets = new int[]{14008, 14009, 14010, 14011, 14012, 14013, 14014, 14015, 14016};
							for(int nexSet : nexSets)
								player.getInventory().add(nexSet, quantity);
							break;
	
						}
						continue loop;
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
				player.getPacketSender().sendMessage("Currently not available.");
			}
		});
	}					
}
