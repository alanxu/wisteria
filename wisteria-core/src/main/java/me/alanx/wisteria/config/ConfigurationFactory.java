package me.alanx.wisteria.config;

public interface ConfigurationFactory {
	
	public final static String DEFAULT_CONFIG_NAME = "wisteria.properties";
	
	public Configuration getConfig();
	
	public static ConfigurationFactory loadInstance() {
		ConfigurationFactory configFactory = null;
		try {
			configFactory = (ConfigurationFactory)(ConfigurationFactory.class.getClassLoader().loadClass("me.alanx.wisteria.config.impl.ConfigurationFactoryImpl").newInstance());
		} catch (Exception e) {
			System.out.println("- Using default configuration solution. -");
			configFactory = new DefaultConfigurationFactory();
		}
		return configFactory;
	}
}
