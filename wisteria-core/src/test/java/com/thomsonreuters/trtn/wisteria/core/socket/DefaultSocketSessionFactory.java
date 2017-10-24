package com.thomsonreuters.trtn.wisteria.core.socket;

import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.thomsonreuters.trtn.wisteria.core.event.EventHandler;
import com.thomsonreuters.trtn.wisteria.core.filter.Filter;
import com.thomsonreuters.trtn.wisteria.core.session.DefaultSession;
import com.thomsonreuters.trtn.wisteria.core.session.Session;
import com.thomsonreuters.trtn.wisteria.core.session.SessionIdGenerator;
import com.thomsonreuters.trtn.wisteria.core.session.SessionOperation;
import com.thomsonreuters.trtn.wisteria.core.session.listener.SessionListener;

public class DefaultSocketSessionFactory implements SocketSessionFactory{
	
	private SessionIdGenerator sessionIdGenerator;
	private LinkedHashMap<String, Filter> filters;
	private EventHandler eventHandler;
	private Set<SessionListener> listeners;

	public DefaultSocketSessionFactory(
			Set<SessionListener> sessionListeners,
			SessionIdGenerator sessionIdGenerator, 
			LinkedHashMap<String, Filter> filters) {
		super();
		this.sessionIdGenerator = sessionIdGenerator;
		this.filters = filters;
		this.eventHandler = eventHandler;
		this.listeners = sessionListeners;
	}

	@Override
	public Session newSession(SocketChannel socketChannel) {
		SocketTransport2 transport = new SocketTransport2(socketChannel, Charset.forName("UTF-8"), 1024); 
		Session session = new DefaultSession(this.sessionIdGenerator.generate(), transport);
		session.addSessionListeners(listeners);
		for(Map.Entry<String, Filter> f : this.filters.entrySet()){
			session.getFilterChain().addLast(f.getKey(), f.getValue());
		}
		return session;
	}

}
