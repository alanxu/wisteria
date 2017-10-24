package me.alanx.wisteria.core.reactor;

public class PassiveSubscription implements Subscription {

	public static PassiveSubscription INSTANCE = new PassiveSubscription();
	
	private PassiveSubscription(){}
	
	@Override
	public void request(long timeout) {
		throw new IllegalStateException("This subscription is in");
	}

	@Override
	public boolean cancel() {
		return false;
	}

	@Override
	public SubscribeMode mode() {
		return SubscribeMode.PASSIVE;
	}

}
