package com.thomsonreuters.trtn.wisteria.core.session;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.thomsonreuters.trtn.wisteria.conf.Profile;
import com.thomsonreuters.trtn.wisteria.core.event.Event;
import com.thomsonreuters.trtn.wisteria.core.event.EventHandler;
import com.thomsonreuters.trtn.wisteria.core.event.EventHandlerDispatcher;
import com.thomsonreuters.trtn.wisteria.core.processor.Processor;
import com.thomsonreuters.trtn.wisteria.core.session.listener.SessionStatusListener;
import com.thomsonreuters.trtn.wisteria.core.transport.Transport;
import com.thomsonreuters.trtn.wisteria.core.transport.Transport2;

public class DefaultSession extends AbstractSession{
	private static final long serialVersionUID = 8094973741575730361L;

	private AtomicBoolean needProcess;

	private EventHandler eventHandler;

	private Processor processor;

	private PriorityBlockingQueue<Event> eventQueue;
	private PriorityQueue<WriteRequest> writeQueue;
	private int selectMode = 0;
	@SuppressWarnings("rawtypes")
	private Transport2 transport;
	

	
	public DefaultSession(String sessionId, @SuppressWarnings("rawtypes") Transport2 transport){
		super(sessionId);
		this.transport = transport;
		this.transport.setSession(this);
	}
	
	public void init(Profile configuration){
		super.init(configuration);
		
		if(this.sessionConfiguation.getEventHandlerMapping() != null)
			this.eventHandler = new EventHandlerDispatcher(this.sessionConfiguation.getEventHandlerMapping());
		
		this.writeQueue = new PriorityQueue<WriteRequest>(sessionConfiguation.getWriteQueueInitialSize(), new java.util.Comparator<WriteRequest>(){
			@Override
			public int compare(WriteRequest o1, WriteRequest o2) {
				return o1.getPriority() - o2.getPriority();
			}});
		
		this.eventQueue = new PriorityBlockingQueue<Event>(sessionConfiguation.getEventQueueInitialSize(), new java.util.Comparator<Event>(){
			@Override
			public int compare(Event o1, Event o2) {
				return o1.getPriority() - o2.getPriority();
			}});
		
		this.setStatus(SessionStatus.INITIATED);
	}

	@Override
	public boolean isClientConnected() {
		//TODO
		throw new UnsupportedOperationException();
	}

	@Override
	public void open() throws SessionException {}

	@Override
	public void close() throws SessionException {
		this.setStatus(SessionStatus.CLOSED);
		for(SessionStatusListener l : this.getSessionListeners(SessionStatusListener.class)){
			l.onSessionClosed(this);
		}
	}

	@Override
	public Processor getProcessor() {
		return this.processor;
	}

	@Override
	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

	@Override
	public void switchNeedProcess(boolean need) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean issueEvent(Event event) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean tryLock() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean tryLock(long timeout, TimeUnit unit)
			throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void lock() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unlock() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Queue<Event> getEventQueue() {
		return this.eventQueue;
	}

	@Override
	public EventHandler getEventHandler() {
		return this.eventHandler;
	}


	@Override
	public void setSelectMode(int mode) {
		this.selectMode = mode;		
	}

	@Override
	public int getSelectMode() {
		return this.selectMode;
	}

	public static class SelectMode{
		public static int SOCKET = 1;
		public static int EVENT = 2;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Transport2 getTransport() {
		return this.transport;
	}

	@Override
	public Queue<WriteRequest> getWriteQueue() {
		return this.writeQueue;
	}

	public void setEventHandler(EventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}

}
