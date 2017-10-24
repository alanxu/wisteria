package com.thomsonreuters.trtn.wisteria.core;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thomsonreuters.trtn.wisteria.conf.Configuration;
import com.thomsonreuters.trtn.wisteria.core.acceptor.Acceptor;
import com.thomsonreuters.trtn.wisteria.core.event.EventHandler;
import com.thomsonreuters.trtn.wisteria.core.filter.Filter;
import com.thomsonreuters.trtn.wisteria.core.operation.Operation;
import com.thomsonreuters.trtn.wisteria.core.processor.DefaultProcessorFactory;
import com.thomsonreuters.trtn.wisteria.core.processor.DefaultProcessorPool;
import com.thomsonreuters.trtn.wisteria.core.processor.Processor;
import com.thomsonreuters.trtn.wisteria.core.processor.ProcessorFactory;
import com.thomsonreuters.trtn.wisteria.core.protocol.BasicProtocolFilter;
import com.thomsonreuters.trtn.wisteria.core.session.Session;
import com.thomsonreuters.trtn.wisteria.core.session.SessionIdGenerator;
import com.thomsonreuters.trtn.wisteria.core.session.SessionInitiationFilter;
import com.thomsonreuters.trtn.wisteria.core.session.SimpleSessionIdGenerator;
import com.thomsonreuters.trtn.wisteria.core.session.listener.SessionListener;
import com.thomsonreuters.trtn.wisteria.core.socket.DefaultSocketSessionFactory;
import com.thomsonreuters.trtn.wisteria.core.socket.SocketAcceptor;
import com.thomsonreuters.trtn.wisteria.core.socket.SocketReadClientInputOperationProvider;
import com.thomsonreuters.trtn.wisteria.core.socket.SocketSessionFactory;
import com.thomsonreuters.trtn.wisteria.core.transport.TransportListener;
import com.thomsonreuters.trtn.wisteria.mock.MockConfiguration;
import com.thomsonreuters.trtn.wisteria.mock.MockSessionMessageHandler;

public class WisteriaService{

	private Logger logger = LoggerFactory.getLogger(WisteriaService.class);
	
	private Configuration configuration;

	
	private volatile AtomicBoolean started = new AtomicBoolean(false);
	
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final ReadLock readLock = lock.readLock();
	private final WriteLock writeLock = lock.writeLock();
	
	private Acceptor acceptor;
	private Processor processor;
	private BlockingQueue<Operation> workQueue;
	
	ExecutorService executor;
	ScheduledExecutorService scheduler;
	
	public WisteriaService() {
		super();
		
		//Create executor
		executor = Executors.newCachedThreadPool();
		scheduler = Executors.newScheduledThreadPool(1);
		
		//Create session work queue
		workQueue = new LinkedBlockingQueue<Operation>();
		
		this.configuration = new MockConfiguration(this.scheduler, this.workQueue);
		
		//Create acceptor
		SocketAddress[] socketAddresses = new SocketAddress[1];
		socketAddresses[0] = new InetSocketAddress("localhost", 51925);
		acceptor = new SocketAcceptor(socketAddresses, getSocketSessionFactory(), executor, workQueue, new SocketReadClientInputOperationProvider());
		

		//jobService.register(selectorRef);
		
		//Create processor pool
		ProcessorFactory processorFactory = new DefaultProcessorFactory();
		processor = new DefaultProcessorPool(workQueue, processorFactory);
	}
	
	private void resolveConfiguration(Object conf){
		if(conf instanceof String){
			//TODO resolve as a file path
			throw new RuntimeException("Cannot resolve configuration! ");
		}else if(conf instanceof Configuration){
			this.configuration = (Configuration)conf;
		}else{
			throw new RuntimeException("Cannot resolve configuration! ");
		}
	}

	public void start() throws Exception {
		this.acceptor.bind();
		this.processor.start();
	}
	
	public void stop() throws Exception {
		this.acceptor.close();
		this.processor.stop();
	}

	public boolean isRunning() {
		return this.started.get();
	}	
	
	private SocketSessionFactory getSocketSessionFactory(){
		SessionListener sessionListener = new MockSessionMessageHandler();
		SessionListener transportListener = new TransportListener(){
			@Override
			public void onConnectionLost(Session session) {
				logger.debug("Transport connection lost. Session: "+session.getSessionId());				
			}

			@Override
			public void onDisconnect(Session session) {
				logger.debug("Transport disconnected. Session: "+session.getSessionId());	
			}

			@Override
			public void onConnect(Session session) {
				logger.debug("Transport connected. Session: "+session.getSessionId());
			}
			
		};
		Set<SessionListener> sessionListeners = new HashSet<SessionListener>();
		sessionListeners.add(sessionListener);
		sessionListeners.add(transportListener);
			
		SessionIdGenerator sessionIdGenerator = new SimpleSessionIdGenerator();
		LinkedHashMap<String, Filter> filters = new LinkedHashMap<String, Filter>();
		//filters.put("rwp2", new BasicProtocolFilter(new Rwp2Protocol()));
		filters.put("initiate", new SessionInitiationFilter(configuration));
		//filters.put("command", new MakeProcessedCommandFilter());
		
		EventHandler eventHandler = null;
		
		return new DefaultSocketSessionFactory(
				sessionListeners, sessionIdGenerator, filters);
	}

}
