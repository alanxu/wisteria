package com.thomsonreuters.trtn.wisteria.core.transport;

import com.thomsonreuters.trtn.wisteria.core.session.Session;

public interface Transport <I, O> {
	I read() throws TransportException;
	void write(O output ) throws TransportException;
	
	void open() throws TransportException;
	void close() throws TransportException;
	
	Session getSession();
	void setSession(Session session);
	
	void addTransportListener(TransportListener transportListener);
	
	boolean isConnected();
}
