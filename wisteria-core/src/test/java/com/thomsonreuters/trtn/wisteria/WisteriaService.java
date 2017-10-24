package com.thomsonreuters.trtn.wisteria;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thomsonreuters.trtn.wisteria.core.acceptor.Acceptor;
import com.thomsonreuters.trtn.wisteria.core.processor.Processor;

public class WisteriaService{

	private Logger logger = LoggerFactory.getLogger(WisteriaService.class);
	


	
	private volatile AtomicBoolean started = new AtomicBoolean(false);
	
	private Collection<Acceptor> acceptors;
	private Processor processor;

	
	

	public void setAcceptors(List<Acceptor> acceptors) {
		this.acceptors = acceptors;
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

	public void start() throws Exception {
		
		for(Acceptor acceptor : this.acceptors){
			acceptor.bind();
		}
		
		this.processor.start();
		started.set(true);
	}
	
	public void stop() throws Exception {
		for(Acceptor acceptor : this.acceptors){
			acceptor.close();
		}
		this.processor.stop();
		this.started.set(false);
	}

	public boolean isRunning() {
		return this.started.get();
	}	
	

}
