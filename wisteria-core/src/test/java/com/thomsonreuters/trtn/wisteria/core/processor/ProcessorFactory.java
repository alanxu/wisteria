package com.thomsonreuters.trtn.wisteria.core.processor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import com.thomsonreuters.trtn.wisteria.core.operation.Operation;

public interface ProcessorFactory {
	Processor newProcessor(BlockingQueue<Operation> workQueue, ExecutorService executor);
	Processor newProcessor(String name, BlockingQueue<Operation> workQueue, ExecutorService executor);
}
