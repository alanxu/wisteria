package com.thomsonreuters.trtn.wisteria.core.transport;

import com.thomsonreuters.trtn.wisteria.core.session.Session;
import com.thomsonreuters.trtn.wisteria.core.session.listener.SessionListener;

public interface TransportListener extends SessionListener{
	void onConnectionLost(Session session);
	void onDisconnect(Session session);
	void onConnect(Session session);
}
