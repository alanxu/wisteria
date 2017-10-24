package me.alanx.wisteria.core;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.alanx.wisteria.core.filter.FilterChainBuilder;
import me.alanx.wisteria.core.protocol.Message;
import me.alanx.wisteria.core.protocol.Packet;
import me.alanx.wisteria.core.reactor.Processor;
import me.alanx.wisteria.core.reactor.Subscriber;
import me.alanx.wisteria.core.reactor.Subscription;

public class ProtocoledProcessor implements Processor<Packet, Message> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final FilterChainBuilder filterChainBuilder;

	private Subscription subscription;

	private final ExecutorService executor;

	Subscriber<? super Message> subscriber;

	public ProtocoledProcessor(FilterChainBuilder filterChainBuilder, ExecutorService executor) {
		super();
		this.filterChainBuilder = filterChainBuilder;
		this.executor = executor;
	}

	@Override
	public void subscribe(Subscriber<? super Message> s) {
		this.subscriber = s;
	}

	@Override
	public void onSubscribe(Subscription subscription) {
		this.subscription = subscription;
	}

	@Override
	public void onNext(Packet data) {

		this.executor.submit(new InboundProcessTask(data, this.subscriber, filterChainBuilder));

	}

	@Override
	public void onError(Throwable t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCompletion() {
		// TODO Auto-generated method stub

	}

}
