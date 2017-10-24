package me.alanx.wisteria.config;

import java.util.Properties;

public class Configuration {
	
	public static final Configuration INSTANCE = ConfigurationFactory.loadInstance().getConfig();
	
	public static final String KEY_SESSION_TIMEOUT_IN_MILLIS = "wisteria.session.timeout";
	
	

	private Properties properties;

	public Configuration(Properties properties) {
		super();
		this.properties = properties;
	}

	public int getInt(String key) {
		return Integer.valueOf(getString(key));
	}

	public long getLong(String key) {
		return Long.valueOf(getString(key));
	}

	public boolean getBoolean(String key) {
		return Boolean.valueOf(getString(key));
	}

	public String getString(String key) {
		return this.properties.getProperty(key);
	}

}
