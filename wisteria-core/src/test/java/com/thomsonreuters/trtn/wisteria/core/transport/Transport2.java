package com.thomsonreuters.trtn.wisteria.core.transport;

import com.thomsonreuters.trtn.wisteria.core.session.Session;

public interface Transport2<I, O> {
	void receive(I input) throws TransportException;
	void send(O output) throws TransportException;
	void sendDirectly(O output) throws TransportException;
	Session getSession();
	void setSession(Session session);
}
