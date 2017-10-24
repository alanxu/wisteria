package me.alanx.wisteria.core.protocol;

import java.io.Serializable;

import me.alanx.wisteria.core.session.Session;

public abstract class Message implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private transient Session session;

	/**
	 * @return the session
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * @param session the session to set
	 */
	public void setSession(Session session) {
		this.session = session;
	}
	
	
}
