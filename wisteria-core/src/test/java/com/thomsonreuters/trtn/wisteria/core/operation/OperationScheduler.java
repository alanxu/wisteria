package com.thomsonreuters.trtn.wisteria.core.operation;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface OperationScheduler {
	ScheduledFuture<Void> scheduleWithFixedDelay(Operation operation,
			long initialDelay, long delay, TimeUnit unit);

	ScheduledFuture<Void> scheduleAtFixedRate(Operation operation,
			long initialDelay, long delay, TimeUnit unit);
}
