package me.alanx.wisteria.core.transport;

public interface TransportListener<T> {

	public void onConnected();
	
	public void onDisconnected();
	
	public void onReceived(IoTransport transport, T data);
	
	public void onSent(IoTransport transport, T data);
	
}
