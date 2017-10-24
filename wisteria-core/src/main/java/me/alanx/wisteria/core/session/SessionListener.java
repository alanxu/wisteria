package me.alanx.wisteria.core.session;

public interface SessionListener {
	
	public void onSessionOpened(Session session);
	
	public void onSessionClosed(Session session);
	
}
