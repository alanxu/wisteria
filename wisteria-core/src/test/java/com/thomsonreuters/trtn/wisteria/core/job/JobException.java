
package com.thomsonreuters.trtn.wisteria.core.job;

public class JobException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = -7580526690346579776L;

	public JobException() {
    }

    public JobException(String message) {
        super(message);
    }

    public JobException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobException(Throwable cause) {
        super(cause);
    }
}
