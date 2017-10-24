package com.thomsonreuters.trtn.wisteria.core.job;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public abstract class AbstractJob implements Job {
	private final ScheduledExecutorService scheduler;
	private long initialDelay;
	private long delay;
	private TimeUnit unit;
	private ScheduledFuture<?> future;

	public AbstractJob(ScheduledExecutorService scheduler) {
		this(scheduler, 0, -1, null);
	}

	public AbstractJob(ScheduledExecutorService scheduler,
			long initialDelay, long delay, TimeUnit unit) {
		super();
		if(initialDelay < 0){
			throw new IllegalArgumentException("initialDelay should not less then 0. ");
		}
		if(delay >= 0 && unit == null){
			throw new IllegalArgumentException("unit should not be null. ");
		}

		this.scheduler = scheduler;
		this.initialDelay = initialDelay;
		this.delay = delay;
		this.unit = unit;
	}

	@Override
	public boolean stop(boolean mayInterruptIfRunning) {
		return this.future.cancel(mayInterruptIfRunning);
	}

	@Override
	public boolean isStopped() {
		if(this.future == null)
			return false;
		return this.future.isDone();
	}	

	@Override
	public boolean isCancelled() {
		if(this.future == null)
			return false;
		return this.future.isCancelled();
	}

	@Override
	public void start() {
		if(future == null || future.isCancelled() || future.isDone()){
			if(this.delay < 0){
				future = this.scheduler.schedule(this, initialDelay, unit);
			}else{
				future = this.scheduler.scheduleWithFixedDelay(this, initialDelay, delay, unit);
			}
		}
	}
	
}
