package com.thomsonreuters.trtn.wisteria.mock;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thomsonreuters.trtn.wisteria.core.event.Event;
import com.thomsonreuters.trtn.wisteria.core.session.Session;
import com.thomsonreuters.trtn.wisteria.core.session.listener.SessionErrorListener;
import com.thomsonreuters.trtn.wisteria.core.session.listener.SessionMessageListener;
import com.thomsonreuters.trtn.wisteria.core.session.listener.SessionStatusListener;

public class MockSessionMessageHandler implements SessionMessageListener, SessionErrorListener, SessionStatusListener{

	private Logger logger = LoggerFactory.getLogger(MockSessionMessageHandler.class);
	

	@Override
	public void onMessageReceived(Session session, Object message) {
		if(message instanceof Event){
			session.issueEvent((Event)message);
		}else{
			logger.warn("Session "+session.getSessionId()+". Message ignored. " + message);
		}
	}

	@Override
	public void onMessageSent(Session session, Object message) {
		logger.info("onMessageSent: " + session.getSessionId() + " -> " + message);
	}
	
	@Override
	public void onExceptionCaught(Session session, Throwable exception) {
		logger.error("Handling error for session "+session.getSessionId(), exception);
	}

	@Override
	public void onSessionCreated(Session session) {
		logger.info("onSessionCreated: " + session.getSessionId());
	}

	@Override
	public void onSessionInitiated(Session session) {
		logger.info("onSessionInitiated: " + session.getSessionId());
	}

	@Override
	public void onSessionSuspended(Session session) {
		logger.info("onSessionSuspended: " + session.getSessionId());
	}

	@Override
	public void onSessionActivated(Session session) {
		logger.info("onSessionActivated: " + session.getSessionId());
	}

	@Override
	public void onSessionClosed(Session session) {
		logger.info("onSessionClosed: " + session.getSessionId());
	}
	
}
