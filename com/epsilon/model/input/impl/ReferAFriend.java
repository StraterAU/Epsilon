package com.epsilon.model.input.impl;

import com.epsilon.model.input.Input;
import com.epsilon.util.Misc;
import com.epsilon.util.NameUtils;
import com.epsilon.world.World;
import com.epsilon.world.content.ReferHandler;
import com.epsilon.world.entity.impl.player.Player;

public class ReferAFriend extends Input{
	@Override
	public void handleSyntax(Player player, String syntax) {
		player.getPacketSender().sendInterfaceRemoval();
		
		if(syntax == null || syntax.length() <= 0 || syntax.length() > 20 || !NameUtils.isValidName(syntax)) {
			player.getPacketSender().sendMessage("Please, enter a valid name.");
			return;
		}
	if(player.isReferedAFriend() == true || ReferHandler.contains(player.getSerialNumber())){
			player.getPacketSender().sendMessage("You already have refered someone.");
			return;
		}
		syntax = Misc.formatText(syntax.replaceAll(" ", "_"));
		Player refered = World.getPlayerByName(syntax);

		if(refered != null){
				if(refered.getSerialNumber() == player.getSerialNumber()){
				player.getPacketSender().sendMessage("You can not refer yourself.");
				return;
			}else{
				
			player.getPacketSender().sendMessage("Refered "+refered.getUsername());
			player.setReferedAFriend(true);
			refered.getPointsHandler().setRefeerFriendPoints(1, true);
			refered.getPacketSender().sendMessage(""+player.getUsername()+" just refered you.You now have "+refered.getPointsHandler().getRefeerFriendPoints()+" references.");
			ReferHandler.add(player.getSerialNumber());
			}
		}else{
			player.getPacketSender().sendMessage("This player must be online to complete this action.");
		}
	}

}
