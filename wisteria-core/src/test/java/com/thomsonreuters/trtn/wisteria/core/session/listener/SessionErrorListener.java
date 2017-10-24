package com.thomsonreuters.trtn.wisteria.core.session.listener;

import com.thomsonreuters.trtn.wisteria.core.session.Session;

public interface SessionErrorListener extends SessionListener {
	void onExceptionCaught(Session session, Throwable exception);
}
