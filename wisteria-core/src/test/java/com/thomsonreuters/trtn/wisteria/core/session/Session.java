package com.thomsonreuters.trtn.wisteria.core.session;

import java.util.Collection;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.thomsonreuters.trtn.wisteria.conf.Configuration;
import com.thomsonreuters.trtn.wisteria.conf.Profile;
import com.thomsonreuters.trtn.wisteria.core.event.Event;
import com.thomsonreuters.trtn.wisteria.core.event.EventHandler;
import com.thomsonreuters.trtn.wisteria.core.filter.FilterChain;
import com.thomsonreuters.trtn.wisteria.core.processor.Processor;
import com.thomsonreuters.trtn.wisteria.core.session.listener.SessionListener;
import com.thomsonreuters.trtn.wisteria.core.transport.Transport;
import com.thomsonreuters.trtn.wisteria.core.transport.Transport2;

public interface Session {
	
	String getSessionId();

	boolean isClientConnected();
	
	void switchNeedProcess(boolean need);
	
	Queue<Event> getEventQueue();
	
	boolean issueEvent(Event event);

	void addSessionListener(SessionListener sessionListener);
	
	void addSessionListeners(Collection<SessionListener> sessionListeners);
	
	<C extends SessionListener> Set<C> getSessionListeners(Class<C> clazz);
	
	@SuppressWarnings("rawtypes")
	Transport2 getTransport();
	
	Queue<WriteRequest> getWriteQueue();

	void setAttribute(Object key, Object value);

	<T> T getAttribute(Object key);
	
	boolean removeAttribute(Object key);
	
	void open() throws SessionException;
	
	void close() throws SessionException;
	
	Processor getProcessor();
	
	void setProcessor(Processor processor);
	
	FilterChain getFilterChain();
	
	boolean tryLock();
	
	boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException;
	
	void lock();
	
	void unlock();
	
	EventHandler getEventHandler();
	
	void setEventHandler(EventHandler eventHandler);
	
	void setSelectMode(int mode);
	
	int getSelectMode();
	
	SessionStatus getStatus();
	
	void setStatus(SessionStatus status);
	
	void init(Profile configuration);	
	
	Configuration getConfiguration();
}
