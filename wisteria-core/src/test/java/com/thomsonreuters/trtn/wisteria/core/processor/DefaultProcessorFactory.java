package com.thomsonreuters.trtn.wisteria.core.processor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import com.thomsonreuters.trtn.wisteria.core.operation.Operation;

public class DefaultProcessorFactory implements ProcessorFactory{
	
	private final static AtomicInteger sequence = new AtomicInteger();
	private final static String NAME_BASE = "DefaultProcessor-";


	@Override
	public Processor newProcessor(BlockingQueue<Operation> workQueue, ExecutorService executor) {
		String name = NAME_BASE + sequence.incrementAndGet();
		return newProcessor(name, workQueue, executor);
	}

	@Override
	public Processor newProcessor(String name, BlockingQueue<Operation> workQueue, ExecutorService executor) {
		DefaultProcessor instance = new DefaultProcessor(name, executor, workQueue);
		return instance;
	}

}
