package com.thomsonreuters.trtn.wisteria.core;

public interface Message {
	public String getProtocol();
	String getProtocolVersion();
	String getMessage();
	<T> T getAttachement();
	void setAttachement(Object attachement);
}
