package com.thomsonreuters.trtn.wisteria.core.session;

import com.thomsonreuters.trtn.wisteria.core.operation.Operation;
import com.thomsonreuters.trtn.wisteria.core.operation.OperationException;

public abstract class SessionOperation implements Operation{

	private final Session session;	
	
	public SessionOperation(Session session) {
		super();
		this.session = session;
	}

	@Override
	public void execute() throws OperationException {
		try {
			doOperation(this.session);
		} catch (Throwable t) {
			throw new OperationException(t);
		}
		
	}
	
	protected abstract void doOperation(Session session);
	
}
