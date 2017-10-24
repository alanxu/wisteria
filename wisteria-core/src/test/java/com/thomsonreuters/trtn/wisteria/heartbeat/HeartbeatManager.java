package com.thomsonreuters.trtn.wisteria.heartbeat;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.thomsonreuters.trtn.wisteria.conf.SessionConfigurationKeys;
import com.thomsonreuters.trtn.wisteria.core.job.Job;
import com.thomsonreuters.trtn.wisteria.core.operation.Operation;
import com.thomsonreuters.trtn.wisteria.core.session.Session;
import com.thomsonreuters.trtn.wisteria.core.session.listener.SessionStatusListener;

public class HeartbeatManager implements SessionStatusListener{

	private final ScheduledExecutorService scheduler;
	private final BlockingQueue<Operation> workQueue;
	
	private final static String HEARTBEAT_JOB_KEY = "HEARTBEAT_JOB_KEY";
	
	public HeartbeatManager(ScheduledExecutorService scheduler, BlockingQueue<Operation> workQueue) {
		this.scheduler = scheduler;
		this.workQueue = workQueue;
	}

	@Override
	public void onSessionCreated(Session session) {}

	@Override
	public void onSessionInitiated(Session session) {
		startHeatbeat(session);
	}

	@Override
	public void onSessionSuspended(Session session) {
		stopHeatbeat(session);
	}

	@Override
	public void onSessionActivated(Session session) {
		startHeatbeat(session);
	}

	@Override
	public void onSessionClosed(Session session) {
		stopHeatbeat(session);
	}
	
	protected void startHeatbeat(Session session){
		Integer interval = session.getConfiguration().getProperty(SessionConfigurationKeys.HEARTBEAT_INTERVAL_IN_MILLIS);
		if(interval == null || interval < 0){
			return;
		}
		
		Job heartbeatJob = session.getAttribute(HEARTBEAT_JOB_KEY);
		
		if(heartbeatJob == null){
			heartbeatJob = new HeartbeatJob(session, workQueue, scheduler, 0, interval, TimeUnit.MILLISECONDS);
			session.setAttribute(HEARTBEAT_JOB_KEY, heartbeatJob);
		}
			
		if(!(heartbeatJob.isStopped() || heartbeatJob.isCancelled()))
			heartbeatJob.start();
		
	}
	
	protected void stopHeatbeat(Session session){
		Job heartbeatJob = session.getAttribute(HEARTBEAT_JOB_KEY);
		if(heartbeatJob != null && !heartbeatJob.isCancelled()){
			heartbeatJob.stop(true);
			session.removeAttribute(HEARTBEAT_JOB_KEY);
		}
	}

}
