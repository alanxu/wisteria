package me.alanx.wisteria.core.reactor;


public interface Subscriber<T> {
	
	/**
     * Invoked after calling {@link Publisher#subscribe(Subscriber)}.
     * <p>
     * No data will start flowing until {@link Subscription#request(long)} is invoked.
     * <p>
     * It is the responsibility of this {@link Subscriber} instance to call {@link Subscription#request(long)} whenever more data is wanted.
     * <p>
     * The {@link Publisher} will send notifications only in response to {@link Subscription#request(long)}.
     * 
     * @param s
     *            {@link Subscription} that allows requesting data via {@link Subscription#request(long)}
     */
	public <A> void onSubscribe(Subscription subscription);
	
	/**
     * Data notification sent by the {@link Publisher} the data may flow in automatically if the publisher is set to {@link SubscribeMode.ACTIVE}
     * <p>
     * Or, if the "Subscription"'s "request" method is called.
     * 
     * @param t the element signaled
     */
	public void onNext(T t);
	
	/**
     * Failed terminal state.
     * <p>
     * No further events will be sent even if {@link Subscription#request(long)} is invoked again.
     *
     * @param t the throwable signaled
     */
	public void onError(Throwable t);
	
	/**
     * Successful terminal state.
     * <p>
     * No further events will be sent even if {@link Subscription#request(long)} is invoked again.
     */
	public void onCompletion();
	
	
}
