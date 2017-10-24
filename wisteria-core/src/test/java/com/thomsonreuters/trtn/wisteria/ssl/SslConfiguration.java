package com.thomsonreuters.trtn.wisteria.ssl;

import java.net.InetSocketAddress;

public interface SslConfiguration {
	String keyStore();
	char[] keyStorePassphase();
	String keyManagerFactoryAlgorithm();
	
	String trustStore();
	char[] trustStorePassphase();
	String trustManagerFactoryAlgorithm();
	
	String[] enabledProtocols();
	String[] enabledCipherSuites();
	
	String sslContextProtocol();
	
	boolean useClientMode();
	boolean needClientAuth();
	boolean wantClientAuth();
	
	boolean isTrustAll();
}
