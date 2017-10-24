package com.thomsonreuters.trtn.wisteria.core.event;

import com.thomsonreuters.trtn.wisteria.core.session.Session;

public interface Event {
	Session getSession();
	int getPriority();
	String getName();
}
