package me.alanx.wisteria.core.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class FutureWrapper<N, O> implements Future<N> {
	
	private final Future<O> wrappedFuture;

	public FutureWrapper(Future<O> wrappedFuture) {
		super();
		this.wrappedFuture = wrappedFuture;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return wrappedFuture.cancel(mayInterruptIfRunning);
	}

	@Override
	public boolean isCancelled() {
		return wrappedFuture.isCancelled();
	}

	@Override
	public boolean isDone() {
		return wrappedFuture.isDone();
	}
	
	@Override
	public N get() throws InterruptedException, ExecutionException {
		
		O o = this.wrappedFuture.get();
		
		N n = convert(o, this.wrappedFuture);
		
		return n;
	}

	@Override
	public N get(long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		
		O o = this.wrappedFuture.get(timeout, unit);
		
		N n = convert(o, this.wrappedFuture);
		
		return n;
	}
	
	public abstract N convert(O oldResult, Future<O> wrappedFuture) throws ExecutionException;
}
