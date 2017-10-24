package me.alanx.wisteria.core.filter;

import me.alanx.wisteria.core.protocol.BasicProtocol;

public class BasicFilterChainBuilder implements FilterChainBuilder {

	private static final Filter gzipFilter = new GzipFilter();
	
	private static final Filter heartbeatFilter = new HeartbeatHandlingFilter();
	
	private static final Filter protocolFilter = new ProtocolFilter(new BasicProtocol());
	
	@Override
	public FilterChain buildFilterChain(FilterTarget targetTask) {
		FilterChain filterChain = FilterChain.start()
				.append(gzipFilter)
				.append(protocolFilter)
				.append(heartbeatFilter)
				.endWith(targetTask);
		
		return filterChain;
	}

}
