package me.alanx.wisteria.core.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SimpleReturnFuture<V> implements Future<V> {

	private final V returnValue;
	private boolean isDone;
	
	public SimpleReturnFuture(V returnValue) {
		this.returnValue = returnValue;
	}
	
	public SimpleReturnFuture(V returnValue, boolean isDone) {
		this(returnValue);
		this.isDone = isDone;
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
	public V get() throws InterruptedException, ExecutionException {
		return this.returnValue;
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return get();
	}
	
	
}
