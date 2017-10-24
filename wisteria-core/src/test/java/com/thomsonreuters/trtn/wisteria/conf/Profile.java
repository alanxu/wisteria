package com.thomsonreuters.trtn.wisteria.conf;

import java.util.List;
import java.util.Map;

import com.thomsonreuters.trtn.wisteria.core.event.Event;
import com.thomsonreuters.trtn.wisteria.core.event.EventHandler;
import com.thomsonreuters.trtn.wisteria.core.filter.Filter;
import com.thomsonreuters.trtn.wisteria.core.session.listener.SessionListener;

public interface Profile {
	List<SessionListener> getSessionListeners();
	Map<String, Filter> getFilters();
	Map<Class<Event>, EventHandler> getEventHandlerMapping();
	int getEventQueueInitialSize();
	int getWriteQueueInitialSize();
	Configuration getConfiguration();
}
