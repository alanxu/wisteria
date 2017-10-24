package me.alanx.wisteria.core.filter;

public abstract class Filter {
	
	public void filter(FilterContext context, FilterChain chain) {
		
		boolean continued;
		
		if (context.isInbound()) {
			continued = filterInbound(context);
		} else {
			continued = filterOutbound(context);
		}
		
		if (continued) {
			chain.filter(context);
		}
		
	}
	
	protected abstract boolean filterInbound(FilterContext context);
	
	protected abstract boolean filterOutbound(FilterContext context);
	
}
