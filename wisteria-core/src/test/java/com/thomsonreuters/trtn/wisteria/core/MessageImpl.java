package com.thomsonreuters.trtn.wisteria.core;

public class MessageImpl implements Message{

	private final String protocol;
	private final String protocolVersion;
	private final String message;
	private volatile Object attachement;
	
	public MessageImpl(String protocol, String protocolVersion, String message) {
		super();
		this.protocol = protocol;
		this.protocolVersion = protocolVersion;
		this.message = message;
	}

	@Override
	public String getProtocol() {
		return this.protocol;
	}

	@Override
	public String getProtocolVersion() {
		return this.protocolVersion;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAttachement() {
		return (T)this.attachement;
	}

	@Override
	public void setAttachement(Object attachement) {
		this.attachement = attachement;
	}

	@Override
	public String toString() {
		return "MessageImpl [protocol=" + protocol + ", protocolVersion="
				+ protocolVersion + ", message=" + message + "]";
	}
	
	

}
