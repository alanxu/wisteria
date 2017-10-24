package a;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import me.alanx.wisteria.core.DispatcherProcessor;
import me.alanx.wisteria.core.ProtocoledProcessor;
import me.alanx.wisteria.core.SessionInitializationProcessor;
import me.alanx.wisteria.core.filter.BasicFilterChainBuilder;
import me.alanx.wisteria.core.filter.FilterChainBuilder;
import me.alanx.wisteria.core.filter.FilteredProtocolProvider;
import me.alanx.wisteria.core.protocol.BasicProtocol;
import me.alanx.wisteria.core.protocol.Message;
import me.alanx.wisteria.core.protocol.Protocol;
import me.alanx.wisteria.core.protocol.ProtocolProvider;
import me.alanx.wisteria.core.reactor.Subscriber;
import me.alanx.wisteria.core.reactor.Subscription;
import me.alanx.wisteria.core.service.HeartbeatService;
import me.alanx.wisteria.core.session.Session;
import me.alanx.wisteria.core.session.SessionManager;
import me.alanx.wisteria.core.socket.AsyncSocketServer;
import me.alanx.wisteria.core.socket.ServerHandler;
import me.alanx.wisteria.core.transport.BasicProtocoledClientTransport;

public class App {

	public App() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		
		Protocol<Message> protocol = new BasicProtocol();
		
		ExecutorService serverExecutor = Executors.newCachedThreadPool();
		
		FilterChainBuilder fcb = new BasicFilterChainBuilder();
		
		ProtocolProvider pp = new FilteredProtocolProvider(protocol, fcb);
		
		SessionManager sessionManager = new SessionManager(pp);
		
		SessionInitializationProcessor sessionInitialProcessor = new SessionInitializationProcessor(sessionManager);
		
		ProtocoledProcessor protocolProcessor = new ProtocoledProcessor(fcb, serverExecutor);
		
		DispatcherProcessor dispatcherProcessor = new DispatcherProcessor();
		

		ServerHandler serverHandler = new ServerHandler() {

			@Override
			public void handle(Session session, Message message) {
				
				System.out.println("Server: " + message);

				session.getTransport().write(message);
			}
			
		};

		AsyncSocketServer server = new AsyncSocketServer("localhost", 1234, serverExecutor);
		
		server.subscribe(sessionInitialProcessor);
		
		sessionInitialProcessor.subscribe(protocolProcessor);
		
		protocolProcessor.subscribe(dispatcherProcessor);
		
		server.start();
		
		

		
		Subscriber<Message> clientSubscriber = new Subscriber<Message> (){

			@Override
			public <A> void onSubscribe(Subscription subscription) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onNext(Message m) {
				System.out.println("Client: " + m);
			}

			@Override
			public void onError(Throwable t) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onCompletion() {
				// TODO Auto-generated method stub
				
			}
			
		};
			
		
			

		Runnable writer = () -> {
			
			BasicProtocoledClientTransport client = BasicProtocoledClientTransport
					.open()
					.protocol(protocol)
					.subscribedBy(clientSubscriber)
					.connect("localhost", 1234);
			
			
			ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
			
			
			
			HeartbeatService hbSvc = new HeartbeatService(executor, client);
			hbSvc.start();
			
			for (;;) {

				

				Future<Integer> f = client.write(new Message() {
					@Override
					public String toString() {
						return "hello message and something";
					}
				});

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		};

		Thread t1 = new Thread(writer);
		t1.start();

		Thread t2 = new Thread(writer);
		t2.start();

		Thread t3 = new Thread(writer);
		t3.start();

		Thread t4 = new Thread(writer);
		t4.start();

		Thread t5 = new Thread(writer);
		t5.start();

		try {
			Thread.sleep(Long.MAX_VALUE);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	static String encodeBytes(ByteBuffer bs) {
		try {
			return "" + Charset.forName("UTF-8").newDecoder().decode(bs);
		} catch (CharacterCodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
