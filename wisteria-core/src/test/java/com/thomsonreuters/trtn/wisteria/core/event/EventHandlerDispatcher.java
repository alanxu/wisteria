package com.thomsonreuters.trtn.wisteria.core.event;

import java.util.Map;

public class EventHandlerDispatcher implements EventHandler{

	private Map<Class<Event>, EventHandler> eventHandlerMap;
	
	public EventHandlerDispatcher(Map<Class<Event>, EventHandler> eventHandlerMapping){
		this.eventHandlerMap = eventHandlerMapping;
	}
	
	@Override
	public void handle(Event event) throws EventHandlingException {
		EventHandler handler = this.eventHandlerMap.get(event.getClass());
		if(handler == null){
			throw new IllegalArgumentException("Can not resolve the event! ");
		}
		handler.handle(event);
	}

}
