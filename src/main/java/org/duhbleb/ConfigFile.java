package org.duhbleb;


import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class ConfigFile {
	static String filename = "myConfig.cfg";
	static PropertiesConfiguration config;
	
	public ConfigFile () throws ConfigurationException {
		ConfigFile.config = new PropertiesConfiguration(filename);
	}
	
	
	public static String getEmail() throws ConfigurationException {
		return config.getString("Email");
	}
	public static String getPassword() throws ConfigurationException {
		return config.getString("Password");
	
	}
}
