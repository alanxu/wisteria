package com.thomsonreuters.trtn.wisteria.core.plugin;

import java.util.Properties;

public interface ServiceRegistration {
	//<S, T extends S> void register(Class<S> serviceInterface, T service); 
	<S> void register(S service, Properties paremeters);
	<S> S[] lookup(Class<S> serviceInterface, String query);
}
