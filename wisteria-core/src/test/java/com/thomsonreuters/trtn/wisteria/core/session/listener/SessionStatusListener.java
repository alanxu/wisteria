package com.thomsonreuters.trtn.wisteria.core.session.listener;

import com.thomsonreuters.trtn.wisteria.core.session.Session;
import com.thomsonreuters.trtn.wisteria.core.session.SessionStatus;

public interface SessionStatusListener extends SessionListener{
	/**
	 * Should be fired when {@link SessionStatus} of {@link Session} is set to {@link SessionStatus.NEW}
	 * 
	 * @param session
	 */
	void onSessionCreated(Session session);
	
	/**
	 * Should be fired when {@link SessionStatus} of {@link Session} is set to {@link SessionStatus.INITIATED}
	 * 
	 * @param session
	 */
	void onSessionInitiated(Session session);
	
	/**
	 * Should be fired when {@link SessionStatus} of {@link Session} is set to {@link SessionStatus.PENDING}
	 * 
	 * @param session
	 */
	void onSessionSuspended(Session session);
	
	/**
	 * Should be fired when {@link SessionStatus} of {@link Session} is set to {@link SessionStatus.READY}
	 * 
	 * @param session
	 */
	void onSessionActivated(Session session);
	
	/**
	 * Should be fired when {@link SessionStatus} of {@link Session} is set to {@link SessionStatus.CLOSED}
	 * 
	 * @param session
	 */
	void onSessionClosed(Session session);
}
