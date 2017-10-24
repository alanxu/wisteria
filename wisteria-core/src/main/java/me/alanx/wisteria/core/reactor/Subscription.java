package me.alanx.wisteria.core.reactor;

public interface Subscription {
	
	public void request(long timeout);
	
	public boolean cancel();
	
	public SubscribeMode mode();
	
}
