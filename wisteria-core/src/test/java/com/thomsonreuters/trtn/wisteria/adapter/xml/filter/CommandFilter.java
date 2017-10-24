package com.thomsonreuters.trtn.wisteria.adapter.xml.filter;

import com.thomsonreuters.trtn.wisteria.core.event.Event;
import com.thomsonreuters.trtn.wisteria.core.filter.FilterBase;
import com.thomsonreuters.trtn.wisteria.core.filter.Filter.NextFilter;
import com.thomsonreuters.trtn.wisteria.core.session.Session;

public abstract class CommandFilter extends FilterBase{
	@Override
    public void clientMessageReceived(NextFilter nextFilter, Session session, Object message) throws Exception {
		Object filteredObject = message;
		if(accept(session, message)){
			filteredObject = wrap(session, message);
		}
		nextFilter.messageReceived(session, filteredObject);
    }
	
	protected abstract boolean accept(Session session, Object message);
	protected abstract Event wrap(Session session, Object message);
}
