package me.alanx.wisteria.core;

import me.alanx.wisteria.core.protocol.Packet;
import me.alanx.wisteria.core.reactor.Processor;
import me.alanx.wisteria.core.reactor.Subscriber;
import me.alanx.wisteria.core.reactor.Subscription;
import me.alanx.wisteria.core.session.Session;
import me.alanx.wisteria.core.session.SessionManager;
import me.alanx.wisteria.core.transport.IoTransport;

public class SessionInitializationProcessor implements Processor<IoTransport, Packet> {

	private final SessionManager sessionManager;
	
	private Subscriber<? super Packet> subscriber;
	
	public SessionInitializationProcessor(SessionManager sessionManager) {
		super();
		this.sessionManager = sessionManager;
	}

	@Override
	public void subscribe(Subscriber<? super Packet> s) {
		
		this.subscriber = s;
	}

	@Override
	public <A> void onSubscribe(Subscription subscription) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNext(IoTransport t) {

		/*
		 * Create session
		 */
		if (this.sessionManager != null) {
			
			Session session = this.sessionManager.newSession(t);
			
			t.setSession(session);
			
			t.subscribe(subscriber);
			
			// Notify the transport to start working
			t.getTransportListeners().forEach(l -> l.onConnected());		
		}
			
		
	}

	@Override
	public void onError(Throwable t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCompletion() {
		// TODO Auto-generated method stub
		
	}

}
