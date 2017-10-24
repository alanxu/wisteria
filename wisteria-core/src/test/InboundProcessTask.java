package me.alanx.wisteria.core.task;

import java.nio.ByteBuffer;
import java.util.concurrent.Callable;

import me.alanx.wisteria.core.filter.FilterChain;
import me.alanx.wisteria.core.protocol.Message;
import me.alanx.wisteria.core.protocol.Packet;
import me.alanx.wisteria.core.reactor.Subscriber;
import me.alanx.wisteria.core.session.Session;

public class InboundProcessTask implements Callable<Void> {

	private final Session session;
	private final ByteBuffer buf;
	private final Subscriber<? super Message> subscriber;
	
	public InboundProcessTask(Packet packet, Subscriber<? super Message> subscriber) {
		super();
		this.session = packet.getSession();
		this.buf = packet.getBytes();
		this.subscriber = subscriber;
	}

	@Override
	public Void call() throws Exception {
		FilterChain fchain = session.getFilterChain().buildFilterChain(null);
		buf.flip();
		Message[] msgs = fchain.filter(session, buf);
		if(msgs != null) {
			for(Message m : msgs) {
				m.setSession(session);
				this.subscriber.onNext(m);
			}
		}
		return null;
	}

}
