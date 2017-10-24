package me.alanx.wisteria.core.socket;

import me.alanx.wisteria.core.protocol.Message;
import me.alanx.wisteria.core.session.Session;

public interface ServerHandler {
	
	void handle(Session session, Message message);
	
}
