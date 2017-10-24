package com.thomsonreuters.trtn.wisteria.core.session;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.thomsonreuters.trtn.wisteria.conf.Configuration;
import com.thomsonreuters.trtn.wisteria.conf.Profile;
import com.thomsonreuters.trtn.wisteria.core.filter.DefaultFilterChain;
import com.thomsonreuters.trtn.wisteria.core.filter.FilterChain;
import com.thomsonreuters.trtn.wisteria.core.session.listener.SessionListener;
import com.thomsonreuters.trtn.wisteria.core.session.listener.SessionListenerManager;

@SuppressWarnings("serial")
public abstract class AbstractSession implements Session, Serializable{
	private final String sessionId;
	
	private volatile SessionStatus status = SessionStatus.NEW;
	private SessionListenerManager sessionListeners;
	protected Profile sessionConfiguation;
	private FilterChain filterChain;
	private final ConcurrentHashMap<Object, Object> attributes = new ConcurrentHashMap<Object, Object>();
	
	public AbstractSession(String sessionId){
		this.sessionId = sessionId;
		this.filterChain = new DefaultFilterChain(this);
		this.sessionListeners = new SessionListenerManager();
	}	

	@Override
	public String getSessionId() {
		return this.sessionId;
	}
	
	@Override
	public void init(Profile configuration){
		this.sessionConfiguation = configuration;
		
		if(this.sessionConfiguation.getSessionListeners() != null)
			this.sessionListeners.addSessionListener(this.sessionConfiguation.getSessionListeners());
	}
	
	@Override
	public SessionStatus getStatus(){
		return this.status;
	}
	
	@Override
	public void setStatus(SessionStatus status){
		this.status = status;
	}	

	@Override
	public <C extends SessionListener> Set<C> getSessionListeners(Class<C> clazz) {
		return this.sessionListeners.getSessionListeners(clazz);
	}

	@Override
	public void addSessionListener(SessionListener sessionListener) {
		this.sessionListeners.addSessionListener(sessionListener);
	}
	
	@Override
	public void addSessionListeners(Collection<SessionListener> sessionListeners){
		this.sessionListeners.addSessionListener(sessionListeners);
	}

	@Override
	public FilterChain getFilterChain() {
		return this.filterChain;
	}

	@Override
	public void setAttribute(Object key, Object value) {
		this.attributes.put(key, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAttribute(Object key) {
		return (T)this.attributes.get(key);
	}
	
	@Override
	public boolean removeAttribute(Object key){
		return this.attributes.remove(key) != null;
	}
	
	@Override
	public Configuration getConfiguration(){
		return this.sessionConfiguation.getConfiguration();
	}
}
