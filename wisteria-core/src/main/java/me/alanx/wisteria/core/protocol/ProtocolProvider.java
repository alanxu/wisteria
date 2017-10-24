package me.alanx.wisteria.core.protocol;

import me.alanx.wisteria.core.transport.IoTransport;
import me.alanx.wisteria.core.transport.ProtocoledTransport;

public interface ProtocolProvider {
	
	public Protocol<Message> getProtocol();
	
	public ProtocoledTransport wrap(IoTransport ioTransport);
	
}
