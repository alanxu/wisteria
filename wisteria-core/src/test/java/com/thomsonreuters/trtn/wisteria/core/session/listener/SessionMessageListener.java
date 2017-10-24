package com.thomsonreuters.trtn.wisteria.core.session.listener;

import com.thomsonreuters.trtn.wisteria.core.session.Session;

public interface SessionMessageListener extends SessionListener{
	void onMessageReceived(Session session, Object message);
	void onMessageSent(Session session, Object message);
}
