package com.thomsonreuters.trtn.wisteria.core.event;


public interface EventHandler<H extends Event> {
	void handle(H event) throws EventHandlingException;
}
