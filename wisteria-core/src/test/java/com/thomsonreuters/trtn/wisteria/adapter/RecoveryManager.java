package com.thomsonreuters.trtn.wisteria.adapter;

import com.thomsonreuters.trtn.wisteria.core.session.Session;
import com.thomsonreuters.trtn.wisteria.core.session.listener.SessionStatusListener;

public class RecoveryManager implements SessionStatusListener{

	@Override
	public void onSessionCreated(Session session) {}

	@Override
	public void onSessionInitiated(Session session) {}

	@Override
	public void onSessionSuspended(Session session) {}

	@Override
	public void onSessionActivated(final Session session) {
/*		if(!(session instanceof LbnSession)){
			throw new IllegalArgumentException();
		}
		LbnSession s = (LbnSession)session;
		//Connector c = ((LbnConnectorImpl)s.getLbnConnector()).getRAFConnector();
		RecoveryResultRetriever recoveryRetriever = new RAFRecoveryResultRetriever(c, 500);
		try {
			List<CDealRecord> results = recoveryRetriever.getRecoveredDeal();
			if(results != null){
				for(final CDealRecord deal : results){
					session.getTransport().send(deal);
				}
			}
		} catch (RetrieveRecoveryResultFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	@Override
	public void onSessionClosed(Session session) {}

}
