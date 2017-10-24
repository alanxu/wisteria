package me.alanx.wisteria.core.protocol;

public class HandshakeResponseMessage extends Message{

	private final boolean success;

	public HandshakeResponseMessage(boolean success) {
		super();
		this.success = success;
	}

	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}
}
