package me.alanx.wisteria.core.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import me.alanx.wisteria.core.transport.ClientTransport;
import me.alanx.wisteria.core.transport.IoClientTransport;
import me.alanx.wisteria.core.transport.TransportListener;


public class AsyncClientSocketTransport extends AsyncSocketTransport implements IoClientTransport{

	
	private volatile AtomicBoolean handshakeRequired = new AtomicBoolean(false);
	
	private volatile AtomicLong heartbeatIntervelInMilli = new AtomicLong(0);
	
	private AsyncClientSocketTransport() {
		super(null);
	}
	
	@Override
	public AsyncClientSocketTransport connect(String serverIp, int serverPort) {
		try {
			this.channel = AsynchronousSocketChannel.open();
			this.channel.connect(new InetSocketAddress(serverIp, serverPort)).get();
			
			this.transportListeners.forEach(l -> l.onConnected());
			
			return this;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public AsyncClientSocketTransport listenedBy(TransportListener listener) {
		super.listenedBy(listener);
		return this;
	}
	
	public AsyncClientSocketTransport requireHandshake(boolean required) {
		this.handshakeRequired.set(required);
		return this;
	}
	
	public AsyncClientSocketTransport heartbeatIntervel(int intervel, TimeUnit unit) {
		this.heartbeatIntervelInMilli.set(unit.toMillis(intervel));
		return this;
	}
	
	
	private void handshake() {
		
	}
	
	public static AsyncClientSocketTransport open() {
		return new AsyncClientSocketTransport();
	}
	
	
}


