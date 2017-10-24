package com.thomsonreuters.trtn.wisteria.core.event;

import com.thomsonreuters.trtn.wisteria.core.session.Session;

public class ClientConnectionLostEvent extends EventBase{

	public ClientConnectionLostEvent(Session session, int priority, String name) {
		super(session, priority, name);
	}

}
