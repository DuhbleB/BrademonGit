package org.duhbleb;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.JDABuilder;

public class Main {

	public static String versionNumber = "0.0.0.1a";
	public static String email = "-----";
	public static String password = "-----";
	
	public static String homeChannel = "brad-e-mon";
	
	public static char callsign = '$';
	
	
	public static void main(String[] args){
		
		Brain brain = new Brain();
		brain.interpreter = new InputInterpreter();
		
		try {
			
			brain.jda = new JDABuilder("dr.trill.phd@gmail.com", "theverybest").addListener(brain.interpreter).buildAsync();
		}catch(LoginException | IllegalArgumentException e){
			
			e.printStackTrace();
		}
		
		brain.goLive();
	}
}
