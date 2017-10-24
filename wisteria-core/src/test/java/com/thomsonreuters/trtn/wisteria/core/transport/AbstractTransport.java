package com.thomsonreuters.trtn.wisteria.core.transport;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thomsonreuters.trtn.wisteria.core.session.Session;

public abstract class AbstractTransport<I, O> implements Transport<I, O>{
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected Session session;
	protected final Set<TransportListener> listeners;
	
	public AbstractTransport(){
		this.listeners = new CopyOnWriteArraySet<TransportListener>();
	}
	
	@Override
	public void setSession(Session session){
		this.session = session;
	}

	@Override
	public Session getSession() {
		return this.session;
	}

	@Override
	public void addTransportListener(TransportListener transportListener) {
		this.listeners.add(transportListener);
	}

}
