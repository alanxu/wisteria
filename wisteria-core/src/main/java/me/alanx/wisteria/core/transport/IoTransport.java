package me.alanx.wisteria.core.transport;

import java.nio.ByteBuffer;

import me.alanx.wisteria.core.protocol.Packet;
import me.alanx.wisteria.core.reactor.Publisher;
import me.alanx.wisteria.core.session.Session;

public interface IoTransport extends Transport<ByteBuffer, ByteBuffer>, Publisher<Packet>{
	
	public void setSession(Session session);
	
	public Session getSession(Session session);
}
