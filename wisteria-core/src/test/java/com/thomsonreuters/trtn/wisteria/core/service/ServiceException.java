
package com.thomsonreuters.trtn.wisteria.core.service;

public class ServiceException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = -7580526690346579776L;

	public ServiceException() {
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }
}
