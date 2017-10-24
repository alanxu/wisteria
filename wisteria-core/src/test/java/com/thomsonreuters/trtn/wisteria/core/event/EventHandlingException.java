package com.thomsonreuters.trtn.wisteria.core.event;

public class EventHandlingException extends RuntimeException{

    public EventHandlingException() {
        super();
    }

    public EventHandlingException(String message) {
        super(message);
    }

    public EventHandlingException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventHandlingException(Throwable cause) {
        super(cause);
    }
	
}
