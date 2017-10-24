package com.thomsonreuters.trtn.wisteria.core.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thomsonreuters.trtn.wisteria.core.acceptor.Acceptor;
import com.thomsonreuters.trtn.wisteria.core.event.EventBase;
import com.thomsonreuters.trtn.wisteria.core.event.SessionEventNames;
import com.thomsonreuters.trtn.wisteria.core.operation.Operation;
import com.thomsonreuters.trtn.wisteria.core.operation.OperationProvider;
import com.thomsonreuters.trtn.wisteria.core.service.AbstractService;
import com.thomsonreuters.trtn.wisteria.core.service.ServiceException;
import com.thomsonreuters.trtn.wisteria.core.session.DefaultSession;
import com.thomsonreuters.trtn.wisteria.core.session.Session;
import com.thomsonreuters.trtn.wisteria.core.session.listener.SessionStatusListener;

public class SocketAcceptor extends AbstractService implements Acceptor{
	private static Logger logger = LoggerFactory.getLogger(SocketAcceptor.class);
	
	public static final String SERVICE_NAME = "SocketAcceptor";
	
	private Map<SocketAddress, Object> socketAddressMap = new HashMap<SocketAddress, Object>(); 
	
	private volatile Selector selector;
	
	private SocketSessionFactory sessionFactory;
	
	//private BlockingQueue<DefaultSession> selectedSessions;
	
	private BlockingQueue<Operation> sessionWorkingQueue;
	
	private OperationProvider readSocketClientDataOperationProvider;
	
	/*public SocketAcceptor(String socketAddresses, SocketSessionFactory sessionFactory, ExecutorService executor, BlockingQueue<Operation> sessionWorkQueue, OperationProvider readClientDataOperationProvider){
		super(SERVICE_NAME, executor);
		
		
	} */
	public SocketAcceptor(){
		super(SERVICE_NAME, null);
	}
	public SocketAcceptor(SocketAddress[] socketAddresses, SocketSessionFactory sessionFactory, ExecutorService executor, BlockingQueue<Operation> sessionWorkQueue, OperationProvider readClientDataOperationProvider) {
		super(SERVICE_NAME, executor);
		
		if(sessionWorkQueue == null){
			throw new IllegalArgumentException("Session working queue must not be null! ");
		}
		
		for(SocketAddress adss : socketAddresses){
			this.socketAddressMap.put(adss, null);
		}
		this.sessionFactory = sessionFactory;
		this.sessionWorkingQueue = sessionWorkQueue;
		this.readSocketClientDataOperationProvider = readClientDataOperationProvider;
	}

	@Override
	public void bind() throws IOException {
		this.selector = Selector.open();
		
		for(SocketAddress adss : socketAddressMap.keySet()){
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			ServerSocket serverSocket = serverSocketChannel.socket();
			//serverSocket.setReuseAddress(true);
			serverSocket.bind(adss);
			register(serverSocketChannel, SelectionKey.OP_ACCEPT);
		}

		this.start();
		logger.info("SocketAdaptor started. ");
	}

	@Override
	public void close() throws IOException {
		for(SelectionKey key : this.selector.keys()){
			key.channel().close();
			key.cancel();
		}
		this.selector.close();
	}
	
	protected SelectionKey register(SelectableChannel channel, int op) {
		try {
			SelectionKey key = channel.register(this.selector, op);
			return key;
		} catch (ClosedChannelException e) {
			throw new RuntimeException("Failed registering channel! ", e);
		}
	}	

	@Override
	public void doService() throws ServiceException{
			try {
				int i = this.selector.select();

				if(i > 0){
					Set<SelectionKey> keys = this.selector.selectedKeys();
					Iterator<SelectionKey> iterator = keys.iterator();
					while(iterator.hasNext()){
						SelectionKey key = (SelectionKey)iterator.next();
						if(key.isValid()){
							if(key.isAcceptable()){
								ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel();
								SocketChannel socketChannel = serverSocketChannel.accept();
								while(socketChannel != null){
									Session session = createSession(socketChannel);
									
									//Notify Session Created.
									for(SessionStatusListener l : session.getSessionListeners(SessionStatusListener.class)){
										l.onSessionCreated(session);
									}
									
									socketChannel = serverSocketChannel.accept();
								}
								iterator.remove();
							}else{
								Session session = (Session)key.attachment();
								//The selectionKey should be removed from the set when its processing finishes.
								session.setAttribute(SocketSessionAttributeKeys.KEY_SELECTEDKEYSET, keys);
								
								//Indicate that the Session is selected by NIO selector.
								session.setSelectMode(session.getSelectMode() | DefaultSession.SelectMode.SOCKET);
								//this.sessionPool.offer(session);

								//SocketTransport2 transport = (SocketTransport2)session.getTransport();
								
								
								Operation readOperation = readSocketClientDataOperationProvider.newOperation(session);
								this.sessionWorkingQueue.add(readOperation);
							}
							
							if(key.isReadable()){
								
							}
						}else{
							DefaultSession session = (DefaultSession)key.attachment();
							session.issueEvent(new EventBase(session, Integer.MAX_VALUE, SessionEventNames.SESSION_LOST));
						}
						
					}
				}
			} catch (Exception e) {
				logger.error("SocketMonitoring error.", e);
			}
	}
	
	@Override
	public void doStop() {
		try {
			for(SelectionKey key : this.selector.keys()){
				key.channel().close();
				key.cancel();
			}
			
			this.selector.close();			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void doInit() throws ServiceException {}

	protected boolean register(Session session) {
		
		try {
			if(this.selector == null){
				this.selector = Selector.open();
			}
			
			SocketTransport2 socketTransport = (SocketTransport2)session.getTransport();
			SocketChannel socketChannel = socketTransport.getChannel();
			SelectionKey key = socketChannel.register(this.selector, SelectionKey.OP_READ);
			key.attach(session);
			session.setAttribute(SocketSessionAttributeKeys.KEY_SELECTIONKEY, key);
			
			return true;
		} catch (IOException e) {
			logger.error("Can not register session. "+session, e);
		}
		return false;
	}
	
	protected boolean remove(DefaultSession session) {
		if(!(session instanceof DefaultSession)){
			return false;
		}
		
		if(this.selector == null){
			return false;
		}
		
		SocketTransport2 socketTransport = (SocketTransport2)session.getTransport();
		SocketChannel socketChannel = socketTransport.getChannel();
		SelectionKey key = socketChannel.keyFor(this.selector);
		if(key != null)
			key.cancel();
		return true;
	}
	
	protected Session createSession(SocketChannel socketChannel) throws IOException{
		socketChannel.configureBlocking(false);
		Session session = this.sessionFactory.newSession(socketChannel);
		
		session.addSessionListener(new SessionStatusListener(){
			@Override
			public void onSessionCreated(Session session) {}

			@Override
			public void onSessionInitiated(Session session) {}

			@Override
			public void onSessionSuspended(Session session) {}

			@Override
			public void onSessionActivated(Session session) {}

			@Override
			public void onSessionClosed(Session session) {
				if(!(session instanceof DefaultSession)){
					return;
				}
				remove((DefaultSession)session);
				
			}});
		
		register(session);
		logger.info("Session "+session.getSessionId()+" created! ");
		
		return session;
	}

}
