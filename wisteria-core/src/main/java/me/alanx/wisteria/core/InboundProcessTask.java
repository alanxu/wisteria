package me.alanx.wisteria.core;

import java.nio.ByteBuffer;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.alanx.wisteria.core.filter.FilterChain;
import me.alanx.wisteria.core.filter.FilterChainBuilder;
import me.alanx.wisteria.core.protocol.Message;
import me.alanx.wisteria.core.protocol.Packet;
import me.alanx.wisteria.core.reactor.Subscriber;
import me.alanx.wisteria.core.session.Session;

public class InboundProcessTask implements Callable<Void> {

	private final Logger log = LoggerFactory.getLogger(InboundProcessTask.class);
	private final Packet packet;
	private final Subscriber<? super Message> subscriber;
	private final FilterChainBuilder fcb;
	
	public InboundProcessTask(Packet packet, Subscriber<? super Message> subscriber, FilterChainBuilder fcb) {
		super();
		this.packet = packet;
		this.subscriber = subscriber;
		this.fcb = fcb;
	}

	@Override
	public Void call() throws Exception {

		/*
		 * When run a taks in an executor, be careful about the exceptions.
		 * 
		 * Although for callables, you can get exceptin by Future.get(), it's 
		 * not a preferable way in a highly effective system.
		 * 
		 * UncaughtExceptionHandler worked for a single Thread, but not in
		 * ExecutorServices.
		 * 
		 * So, a more practical way is to try/catch all the exception and handle
		 * them within the call method.
		 */
		try {
			FilterChain fchain = fcb.buildFilterChain(null);
			this.packet.getBytes().flip();
			Message[] msgs = fchain.filter(this.packet);
			if(msgs != null) {
				for(Message m : msgs) {
					m.setSession(this.packet.getSession());
					this.subscriber.onNext(m);
				}
			}
		} catch (Exception e) {
			log.error("Failed processing inbound packet. ", e);
		}
		return null;
	}

}
