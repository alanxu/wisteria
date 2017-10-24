package me.alanx.wisteria.core.service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.alanx.wisteria.config.Configuration;
import me.alanx.wisteria.core.protocol.HeartbeatMessage;
import me.alanx.wisteria.core.transport.BasicProtocoledTransport;

public class HeartbeatService extends ScheduledService {
	
	private final BasicProtocoledTransport transport;
	
	
	
	private final Logger log = LoggerFactory.getLogger(getClass());

	public HeartbeatService(ScheduledExecutorService executor, BasicProtocoledTransport transport) {
		super(executor);
		this.transport = transport;
	}

	@Override
	protected ScheduledFuture<?> schedule(ScheduledExecutorService executor, Runnable task) {
		long timeout = Configuration.INSTANCE.getLong(Configuration.KEY_SESSION_TIMEOUT_IN_MILLIS);
		long delay = timeout / 2;
		
		return this.executor.scheduleWithFixedDelay(this, 0, delay, TimeUnit.MILLISECONDS);
	}


	
	@Override
	public void run() {
		log.trace("HeartbeatService run");
		this.transport.write(new HeartbeatMessage());
		
	}

}
