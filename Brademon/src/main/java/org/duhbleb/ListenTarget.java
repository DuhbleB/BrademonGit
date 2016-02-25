package org.duhbleb;

public enum ListenTarget {

	idle("Not listening"),
	channelChange("Listening for name of channel"),
	say("Listening for message"),
	sayContinuous("Listening for message"),
	brademonGeneration("Listening for the name"),
	brademonBattleOpponentSelect("Listening for the opponent's name"),
	brademonBattleNextMove("Listening for next move!");
	
	String description;
	
	
	ListenTarget(String description){
		
		this.description = description;
	}
}
