package com.thomsonreuters.trtn.wisteria.core.session;

public class SessionOperationException extends Exception{


    /**
	 * 
	 */
	private static final long serialVersionUID = -2318807601378032056L;

	public SessionOperationException() {
        super();
    }

    public SessionOperationException(String message) {
        super(message);
    }

    public SessionOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SessionOperationException(Throwable cause) {
        super(cause);
    }
	
}
