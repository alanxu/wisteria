package com.thomsonreuters.trtn.wisteria.mock;

import com.thomsonreuters.trtn.wisteria.core.event.EventHandler;
import com.thomsonreuters.trtn.wisteria.core.event.EventHandlingException;

public class MockEventHandler implements EventHandler<MockEvent>{

	@Override
	public void handle(MockEvent event) throws EventHandlingException {
		event.getSession();
		
	}
	
}