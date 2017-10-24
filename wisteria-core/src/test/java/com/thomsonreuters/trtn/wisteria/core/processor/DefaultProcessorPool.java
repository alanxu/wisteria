package com.thomsonreuters.trtn.wisteria.core.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.thomsonreuters.trtn.wisteria.core.operation.Operation;
import com.thomsonreuters.trtn.wisteria.core.operation.OperationExecutor;
import com.thomsonreuters.trtn.wisteria.core.session.Session;

public class DefaultProcessorPool implements Processor, OperationExecutor{

	private final List<Processor> processors;
	private ExecutorService executor;
	private final int poolSize;
	private final BlockingQueue<Operation> workQueue;
	
	public DefaultProcessorPool(BlockingQueue<Operation> workQueue, ProcessorFactory processorFactory){
		this(Runtime.getRuntime().availableProcessors()+1, workQueue, processorFactory);
	}
	
	public DefaultProcessorPool(int poolSize, BlockingQueue<Operation> workQueue, ProcessorFactory processorFactory){
		this.poolSize = poolSize;
		this.processors = Collections.synchronizedList(new ArrayList<Processor>(this.poolSize));
		this.executor = Executors.newCachedThreadPool();
		this.workQueue = workQueue;
		for(int i=0; i<this.poolSize; i++){
			this.processors.add(processorFactory.newProcessor(workQueue, executor));
		}
	}

	@Override
	public synchronized boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public synchronized boolean isStopping() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public synchronized boolean isStopped() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public synchronized void start() {
		for(Processor p : this.processors){
			p.start();
		}		
	}

	@Override
	public synchronized void stop() {
		for(Processor p : this.processors){
			p.stop();
		}
	}

	@Override
	public Future<Void> submit(Operation operation) {
		this.workQueue.add(operation);
		//TODO
		return null;
	}	
	


}
