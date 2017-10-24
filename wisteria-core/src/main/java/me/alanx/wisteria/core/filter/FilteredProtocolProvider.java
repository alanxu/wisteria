package me.alanx.wisteria.core.filter;

import me.alanx.wisteria.core.protocol.Message;
import me.alanx.wisteria.core.protocol.Protocol;
import me.alanx.wisteria.core.protocol.ProtocolProvider;
import me.alanx.wisteria.core.transport.FilteredTransport;
import me.alanx.wisteria.core.transport.IoTransport;
import me.alanx.wisteria.core.transport.ProtocoledTransport;

public class FilteredProtocolProvider implements ProtocolProvider {

	private final Protocol<Message> protocol;
	private final FilterChainBuilder fcb;
	
	public FilteredProtocolProvider(Protocol<Message> protocol, FilterChainBuilder fcb) {
		super();
		this.protocol = protocol;
		this.fcb = fcb;
	}

	@Override
	public Protocol<Message> getProtocol() {
		return this.protocol;
	}

	@Override
	public ProtocoledTransport wrap(IoTransport ioTransport) {
		ProtocoledTransport pTransport = new FilteredTransport(ioTransport, fcb);
		return pTransport;
	}

}
