package org.duhbleb;

public enum BrademonType {

	air(1, 3, 1, 1), fire(1, 1, 2, 1), earth(1, 1, 1, 3), water(3, 1, 1, 1), autism(-1, 0, 1, 5);
	
	int strengthOnLevelUp;
	int speedOnLevelUp;
	int attackOnLevelUp;
	int defenseOnLevelUp;
	
	
	BrademonType(int strength, int speed, int attack, int defense){
		
		strengthOnLevelUp = strength;
		speedOnLevelUp = speed;
		attackOnLevelUp = attack;
		defenseOnLevelUp = defense;
	}
}
