package org.duhbleb;

import java.io.Serializable;

public class BrademonMove implements Serializable {

	private static final long serialVersionUID = 1L; // pfpfpfpfpfpf...

	public String name;
	
	public int damageToHealth;
	public int damageToStrength;
	public int damageToSpeed;
	public int damageToAttack;
	public int damageToDefense;
	
	public transient int pointsToSpend;
	
	
	public BrademonMove(String name){
		
		this.name = name;
	}
}
