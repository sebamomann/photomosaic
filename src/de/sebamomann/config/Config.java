package de.sebamomann.config;

import java.util.Properties;

/**
 * Configuration to load Variables from configuration (.cfg) file
 */
public class Config
{
	private Properties file;

	/**
	 * Default Constructor
	 */
	public Config()
	{
		file = new Properties();

		/*
		 * Load file as configuration to access variables
		 */
		try
		{
			file.load(this.getClass().getClassLoader().getResourceAsStream("config.cfg"));
		} catch (Exception e)
		{
			System.out.println("Could not load Configuration File!");
		}
	}

	/**
	 * Fetch variable from configuration file
	 * 
	 * @param key String Name of variable
	 * @return String Value specified in configuration file
	 */
	public String getProperty(String key)
	{
		String value = this.file.getProperty(key);
		return value;
	}
}
