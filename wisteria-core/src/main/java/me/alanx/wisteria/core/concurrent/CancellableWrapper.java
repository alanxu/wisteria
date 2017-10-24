package me.alanx.wisteria.core.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class CancellableWrapper<V> {
	private final V v;
	private AtomicBoolean cancelled = new AtomicBoolean(false);

	public CancellableWrapper(V v) {
		super();
		this.v = v;
	}

	public boolean cancel() {

		return this.cancelled.compareAndSet(false, true);

	}
	
	public boolean isCancelled() {
		
		return this.cancelled.get();
		
	}
	
	public V get() {
		return v;
	}
}



class InternalFuture<T> implements Future<T> {

	T result;
	volatile boolean completed;
	volatile boolean canclled;

	@Override
	public synchronized boolean cancel(boolean mayInterruptIfRunning) {

		if (this.completed)
			return false;

		this.canclled = true;
		// this.completed = true;

		notifyAll();

		return true;

	}

	// Read method can be without synchronization
	// as long as the variable it reads is volatile
	@Override
	public boolean isCancelled() {
		return canclled;
	}

	@Override
	public boolean isDone() {
		return this.completed;
	}

	@Override
	public synchronized T get() throws InterruptedException, ExecutionException {
		while (!this.completed || !this.canclled) {
			wait();
		}

		return getResult();
	}

	@Override
	public synchronized T get(long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		// Args.notNull(unit, "Time unit");
		final long msecs = unit.toMillis(timeout);
		final long startTime = (msecs <= 0) ? 0 : System.currentTimeMillis();
		long waitTime = msecs;
		if (this.completed) {
			return getResult();
		} else if (waitTime <= 0) {
			throw new TimeoutException();
		} else {
			for (;;) {
				wait(waitTime);
				if (this.completed) {
					return getResult();
				} else {
					waitTime = msecs - (System.currentTimeMillis() - startTime);
					if (waitTime <= 0) {
						throw new TimeoutException();
					}
				}
			}
		}
	}

	public void completed(T result) {
		synchronized (this) {
			this.result = result;
		}
	}

	private T getResult() {
		if (this.result == null && this.completed) {
			try {
				throw new ExecutionException("Operation is aborted. ", null);
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return this.result;
	}

}