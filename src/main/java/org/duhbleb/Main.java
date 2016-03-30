package org.duhbleb;

import javax.security.auth.login.LoginException;

import org.apache.commons.configuration.ConfigurationException;

import net.dv8tion.jda.JDABuilder;

public class Main {

	public static ConfigFile config;
	public static String versionNumber;
	public static String email;
	public static String password;

	public static String homeChannel;

	public static char callsign;


	public static void main(String[] args) throws ConfigurationException{
		config = new ConfigFile();
		getConfigDefaults();
		
		Brain brain = new Brain();
		brain.interpreter = new InputInterpreter();

		try {

			brain.jda = new JDABuilder(email, password).addListener(brain.interpreter).buildAsync();
		}catch(LoginException | IllegalArgumentException e){

			e.printStackTrace();
		}

		brain.goLive();
	}


	private static void getConfigDefaults() throws ConfigurationException {
		versionNumber = ConfigFile.config.getString("Version");
		email = ConfigFile.getEmail();
		password = ConfigFile.getPassword();
		homeChannel = ConfigFile.config.getString("Home_Channel");
		callsign = ConfigFile.config.getString("Callsign").charAt(0);
	}
}
