package me.alanx.wisteria.core.service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import me.alanx.wisteria.config.Configuration;
import me.alanx.wisteria.core.protocol.HeartbeatMessage;

public abstract class ScheduledService implements Service, Runnable {

	protected final ScheduledExecutorService executor;
	private ScheduledFuture<?> future;
	
	public ScheduledService(ScheduledExecutorService executor) {
		super();
		this.executor = executor;
	}
	
	@Override
	public void start() {
		this.future = schedule(executor, this);
		
	}
	
	@Override
	public void stop() {
		if(this.future != null)
			this.future.cancel(false);
		else
			throw new IllegalStateException("The service is not scheduled. ");
		
	}
	
	protected abstract ScheduledFuture<?> schedule(ScheduledExecutorService executor, Runnable task);

}
