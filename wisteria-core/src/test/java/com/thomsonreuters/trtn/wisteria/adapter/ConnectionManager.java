package com.thomsonreuters.trtn.wisteria.adapter;

//import com.m_systems.comms.CConnectionException;
import com.thomsonreuters.trtn.wisteria.core.session.Session;
import com.thomsonreuters.trtn.wisteria.core.session.SessionStatus;
import com.thomsonreuters.trtn.wisteria.core.session.listener.SessionStatusListener;
//import com.thomsonreuters.trtn.wisteria.lbn.LbnSession;

public class ConnectionManager implements SessionStatusListener{

	@Override
	public void onSessionCreated(Session session) {}

	@Override
	public void onSessionInitiated(Session session) {
		checkSession(session);
		/*LbnSession lbnSession = (LbnSession)session;
		try {
			lbnSession.getLbnConnector().connect();
			lbnSession.setStatus(SessionStatus.READY);
			for(SessionStatusListener l : session.getSessionListeners(SessionStatusListener.class)){
				l.onSessionActivated(lbnSession);
			}
		} catch (CConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	@Override
	public void onSessionSuspended(Session session) {}

	@Override
	public void onSessionActivated(Session session) {}

	@Override
	public void onSessionClosed(Session session) {}
	
	private void checkSession(Session session){
		/*if(!(session instanceof LbnSession)){
			throw new IllegalArgumentException("Unsupported session");
		}*/
	}

}
