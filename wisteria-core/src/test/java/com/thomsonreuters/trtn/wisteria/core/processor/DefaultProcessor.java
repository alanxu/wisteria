package com.thomsonreuters.trtn.wisteria.core.processor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thomsonreuters.trtn.wisteria.core.operation.Operation;
import com.thomsonreuters.trtn.wisteria.core.operation.OperationException;
import com.thomsonreuters.trtn.wisteria.core.service.AbstractService;
import com.thomsonreuters.trtn.wisteria.core.service.ServiceException;

public class DefaultProcessor extends AbstractService implements Processor{
	private static Logger logger = LoggerFactory.getLogger(DefaultProcessor.class);
	private BlockingQueue<Operation> workQueue;
	
	public DefaultProcessor(String name, ExecutorService executor, BlockingQueue<Operation> workQueue) {
		super(name, executor);
		this.workQueue = workQueue;
	}

	@Override
	protected void doInit() throws ServiceException {}

	@Override
	protected void doService() throws ServiceException {
			try {
				Operation operation = this.workQueue.take();
				if(operation == null){
					return;
				}
				operation.execute();
				//logger.debug("[" + Thread.currentThread().getName() + "]" + "Process session "+session.getSessionId()+". ");
				
			} catch (OperationException e) {
				logger.error("Failed processing operation! ", e);
			} catch (Exception e){
				logger.error("Unexpected error while processing operation!  ", e);
			}
	}

	@Override
	protected void doStop() throws ServiceException {}
	
	@Override
	protected void onFinish(){
		logger.info("Processor "+threadName + " stopped! ");
	};

/*	protected void process(Session session) throws ProcessorException{
		try{
			SessionOperation<Session> so = session.getSessionOperation();
			so.execute(session);
		}catch(Throwable t){
			throw new ProcessorException(t);
		}finally{
			//session.unlock();
		}
	}*/
}
