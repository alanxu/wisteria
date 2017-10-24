package me.alanx.wisteria.core.transport;

import java.nio.ByteBuffer;

import me.alanx.wisteria.core.filter.FilterChain;
import me.alanx.wisteria.core.filter.FilterChainBuilder;
import me.alanx.wisteria.core.protocol.Message;

public class FilteredTransport extends AbstractProtocoledTransport {

	private final FilterChainBuilder fchaiBuilder;
	
	public FilteredTransport(IoTransport socketTransport, FilterChainBuilder fchaiBuilder) {
		super(socketTransport);
		this.fchaiBuilder = fchaiBuilder;
	}

	@Override
	protected Message[] encode(ByteBuffer buf) {
		FilterChain filterChain = this.fchaiBuilder.buildFilterChain(null);
		return filterChain.filter(this.getSession(), buf);
	}

	@Override
	protected ByteBuffer decode(Message msg) {
		FilterChain filterChain = this.fchaiBuilder.buildFilterChain(null);
		filterChain.forOutbound();
		return filterChain.filter(this.getSession(), msg);
	}

}
