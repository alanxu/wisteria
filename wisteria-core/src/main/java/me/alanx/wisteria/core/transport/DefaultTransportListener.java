package me.alanx.wisteria.core.transport;

import java.nio.ByteBuffer;

import me.alanx.wisteria.core.session.Session;

public class DefaultTransportListener implements TransportListener<ByteBuffer> {

	private final Session session;
	
	
	
	public DefaultTransportListener(Session session) {
		super();
		this.session = session;
	}

	@Override
	public void onConnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceived(ByteBuffer data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSent(ByteBuffer data) {
		// TODO Auto-generated method stub
		
	}

	
	
}
