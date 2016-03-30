package org.duhbleb;

import java.util.Random;

public class Battle {

	public String player1;
	public Brademon player1Brademon;
	public Brademon archivedPlayer1Brademon;
	
	public String player2;
	public Brademon player2Brademon;
	public Brademon archivedPlayer2Brademon;
	
	public boolean isPlayer1Turn;
	private int turnCounter;
	
	public boolean hasEnded;
	
	
	public Battle(String player1, String player2){
		
		this.player1 = player1;
		this.player2 = player2;
		
		isPlayer1Turn = true;
		turnCounter = 1;
		
		hasEnded = false;
		
		if(!loadAndInitializeBrademon()){
			
			hasEnded = true;
		}
	}
	
	private boolean loadAndInitializeBrademon(){
		
		player1Brademon = Brademon.deserialize(player1);
		archivedPlayer1Brademon = Brademon.deserialize(player1);
		player2Brademon = Brademon.deserialize(player2);
		archivedPlayer2Brademon = Brademon.deserialize(player2);
		
		if(player1Brademon == null || player2Brademon == null)
			return false;
		
		return true;
	}
	
	public void switchTurn(){
		
		isPlayer1Turn = !isPlayer1Turn;
		
		turnCounter ++;
	}
	
	public String getTurn(){
		
		if(isPlayer1Turn)
			return player1;
		else
			return player2;
	}
	
	public String sendCommand(String senderName, String command){
		
		String result = "error";
		
		
		if(command.toLowerCase().equals("run")){
			
			result = senderName + " ran like a little bitch!";
			endBattle();
			
			return result;
		}
			
		result = doMove(senderName, command);

		return result;
	}
	
	private String doMove(String senderName, String moveName){
		System.out.println(1);
		Random randy = new Random();
		
		String result = "";
		
		BrademonMove move = null;
		
		if(senderName.equals(player1))
			move = player1Brademon.getMove(moveName);
		
		if(senderName.equals(player2))
			move = player2Brademon.getMove(moveName);
		System.out.println(move);
		if(move == null){
			
			result += "That wasn't a move; you don't know anything!\n";
			return result;
		}
		
		boolean critical = false;
		boolean miss = false;
		boolean evade = false;
		
		int multiplier = 1;
		
		critical = (randy.nextInt(7) == 1) ? true : false;
		miss = (randy.nextInt(10) == 1) ? true : false;

		boolean targetSelf = (critical && miss);
		
		multiplier = (critical) ? 2 : 1;
		multiplier = (miss) ? 0 : multiplier;
		System.out.println(2);
		if(senderName.equals(player1)){
			
			float evadeChance = (float) (player2Brademon.speed - player1Brademon.speed) / (float) (player2Brademon.speed + player1Brademon.speed);
			
			if(evadeChance < 0)
				evadeChance = 0;
			
			evade = (randy.nextFloat() < evadeChance) ? true : false;
			
			if(evade)
				multiplier = 0;
			
			float damageToHealth = (float) (((float) move.damageToHealth / (float) player2Brademon.defense) * multiplier);
			float damageToStrength = (float) (((float) move.damageToStrength / (float) player2Brademon.strength) * multiplier);
			float damageToSpeed = (float) (((float) move.damageToSpeed / (float) player2Brademon.strength) * multiplier);
			float damageToAttack = (float) (((float) move.damageToAttack / (float) player2Brademon.strength) * multiplier);
			float damageToDefense = (float) (((float) move.damageToDefense / (float) player2Brademon.strength) * multiplier);
			
			if(critical)
				result += "Critical!\n";
			
			if(miss)
				result += "Miss!\n";
			
			if(evade)
				result += "Evade!\n";
			//TODO: Make better
			
			if(!targetSelf){
				
				player2Brademon.health -= damageToHealth;
				player2Brademon.strength -= damageToStrength;
				player2Brademon.speed -= damageToSpeed;
				player2Brademon.attack -= damageToAttack;
				player2Brademon.defense -= damageToDefense;
				
				if(player2Brademon.health < 1)
					player2Brademon.health = 1;
				
				if(player2Brademon.strength < 1)
					player2Brademon.strength = 1;
				
				if(player2Brademon.speed < 1)
					player2Brademon.speed = 1;
				
				if(player2Brademon.attack < 1)
					player2Brademon.attack = 1;
				
				if(player2Brademon.defense < 1)
					player2Brademon.defense = 1;
				
				String compressedResults = "(h: " + damageToHealth + ", str: " + damageToStrength + ", sp: " + damageToSpeed + ", att: " + damageToAttack + ", def: " + damageToDefense + ")";
				
				result += player1Brademon.name + " attacked " + player2Brademon.name + " for " + compressedResults + "point(s)!\n";
			}else {
			
				if(player1Brademon.type != BrademonType.autism){
					
					player1Brademon.health -= 1;
					result += player1Brademon.name + " failed so incredibly hard that it hit itself for one point!\n";
				}else{

					player1Brademon.health += 1;
					result += player1Brademon.name + " failed so incredibly hard, but is autistic! The autism fuels its autism!\n";
					result += player1Brademon.name + " healed itself for one point!\n";
				}
			}
		}
		System.out.println(3);
		if(senderName.equals(player2)){
			
			float evadeChance = (float) (player1Brademon.speed - player2Brademon.speed) / (float) (player1Brademon.speed + player2Brademon.speed);
			
			if(evadeChance < 0)
				evadeChance = 0;
			
			evade = (randy.nextFloat() < evadeChance) ? true : false;
			
			if(evade)
				multiplier = 0;
			
			float damageToHealth = (float) (((float) move.damageToHealth / (float) player1Brademon.defense) * multiplier);
			float damageToStrength = (float) (((float) move.damageToStrength / (float) player1Brademon.strength) * multiplier);
			float damageToSpeed = (float) (((float) move.damageToSpeed / (float) player1Brademon.strength) * multiplier);
			float damageToAttack = (float) (((float) move.damageToAttack / (float) player1Brademon.strength) * multiplier);
			float damageToDefense = (float) (((float) move.damageToDefense / (float) player1Brademon.strength) * multiplier);
			
			if(critical)
				result += "Critical!\n";
			
			if(miss)
				result += "Miss!\n";
			
			if(evade)
				result += "Evade!\n";
			//TODO: Make better
			
			if(!targetSelf){
				
				player1Brademon.health -= damageToHealth;
				player1Brademon.strength -= damageToStrength;
				player1Brademon.speed -= damageToSpeed;
				player1Brademon.attack -= damageToAttack;
				player1Brademon.defense -= damageToDefense;
				
				if(player1Brademon.strength < 1)
					player1Brademon.strength = 1;
				
				if(player1Brademon.speed < 1)
					player1Brademon.speed = 1;
				
				if(player1Brademon.attack < 1)
					player1Brademon.attack = 1;
				
				if(player1Brademon.defense < 1)
					player1Brademon.defense = 1;
				
				String compressedResults = "(h: " + damageToHealth + ", str: " + damageToStrength + ", sp: " + damageToSpeed + ", att: " + damageToAttack + ", def: " + damageToDefense + ")";
				
				result += player2Brademon.name + " attacked " + player1Brademon.name + " for " + compressedResults + "point(s)!\n";
			}else {
			
				if(player2Brademon.type != BrademonType.autism){
					
					player2Brademon.health -= 1;
					result += player2Brademon.name + " failed so incredibly hard that it hit itself for one point!\n";
				}else{

					player2Brademon.health += 1;
					result += player2Brademon.name + " failed so incredibly hard, but is autistic! The autism fuels its autism!\n";
					result += player2Brademon.name + " healed itself for one point!\n";
				}
			}
		}
		
		String compressedResultsPlayer1 = "(" + player1Brademon.health + ", " + player1Brademon.strength + ", " + player1Brademon.speed + ", " + player1Brademon.attack + ", " + player1Brademon.defense + ")";
		String compressedResultsPlayer2 = "(" + player2Brademon.health + ", " + player2Brademon.strength + ", " + player2Brademon.speed + ", " + player2Brademon.attack + ", " + player2Brademon.defense + ")";
		
		result += "\n";
		result += player1Brademon.name + "'s stats are (health, strength, speed, attack, defense): " + compressedResultsPlayer1 + "\n";
		result += player2Brademon.name + "'s stats are (health, strength, speed, attack, defense): " + compressedResultsPlayer2 + "\n";
		result += "\n";
		
		if(player1Brademon.health <= 0){
			
			result += player1Brademon.name + " got rekt!\n";
			result += player2 + " is the victor!\n";
			
			player2Brademon.wins ++;
			player1Brademon.losses ++;
			
			result += endBattle();
		}
		
		if(player2Brademon.health <= 0){
			
			result += player2Brademon.name + " got rekt!\n";
			result += player1 + " is the victor!\n";
			
			player1Brademon.wins ++;
			player2Brademon.losses ++;
			
			result += endBattle();
		}

		return result;
	}
	
	private String endBattle(){
		
		String result = "";
		
		float player1LevelExperienceModifier = Math.abs((player2Brademon.level - player1Brademon.level) / (player1Brademon.level));
		float player2LevelExperienceModifier = Math.abs((player1Brademon.level - player2Brademon.level) / (player2Brademon.level));
		
		if(player1LevelExperienceModifier == 0)
			player1LevelExperienceModifier = 1f;
		
		if(player2LevelExperienceModifier == 0)
			player2LevelExperienceModifier = 1f;
		
		float player1Experience = (((float) player1Brademon.health / (float) player1Brademon.maxHealth)) + (0.5f / (float) player1Brademon.level) * player1LevelExperienceModifier;
		float player2Experience = (((float) player2Brademon.health / (float) player2Brademon.maxHealth)) + (0.5f / (float) player2Brademon.level) * player2LevelExperienceModifier;
		
		player1Brademon.experience += player1Experience;
		player2Brademon.experience += player2Experience;
		
		result += player1Brademon.name + " gained " + player1Experience + " experience.\n";
		result += player2Brademon.name + " gained " + player2Experience + " experience.\n";
		
		while(player1Brademon.experience >= 1){
			
			float remainder = player1Brademon.experience - 1;
			
			archivedPlayer1Brademon.levelUp();
			player1Brademon.experience = remainder;
			
			result += player1Brademon.name + " leveled up!" + "\n";
		}
		
		while(player2Brademon.experience >= 1){
			
			float remainder = player2Brademon.experience - 1;
			
			archivedPlayer2Brademon.levelUp();
			player2Brademon.experience = remainder;
			
			result += player2Brademon.name + " leveled up!" + "\n";
		}
		
		player1Brademon.health = player1Brademon.maxHealth;
		player2Brademon.health = player2Brademon.maxHealth;
		
		player1Brademon.level = archivedPlayer1Brademon.level;
		player1Brademon.strength = archivedPlayer1Brademon.strength;
		player1Brademon.speed = archivedPlayer1Brademon.speed;
		player1Brademon.attack = archivedPlayer1Brademon.attack;
		player1Brademon.defense = archivedPlayer1Brademon.defense;
		
		player2Brademon.level = archivedPlayer1Brademon.level;
		player2Brademon.strength = archivedPlayer1Brademon.strength;
		player2Brademon.speed = archivedPlayer1Brademon.speed;
		player2Brademon.attack = archivedPlayer1Brademon.attack;
		player2Brademon.defense = archivedPlayer1Brademon.defense;
		
		Brademon.serialize(player1Brademon);
		Brademon.serialize(player2Brademon);
		
		hasEnded = true;
		
		return result;
	}
	
	public String getPossibleMoves(){
		
		String result = "";
		
		if(isPlayer1Turn){
			
			result += player1Brademon.listMoves();
		}else {
			
			result += player2Brademon.listMoves();
		}
		
		return result;
	}
}
