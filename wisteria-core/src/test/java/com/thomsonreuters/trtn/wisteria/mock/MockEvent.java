package com.thomsonreuters.trtn.wisteria.mock;

import com.thomsonreuters.trtn.wisteria.core.event.EventBase;
import com.thomsonreuters.trtn.wisteria.core.session.Session;

public class MockEvent extends EventBase{

	private String message;
	
	public MockEvent(Session session, int priority, String name) {
		super(session, priority, name);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
