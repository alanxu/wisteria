package me.alanx.wisteria.core.session;

import me.alanx.wisteria.core.protocol.ProtocolProvider;
import me.alanx.wisteria.core.transport.IoTransport;
import me.alanx.wisteria.core.transport.ProtocoledTransport;

public class SessionManager {
	
	private static final ThreadLocal<Session> CURRENT_SESSION = new ThreadLocal<>();
	
	private final ProtocolProvider protocolProvider;
	
	//private final 
	
	public SessionManager(ProtocolProvider protocolProvider) {
		this.protocolProvider = protocolProvider;
	}
	
	public Session newSession(IoTransport transport) {
		
		ProtocoledTransport protocoledTransport = this.protocolProvider.wrap(transport);
		Session session = new Session(protocoledTransport);
		//CURRENT_SESSION.set(session);
		return session;
	}

	
}
