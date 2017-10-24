package com.thomsonreuters.trtn.wisteria.mock;

import com.thomsonreuters.trtn.wisteria.core.filter.FilterBase;
import com.thomsonreuters.trtn.wisteria.core.session.Session;

public class MockFilter extends FilterBase{
	@Override
    public void clientMessageReceived(NextFilter nextFilter, Session session, Object message) throws Exception {
		Object filteredObject = message;
		MockEvent event = new MockEvent(session, 0, "MockEvent");
		event.setMessage(message.toString());
		nextFilter.messageReceived(session, event);
    }

}
