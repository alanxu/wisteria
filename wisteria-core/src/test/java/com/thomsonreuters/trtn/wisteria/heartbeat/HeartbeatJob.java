package com.thomsonreuters.trtn.wisteria.heartbeat;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thomsonreuters.trtn.wisteria.conf.SessionConfigurationKeys;
import com.thomsonreuters.trtn.wisteria.core.job.AbstractJob;
import com.thomsonreuters.trtn.wisteria.core.operation.Operation;
import com.thomsonreuters.trtn.wisteria.core.operation.OperationProvider;
import com.thomsonreuters.trtn.wisteria.core.session.Session;

public class HeartbeatJob extends AbstractJob {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	private Session session;
	private BlockingQueue<Operation> workQueue;
	private OperationProvider operationProvider;
	
	public HeartbeatJob(Session session, BlockingQueue<Operation> workQueue, ScheduledExecutorService scheduler, long initialDelay,
			long delay, TimeUnit unit) {
		super(scheduler, initialDelay, delay, unit);
		this.session = session;
		this.operationProvider = this.session.getConfiguration().getProperty(SessionConfigurationKeys.HEARTBEAT_OPERATION_PROVIDER);
		if(this.operationProvider == null){
			throw new IllegalArgumentException("No HEARTBEAT_OPERATION_PROVIDER configured. ");
		}
		
		this.workQueue = workQueue;
	}

	@Override
	public void run() {
		try {
			this.workQueue.put(operationProvider.newOperation(session));
		} catch (InterruptedException e) {
			logger.error("Error submit heartbeat operation for session: " + session.getSessionId());
		}		
	}
	


}
