package me.alanx.wisteria.core.socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.alanx.wisteria.core.concurrent.CompletionFuture;
import me.alanx.wisteria.core.concurrent.FutureWrapper;
import me.alanx.wisteria.core.concurrent.SimpleReturnFuture;
import me.alanx.wisteria.core.protocol.Packet;
import me.alanx.wisteria.core.reactor.PassiveSubscription;
import me.alanx.wisteria.core.reactor.Publisher;
import me.alanx.wisteria.core.reactor.SubscribeMode;
import me.alanx.wisteria.core.reactor.Subscriber;
import me.alanx.wisteria.core.session.Session;
import me.alanx.wisteria.core.session.SessionListener;
import me.alanx.wisteria.core.session.SessionManager;
import me.alanx.wisteria.core.transport.IoTransport;
import me.alanx.wisteria.core.transport.Transport;
import me.alanx.wisteria.core.transport.TransportListener;

public class AsyncSocketTransport implements IoTransport, TransportListener<ByteBuffer> {

	protected AsynchronousSocketChannel channel;

	private final Queue<ByteBuffer> outcomingMessageQ = new ConcurrentLinkedQueue<ByteBuffer>();

	protected List<TransportListener<ByteBuffer>> transportListeners = new ArrayList<>();

	//private SessionListener<ByteBuffer> sessionListener;
	
	private SubscribeMode readMode = SubscribeMode.PASSIVE;
	
	private Session session = null;
	
	private Subscriber<? super Packet> subscriber;
	

	private final Logger log = LoggerFactory.getLogger(getClass());

	/*
	 * This Semaphore is used to control there is only one write operation on
	 * the AsynchronousSocketChannel at a time.
	 * 
	 * Multiple write call to AsynchronousSocketChannel will cause
	 * WritePendingException
	 */
	private final Semaphore permit = new Semaphore(1);

	public AsyncSocketTransport(AsynchronousSocketChannel channel) {
		this.channel = channel;
		
		this.transportListeners.add(this);
	}

	@Override
	public Future<Integer> write(ByteBuffer bb) {

		/*
		 * First thing, add the buffer in the Q to prevent all writes completed
		 * before the this buffer added to the empty Q.
		 */
		this.outcomingMessageQ.offer(bb);

		/*
		 * Read the Q and send the packet util the Q is empty.
		 * 
		 * This method will for ever called after the offer call on the Q to
		 * make sure write will be triggered if the Q isn't empty.
		 */
		return sendOne();

	}

	/**
	 * 
	 * @return true if there was a packet being sent, false if nothing sent
	 */
	private final Future<Integer> sendOne() {

		log.trace("Try acquire permit. ");

		boolean sent = false;

		CompletionFuture<Integer, Semaphore> f = null;

		/*
		 * If no write operation going on.
		 */
		if (this.permit.tryAcquire()) {

			log.trace("Get permit. ");

			/*
			 * If the Q is empty at the time, go ahead and and call write
			 * directly. After the write completed, check the Q again and write
			 * the pending messages if any. When there's no message in the Q at
			 * the time, release the permit.
			 */
			if (this.outcomingMessageQ.size() > 0) {

				ByteBuffer bb = this.outcomingMessageQ.poll();

				if (bb != null) {

					/*
					 * Create a Future and also a CompletionHandler as a return
					 * the the caller of the method.
					 */
					f = new CompletionFuture<Integer, Semaphore>() {

						private Throwable t;

						private boolean isDone;

						private int v;

						private final CountDownLatch latch = new CountDownLatch(1);

						@Override
						public void completed(Integer result, Semaphore permit) {

							log.trace("Packet sent. writes: {}", result);

							/*
							 * Release the permit as soon as write completed, so
							 * that the current thread can fire another
							 * sendOne() and possibly acquire the permit to
							 * write next packet.
							 */
							permit.release();

							log.trace("Permit released");

							this.v = result;

							this.isDone = true;

							this.latch.countDown();

							/*
							 * Another sendOne() will be fired on completion of
							 * the previous one. But this call is not garantee
							 * to send packet, because it need to compete with
							 * other threads calling the write method of
							 * transport. If this thread doesn't success in the
							 * competetion, it doesn't matter. The write will be
							 * triggered by the winner.
							 * 
							 * There is no possibility that any packet will
							 * remain in the Q because of the race condition
							 * (all thread thinks the Q is empty while another
							 * thread added a packet to the Q). Because the last
							 * winner will call the send recursively utill the Q
							 * is empty.
							 */
							sendOne();

						}

						@Override
						public void failed(Throwable exc, Semaphore attachment) {

							log.error("Failed sending packet. err: {}", exc);

							// permit need to be released anyway.
							permit.release();

							this.t = exc;

							this.latch.countDown();
						}

						@Override
						public boolean cancel(boolean mayInterruptIfRunning) {
							return false;
						}

						@Override
						public boolean isCancelled() {
							return !this.isDone;
						}

						@Override
						public boolean isDone() {
							return this.isDone;
						}

						@Override
						public Integer get() throws InterruptedException, ExecutionException {

							this.latch.await();

							if (this.t != null) {
								throw new ExecutionException(t);
							}

							return this.v;
						}

						@Override
						public Integer get(long timeout, TimeUnit unit)
								throws InterruptedException, ExecutionException, TimeoutException {

							this.latch.await();

							return get();
						}
					};

					/*
					 * = Call write method =
					 */
					this.channel.write(bb, permit, f);

					// Indicate if need to release the permit in this method.
					sent = true;

				}
			}
			
			/*
			 * If no write fired, release the permit immediately.
			 */
			if (!sent) {

				log.trace("Permit released immediately. ");

				this.permit.release();

			}
		}


		if (!sent) {

			return new SimpleReturnFuture<Integer>(0);

		} else {

			return f;

		}

	}

	@Override
	public Future<ByteBuffer> read() {
		
		if (this.readMode == SubscribeMode.PASSIVE) {
			throw new IllegalStateException("The read mode is set to passitive. ");
		}
		
		ByteBuffer buffer = ByteBuffer.allocate(32 * 1024);
		Future<Integer> f = this.channel.read(buffer);

		Future<ByteBuffer> ff = new FutureWrapper<ByteBuffer, Integer>(f) {

			@Override
			public ByteBuffer convert(Integer oldResult, Future<Integer> wrappedFuture) throws ExecutionException {
				if (oldResult < 0) {
					throw new ExecutionException("", null);
				}
				return buffer;
			}

		};

		return ff;
	}

	@Override
	public void close() throws IOException {
		this.channel.close();
		
		this.transportListeners.forEach(l -> l.onDisconnected());
	}

	private void startRead(AsynchronousSocketChannel asynchronousSocketChannel) {
		final ByteBuffer buf = ByteBuffer.allocate(4 * 1024);

		// read message from the other end
		asynchronousSocketChannel.read(buf, asynchronousSocketChannel,
				new CompletionHandler<Integer, AsynchronousSocketChannel>() {

					/**
					 * some message is read from client, this callback will be
					 * called
					 */
					@Override
					public void completed(Integer result, AsynchronousSocketChannel channel) {

						//AsyncSocketTransport.this.transportListeners.forEach(l -> l.onReceived(buf));
						subscriber.onNext(new Packet(session, buf));

						// start to read next message again
						startRead(channel);
					}

					@Override
					public void failed(Throwable exc, AsynchronousSocketChannel channel) {
						System.out.println("fail to read message from client");
					}
				});
	}

	


	/**
	 * @return the transportListeners
	 */
	public List<TransportListener<ByteBuffer>> getTransportListeners() {
		return transportListeners;
	}
	
	@Override
	public AsyncSocketTransport listenedBy(TransportListener listener) {
		this.transportListeners.add(listener);
		return this;
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected() {

		
		if(this.readMode == SubscribeMode.PASSIVE) {
			if(this.transportListeners == null) {
				throw new IllegalStateException("The SessinListener is not set. Call listenedBy() to set it before connect. ");
			}
			
			startRead(this.channel);
		}
		
	}
	
	public static AsyncSocketTransport open(AsynchronousSocketChannel channel) {
		return new AsyncSocketTransport(channel);
	}

	@Override
	public AsyncSocketTransport withReadMode(SubscribeMode mode) {
		this.readMode = mode;
		return this;
	}


	@Override
	public void onReceived(ByteBuffer data) {
		if (this.session != null)
			this.subscriber.onNext(new Packet(session, data));
	}

	@Override
	public void onSent(ByteBuffer data) {}

	@Override
	public void subscribe(Subscriber<? super Packet> s) {
		this.subscriber = s;
		s.onSubscribe(PassiveSubscription.INSTANCE);
	}

	@Override
	public void setSession(Session session) {
		this.session = session;
	}

	@Override
	public Session getSession(Session session) {
		return this.session;
	}

	
}

