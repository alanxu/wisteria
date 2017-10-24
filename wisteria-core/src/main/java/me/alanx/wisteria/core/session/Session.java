package me.alanx.wisteria.core.session;

import java.util.UUID;

import me.alanx.wisteria.core.filter.FilterChainBuilder;
import me.alanx.wisteria.core.transport.ProtocoledTransport;

public class Session {
	
	private final String id = UUID.randomUUID().toString();

	private final ProtocoledTransport transport;
	
	private FilterChainBuilder filterChain;
	

	public Session(ProtocoledTransport transport) {
		super();
		this.transport = transport;
	}


	public ProtocoledTransport getTransport() {
		return this.transport;
	}


	/**
	 * @return the filterChain
	 */
	public FilterChainBuilder getFilterChain() {
		return filterChain;
	}


	/**
	 * @param filterChain the filterChain to set
	 */
	public void setFilterChain(FilterChainBuilder filterChain) {
		this.filterChain = filterChain;
	}


	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	
	

}
