package com.thomsonreuters.trtn.wisteria.core.job;


public interface Job extends Runnable/* extends RunnableFuture<Void>*/ {
	void start();
	
	boolean stop(boolean mayInterruptIfRunning);
	
	boolean isStopped();
	
	boolean isCancelled();
}
