package com.thomsonreuters.trtn.wisteria.core.operation;

import com.thomsonreuters.trtn.wisteria.core.session.Session;


public interface OperationProvider {
	Operation newOperation(Session session);
}
