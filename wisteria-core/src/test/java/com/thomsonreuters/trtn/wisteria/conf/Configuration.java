package com.thomsonreuters.trtn.wisteria.conf;

public interface Configuration {
	<T> T getProperty(Object key);
}
