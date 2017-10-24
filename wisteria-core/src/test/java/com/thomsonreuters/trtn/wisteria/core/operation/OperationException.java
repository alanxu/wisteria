package com.thomsonreuters.trtn.wisteria.core.operation;

public class OperationException extends Exception{



	/**
	 * 
	 */
	private static final long serialVersionUID = -3898331961857392588L;

	public OperationException() {
        super();
    }

    public OperationException(String message) {
        super(message);
    }

    public OperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public OperationException(Throwable cause) {
        super(cause);
    }
	
}
