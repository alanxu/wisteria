package com.thomsonreuters.trtn.wisteria.core.session.listener;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class SessionListenerManager {
	private Set<SessionListener> listeners = new CopyOnWriteArraySet<SessionListener>();
	
	public void addSessionListener(SessionListener sessionListener){		
		listeners.add(sessionListener);
	}
	
	public void addSessionListener(Collection<SessionListener> sessionListeners){
		synchronized(sessionListeners){
			for(SessionListener l : sessionListeners){
				listeners.add(l);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public <C extends SessionListener> Set<C> getSessionListeners(Class<C> clazz){
		Set<C> result = new HashSet<C>(this.listeners.size());
		for (SessionListener l : this.listeners) {
			if(clazz.isAssignableFrom(l.getClass())){
				result.add((C) l);
			}
		}
		return Collections.synchronizedSet(result);
	}
	
	
}
