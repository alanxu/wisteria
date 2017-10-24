package com.thomsonreuters.trtn.wisteria.core.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public abstract class AbstractService implements Service {
	private Logger logger = LoggerFactory.getLogger(AbstractService.class);
	
	protected static final AtomicInteger id = new AtomicInteger();
	protected final String threadName;
	private final ExecutorService executor;
	protected final boolean createdExecutor;
	private int threadCount;
	
	protected volatile boolean active = true;
	protected volatile boolean running;
	protected volatile boolean stopping;
	protected volatile boolean stopped;
	
	//private AtomicReference<ServiceWorker> worker = new AtomicReference<ServiceWorker>();	
    
    public AbstractService(String name, ExecutorService executor){
    	this(name, executor, 1);
    }
    
    public AbstractService(String name, ExecutorService executor, int threadCount){
    	if (executor == null) {
            this.executor = Executors.newCachedThreadPool();
            createdExecutor = true;
        } else {
            this.executor = executor;
            createdExecutor = false;
        }
    	this.threadCount =threadCount; 
        threadName = name == null ? getClass().getSimpleName() + '-' + id.incrementAndGet() : name; 
    }
    
    public final boolean isActive() {
        return !this.isStopped() && !this.isStopping() && this.active;
    }
    
    public final boolean isRunning() {
        return running;
    }
    
    public final boolean isStopping() {
        return stopping;
    }

    public final boolean isStopped() {
        return stopped;
    }


	public final void start(){
		try {
			if(running){
				return;
			}else if(stopping){
				throw new IllegalStateException("Service is being stopped. ");
			}
			
			doInit();
			execute();
			active = true;
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public final void stop(){
		stopping = true;
		try {
			doStop();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		running = false;
		active = false;
		stopping = false;
		stopped = true;	
	}

    protected final void execute(){
    	for(int i=0; i<this.threadCount; i++){
    		executor.submit(new ServiceWorker());
    	}
    	running = true;
    }
    
    protected abstract void doInit() throws ServiceException;
    protected abstract void doService() throws ServiceException;
    protected abstract void doStop() throws ServiceException;
    
    protected void onException(Exception exception){};
    protected void onFinish(){};
    
    private class ServiceWorker implements Runnable{
		@Override
		public void run() {
			try {
				for(;;){
					if(active && !Thread.interrupted()){
						doService();
					}
				}
			} catch (ServiceException e) {
				onException(e);
			} catch (Exception e){
				logger.error("Service terminated due to a exception!", e);
				onException(e);
			} finally {
				//worker.set(null);
				logger.warn(threadName + " stopped!");
				running = false;
				onFinish();
			}
		}
    	
    }
    
    
}
