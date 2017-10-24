package com.thomsonreuters.trtn.wisteria.core.event;

import com.thomsonreuters.trtn.wisteria.core.session.Session;

public class EventBase implements Event{
	private final Session session;
	private final int priority;
	private final String name;
	
	
	public EventBase(Session session, int priority, String name) {
		super();
		this.session = session;
		this.priority = priority;
		this.name = name;
	}


	public Session getSession(){
		return this.session;
	}


	public int getPriority() {
		return priority;
	}


	public String getName() {
		return name;
	}
	
	
}
