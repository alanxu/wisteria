package me.alanx.wisteria.core.filter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import me.alanx.wisteria.core.protocol.BasicProtocol;

public class DefaultFilterChainBuilder implements FilterChainBuilder {

	private final List<Filter> filters;
	
	public DefaultFilterChainBuilder(List<Filter> filters) {
		super();
		this.filters = filters;
	}



	@Override
	public FilterChain buildFilterChain(FilterTarget targetTask) {
		FilterChain filterChain = FilterChain.start();
		for (Filter f : filters) {
			filterChain.append(f);
		}
		if (targetTask != null)
			filterChain.endWith(targetTask);
		else
			filterChain.end();
		
		return filterChain;
	}

}
