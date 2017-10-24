package me.alanx.wisteria.core.filter;

public interface FilterChainBuilder {
	
	public FilterChain buildFilterChain(FilterTarget targetTask);
	
}
