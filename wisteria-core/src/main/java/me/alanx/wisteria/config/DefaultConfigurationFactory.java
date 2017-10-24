package me.alanx.wisteria.config;

import java.io.IOException;
import java.util.Properties;

public class DefaultConfigurationFactory implements ConfigurationFactory {

	private static final Properties PROPS = new Properties();
	private static Configuration CONFIG = null;
	
	{
		try {
			PROPS.load(DefaultConfigurationFactory.class.getClassLoader().getResourceAsStream(DEFAULT_CONFIG_NAME));
			CONFIG = new Configuration(PROPS);
		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		}
	}
	
	 
	
	@Override
	public Configuration getConfig() {
		return CONFIG;
	}

}
