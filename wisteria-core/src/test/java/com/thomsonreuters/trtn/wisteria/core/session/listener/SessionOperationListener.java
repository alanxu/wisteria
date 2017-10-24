package com.thomsonreuters.trtn.wisteria.core.session.listener;

import com.thomsonreuters.trtn.wisteria.core.session.Session;

public interface SessionOperationListener extends SessionListener {
	void beforeStart(Session session);
	void afterFinish(Session session);
	void onError(Session session, Throwable t);
}