package com.thomsonreuters.trtn.wisteria.core.session;

public class WriteRequest {
	private String destination;
	private Object payload;
	private Session session;
	private int priority;

	public WriteRequest(String destination, Object payload, Session session,
			int priority) {
		super();
		this.destination = destination;
		this.payload = payload;
		this.session = session;
		this.priority = priority;
	}

	public String getDestination() {
		return destination;
	}

	public Object getPayload() {
		return payload;
	}

	public Session getSession() {
		return session;
	}

	public int getPriority() {
		return priority;
	}

}
