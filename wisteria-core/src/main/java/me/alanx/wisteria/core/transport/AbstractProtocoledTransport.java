package me.alanx.wisteria.core.transport;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.alanx.wisteria.core.concurrent.FutureWrapper;
import me.alanx.wisteria.core.protocol.Message;
import me.alanx.wisteria.core.protocol.Packet;
import me.alanx.wisteria.core.protocol.Protocol;
import me.alanx.wisteria.core.reactor.PassiveSubscription;
import me.alanx.wisteria.core.reactor.Publisher;
import me.alanx.wisteria.core.reactor.SubscribeMode;
import me.alanx.wisteria.core.reactor.Subscriber;
import me.alanx.wisteria.core.reactor.Subscription;
import me.alanx.wisteria.core.session.Session;

public abstract class AbstractProtocoledTransport implements ProtocoledTransport, TransportListener<ByteBuffer>, Subscriber<Packet>, Publisher<Message>{

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private final IoTransport ioTransport;
	
	private final Queue<Message> messageBuffer = new ConcurrentLinkedQueue<>();
	
	protected List<TransportListener<Message>> transportListeners = new ArrayList<>();
	
	private Protocol<Message> protocol;
	
	private Session session;
	
	private Subscriber<? super Message> subscriber;
	
	
	public AbstractProtocoledTransport(IoTransport socketTransport) {
		
		this.ioTransport = socketTransport;
		this.ioTransport.listenedBy(this);
		
		this.ioTransport.subscribe(this);
		
	}


	@Override
	public Future<Integer> write(Message data) {
		
		ByteBuffer buf = decode(data);
		
		buf.flip();
		
		return this.ioTransport.write(buf);
	}


	@Override
	public Future<Message> read() {
		Future<ByteBuffer> f = this.ioTransport.read();
		return new FutureWrapper<Message, ByteBuffer>(f) {

			@Override
			public Message convert(ByteBuffer oldResult, Future<ByteBuffer> wrappedFuture) throws ExecutionException {
				oldResult.flip();
				synchronized(AbstractProtocoledTransport.this) {
					Message[] all = encode(oldResult);
					messageBuffer.addAll(Arrays.asList(all));
					return messageBuffer.poll();
				}
			}
		};
		
	}

	
	public AbstractProtocoledTransport protocol(Protocol<Message> protocol) {
		this.protocol = protocol;
		return this;
	}

	@Override
	public void close() throws IOException {
		this.ioTransport.close();
	}

	@Override
	public AbstractProtocoledTransport withReadMode(SubscribeMode mode) {
		this.ioTransport.withReadMode(mode);
		return this;
	}


	@Override
	public AbstractProtocoledTransport listenedBy(TransportListener<Message> listener) {
		this.transportListeners.add(listener);
		return this;
	}
	
	/**
	 * @return the transportListeners
	 */
	public List<TransportListener<Message>> getTransportListeners() {
		return transportListeners;
	}


	/**
	 * @return the protocol
	 */
	public Protocol<Message> getProtocol() {
		return protocol;
	}


	/**
	 * @return the ioTransport
	 */
	public IoTransport getIoTransport() {
		return ioTransport;
	}


	@Override
	public void onConnected() {}


	@Override
	public void onDisconnected() {}


	@Override
	public void onReceived(ByteBuffer data) {
		data.flip();
		for(Message m : encode(data)) {
			this.transportListeners.forEach(l -> l.onReceived(m));
		}
	}


	
	/**
	 * @return the session
	 */
	public Session getSession() {
		return session;
	}


	/**
	 * @param session the session to set
	 */
	public void setSession(Session session) {
		this.session = session;
	}


	@Override
	public void onSent(ByteBuffer data) {}

	protected abstract Message[] encode(ByteBuffer buf);
	
	protected abstract ByteBuffer decode(Message msg);


	/* (non-Javadoc)
	 * @see me.alanx.wisteria.core.reactor.Subscriber#onSubscribe(me.alanx.wisteria.core.reactor.Subscription)
	 */
	@Override
	public void onSubscribe(Subscription subscription) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see me.alanx.wisteria.core.reactor.Subscriber#onNext(java.lang.Object)
	 */
	@Override
	public void onNext(Packet t) {
		ByteBuffer buf = t.getBytes();
		Message[] msgs = encode(buf);
		
		if (msgs != null) {
			for (Message m : msgs) {
				if (m != null) {
					this.subscriber.onNext(m);
				}
			}
		}
		
	}


	/* (non-Javadoc)
	 * @see me.alanx.wisteria.core.reactor.Subscriber#onError(java.lang.Throwable)
	 */
	@Override
	public void onError(Throwable t) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see me.alanx.wisteria.core.reactor.Subscriber#onCompletion()
	 */
	@Override
	public void onCompletion() {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see me.alanx.wisteria.core.reactor.Publisher#subscribe(me.alanx.wisteria.core.reactor.Subscriber)
	 */
	@Override
	public void subscribe(Subscriber<? super Message> s) {
		this.subscriber = s;
		s.onSubscribe(PassiveSubscription.INSTANCE);
		
	}
	
	public AbstractProtocoledTransport subscribedBy(Subscriber<? super Message> s) {
		subscribe(s);
		return this;
	}
	
	

}
