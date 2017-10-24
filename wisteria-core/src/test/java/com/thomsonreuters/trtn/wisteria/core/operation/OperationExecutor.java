package com.thomsonreuters.trtn.wisteria.core.operation;

import java.util.concurrent.Future;

public interface OperationExecutor {
	Future<Void> submit(Operation operation);
}
