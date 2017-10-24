package com.thomsonreuters.trtn.wisteria.core.transport;

public class TransportException extends RuntimeException{


    /**
	 * 
	 */
	private static final long serialVersionUID = 3323315152191171335L;

	public TransportException() {
        super();
    }

    public TransportException(String message) {
        super(message);
    }

    public TransportException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransportException(Throwable cause) {
        super(cause);
    }
	
}
