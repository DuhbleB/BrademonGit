package org.duhbleb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Random;

public class Brademon implements Serializable {

	private static final long serialVersionUID = 1L; // pfpfpfpfpfpfpfpffff....

	private static String serializationPath = "serialization/brademon/";
	
	public String name;
	public String ownerName;
	
	public int level;
	public float experience;
	
	public float health;
	public float maxHealth;
	
	public int strength;
	public int speed;
	public int attack;
	public int defense;
	public int wins;
	public int losses;
	
	public BrademonType type;
	
	
	public static void serialize(Brademon brademon){
		
		try {
			
			String pathFileIncluded = serializationPath + brademon.ownerName + ".bdm";
			File file = new File(serializationPath);
			
			if(!file.exists()){
				
				file.mkdirs();
			}
			
			FileOutputStream fileStream = new FileOutputStream(pathFileIncluded);
			ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
			
			objectStream.writeObject(brademon);
			
			objectStream.flush();
			objectStream.close();
			fileStream.flush();
			fileStream.close();
		}catch(IOException e){
		
			e.printStackTrace();
		}
	}
	
	public static Brademon deserialize(String ownerName){
		
		Brademon deserialized = null;
		
		try {
			
			String pathFileIncluded = serializationPath + ownerName + ".bdm";
			File file = new File(serializationPath);
			
			if(!file.exists())
				return null;
			
			FileInputStream fileStream = new FileInputStream(pathFileIncluded);
			ObjectInputStream objectStream = new ObjectInputStream(fileStream);
			
			deserialized = (Brademon) objectStream.readObject();
			
			objectStream.close();
			fileStream.close();
		}catch(IOException | ClassNotFoundException e){
			
			e.printStackTrace();
		}
		
		return deserialized;
	}
	
	public static Brademon generateNewBrademon(String name, String ownerName){
		
		Random randy = new Random();
		Brademon b = new Brademon();
		
		b.name = name;
		b.ownerName = ownerName;
		
		b.level = 1;
		
		b.health = 20.0f;
		b.maxHealth = 20.0f;
		b.strength = 1;
		b.speed = 1;
		b.attack = 1;
		b.defense = 1;
		
		b.wins = 0;
		b.losses = 0;
		
		BrademonType[] types = BrademonType.values();
		b.type = types[randy.nextInt(types.length)];
		
		serialize(b);
		
		return b;
	}
	
	public void printDescription(){
		
		String stats = "Name: " + name + '\n' + "Level: " + level + "\n" + "Health: " + health + "\n" + "Max Health: " + maxHealth + 
				"\n" + "Strength: " + strength + "\n" + "Speed: " + speed + "\n" + "Attack: " + attack +
				"\n" + "Defense: " + defense + "\n" + "Type: " + type.toString() + 
				"\n" + "Wins: " + wins + "\n" + "Losses: " + losses;
		
		Brain.singleton.sendMessage(Main.homeChannel, stats);
	}
	
	public void levelUp(){
		
		level ++;
		
		strength += type.strengthOnLevelUp;
		speed += type.speedOnLevelUp;
		attack += type.attackOnLevelUp;
		defense += type.defenseOnLevelUp;
	}
}
