package com.thomsonreuters.trtn.wisteria.core.session;

public enum SessionStatus {
	NEW, //Session is newly created, it should be waiting for initiate.
	INITIATED, //Session is initiated with variety of specific configurations. E.g. filters, listeners
	PENDING, //Session is initiated successfully, but is waiting for something to be ready. E.g. waiting for other connections or other resources.
	READY, //Session has everything it needs and can work now.
	CLOSED //Session is closed, all the resources has been released.
}
