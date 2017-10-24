package com.thomsonreuters.trtn.wisteria.core.transport;

import com.thomsonreuters.trtn.wisteria.core.session.Session;

public abstract class AbstractTransport2<I, O> implements Transport2<I, O> {

	private Session session;	
	
/*	public AbstractTransport2(Session session) {
		super();
		this.session = session;
	}*/

	@Override
	public void receive(I input) {
		session.getFilterChain().fireMessageReceivedFromClient(input);
	}

	@Override
	public void send(O output) {
		session.getFilterChain().fireFilterWrite(output);
	}
	
	@Override
	public Session getSession() {
		return session;
	}
	
	@Override
	public void setSession(Session session) {
		this.session = session;
	}

}
