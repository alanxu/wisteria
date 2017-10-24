package me.alanx.wisteria.core.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;

import me.alanx.wisteria.config.Configuration;
import me.alanx.wisteria.core.Server;
import me.alanx.wisteria.core.protocol.Message;
import me.alanx.wisteria.core.protocol.ProtocolProvider;
import me.alanx.wisteria.core.reactor.PassiveSubscription;
import me.alanx.wisteria.core.reactor.Publisher;
import me.alanx.wisteria.core.reactor.SubscribeMode;
import me.alanx.wisteria.core.reactor.Subscriber;
import me.alanx.wisteria.core.reactor.Subscription;
import me.alanx.wisteria.core.session.Session;
import me.alanx.wisteria.core.session.Session;
import me.alanx.wisteria.core.session.SessionManager;
import me.alanx.wisteria.core.transport.IoTransport;
import me.alanx.wisteria.core.transport.ProtocoledTransport;
import me.alanx.wisteria.core.transport.TransportListener;

public class AsyncSocketServer implements Server, Publisher<IoTransport>{

	private final SocketAddress address;
		
	private AsynchronousServerSocketChannel serverSocketChannel;
	
	private ExecutorService serverExecutor;
	
	private ExecutorService groupExecutor;
	
	private Configuration configration;
	
	private Subscriber<? super IoTransport> connectionSubscriber;
	
	public AsyncSocketServer(String serverIp, 
			int serverPort, 
			ExecutorService serverExecutor) {
		
		if(StringUtils.isBlank(serverIp)) {
			this.address = new InetSocketAddress(serverPort);
		} else {
			this.address = new InetSocketAddress(serverIp, serverPort);
		}
		
		this.serverExecutor = serverExecutor;
		
	}

	@Override
	public void start() {
		
		if(this.connectionSubscriber == null) {
			throw new IllegalStateException("No connection subscriber. ");
		}
		
		//TODO refactor thread pool creating and management
		this.groupExecutor = Executors.newCachedThreadPool();
		
		try {
			
			AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup.withCachedThreadPool(this.groupExecutor, 8);
			this.serverSocketChannel = AsynchronousServerSocketChannel.open(channelGroup);
			
			serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			serverSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 64 * 1024);
			
			serverSocketChannel.bind(address, 0);
			
			serverSocketChannel.accept(serverSocketChannel, new CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel>() {

				@Override
				public void completed(AsynchronousSocketChannel asynchronousSocketChannel, AsynchronousServerSocketChannel serverSocketChannel) {
					
					try {
						
						serverSocketChannel.accept(serverSocketChannel, this);
						
						asynchronousSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
						asynchronousSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 32 * 1024);
						asynchronousSocketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 32 * 1024);
						asynchronousSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
						
						AsyncSocketTransport socketTransport = AsyncSocketTransport
								.open(asynchronousSocketChannel);
						
						AsyncSocketServer.this.connectionSubscriber.onNext(socketTransport);
						
						
						
						//----
						
						//----
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
				}

				@Override
				public void failed(Throwable exc, AsynchronousServerSocketChannel attachment) {
					// TODO Auto-generated method stub
					
				}

				
				
			});
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void subscribe(Subscriber<? super IoTransport> s) {
		this.connectionSubscriber = s;
		
	}
	

}

