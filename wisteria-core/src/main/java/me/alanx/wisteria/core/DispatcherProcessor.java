package me.alanx.wisteria.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.alanx.wisteria.core.protocol.Message;
import me.alanx.wisteria.core.reactor.Processor;
import me.alanx.wisteria.core.reactor.Subscriber;
import me.alanx.wisteria.core.reactor.Subscription;

public class DispatcherProcessor implements Processor<Message, Message> {
	
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void subscribe(Subscriber<? super Message> s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <A> void onSubscribe(Subscription subscription) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onNext(Message m) {
		log.debug("Client: message received. Message: {}", m);		
		
		m.getSession().getTransport().write(m);
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
