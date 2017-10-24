package me.alanx.wisteria.core.protocol;

import me.alanx.wisteria.core.transport.BasicProtocoledTransport;
import me.alanx.wisteria.core.transport.IoTransport;
import me.alanx.wisteria.core.transport.ProtocoledTransport;

public class BasicProtocolProvider implements ProtocolProvider {

	private static BasicProtocol PROTOCOL = new BasicProtocol();
	
	
	@Override
	public Protocol<Message> getProtocol() {
		return PROTOCOL;
	}

	@Override
	public ProtocoledTransport wrap(IoTransport ioTransport) {
		return new BasicProtocoledTransport(ioTransport).protocol(PROTOCOL);
	}

}
