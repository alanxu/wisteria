package com.thomsonreuters.trtn.wisteria.core.session;

public class SessionException extends RuntimeException{

	private static final long serialVersionUID = -7505597171155617499L;

    public SessionException() {
        super();
    }

    public SessionException(String message) {
        super(message);
    }

    public SessionException(String message, Throwable cause) {
        super(message, cause);
    }

    public SessionException(Throwable cause) {
        super(cause);
    }
	
}
