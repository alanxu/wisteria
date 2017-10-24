package me.alanx.wisteria.core.transport;

public interface TransportListener<T> {

	public void onConnected();
	
	public void onDisconnected();
	
	public void onReceived(T data);
	
	public void onSent(T data);
	
}
