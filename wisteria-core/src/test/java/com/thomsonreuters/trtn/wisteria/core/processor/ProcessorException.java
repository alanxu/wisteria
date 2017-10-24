
package com.thomsonreuters.trtn.wisteria.core.processor;

public class ProcessorException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1772945851111256122L;

	public ProcessorException() {
    }

    public ProcessorException(String message) {
        super(message);
    }

    public ProcessorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessorException(Throwable cause) {
        super(cause);
    }
}
