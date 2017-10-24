package com.thomsonreuters.trtn.wisteria.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.thomsonreuters.trtn.wisteria.adapter.ConnectionManager;
import com.thomsonreuters.trtn.wisteria.adapter.RecoveryManager;
import com.thomsonreuters.trtn.wisteria.adapter.xml.filter.MakeProcessedCommandFilter;
import com.thomsonreuters.trtn.wisteria.conf.Configuration;
import com.thomsonreuters.trtn.wisteria.conf.Profile;
import com.thomsonreuters.trtn.wisteria.conf.SessionConfigurationKeys;
import com.thomsonreuters.trtn.wisteria.core.event.Event;
import com.thomsonreuters.trtn.wisteria.core.event.EventHandler;
import com.thomsonreuters.trtn.wisteria.core.filter.Filter;
import com.thomsonreuters.trtn.wisteria.core.operation.Operation;
import com.thomsonreuters.trtn.wisteria.core.session.listener.SessionListener;
import com.thomsonreuters.trtn.wisteria.core.socket.SocketReadClientInputOperationProvider;
import com.thomsonreuters.trtn.wisteria.heartbeat.HeartbeatManager;
import com.thomsonreuters.trtn.wisteria.heartbeat.HeartbeatMessageBuilder;
import com.thomsonreuters.trtn.wisteria.heartbeat.SendHeartbeatOperationProvider;

public class MockConfiguration implements Configuration{

	final private Map<String, Object> cache = new HashMap<String, Object>(10);
	
	ExecutorService executor = Executors.newCachedThreadPool();

	ScheduledExecutorService scheduler;
	BlockingQueue<Operation> sessionWorkQueue;
	
	

	public MockConfiguration(ScheduledExecutorService scheduler,
			BlockingQueue<Operation> sessionWorkQueue) {
		super();
		this.scheduler = scheduler;
		this.sessionWorkQueue = sessionWorkQueue;
	}

	public  Profile getSessionConfiguration(String key) {
		return new Profile(){
/*			@Override
			public synchronized SessionHandler getSessionHandler() {
				SessionHandler instance = (SessionHandler)cache.get("getSessionHandler");
				if(instance == null){
					instance = new MockSessionMessageHandler();
					cache.put("getSessionHandler", instance);
				}
				return instance;
			}*/

			@Override
			public synchronized List<SessionListener> getSessionListeners() {
					
					List<SessionListener> result = new ArrayList<SessionListener>();
					result.add(new HeartbeatManager(scheduler, sessionWorkQueue));
					result.add(new ConnectionManager());
					result.add(new RecoveryManager());
					return result;
			}

			@Override
			public synchronized Map<String, Filter> getFilters() {
				Map<String, Filter> instance = (Map<String, Filter>)cache.get("getFilters");
				if(instance == null){
					instance = new HashMap<String, Filter>();
					instance.put("command", new MakeProcessedCommandFilter());
					cache.put("getFilters", instance);
				}
				return instance;
			}

			@Override
			public synchronized Map<Class<Event>, EventHandler> getEventHandlerMapping() {
				Map<Class<Event>, EventHandler> instance = (Map<Class<Event>, EventHandler>)cache.get("getEventHandlerMapping");
				if(instance == null){
					instance = new HashMap<Class<Event>, EventHandler>();
					cache.put("getEventHandlerMapping", instance);					
				}
				return instance;
			}

			@Override
			public synchronized int getEventQueueInitialSize() {
				return 20;
			}

			@Override
			public int getWriteQueueInitialSize() {
				return 20;
			}

			@Override
			public Configuration getConfiguration() {
				return new Configuration(){
					
					private Map<String, Object> configurationItems;
					
					{
						this.configurationItems = new HashMap<String, Object>();
						configurationItems.put(SessionConfigurationKeys.HEARTBEAT_INTERVAL_IN_MILLIS, new Integer(2000));
						configurationItems.put(SessionConfigurationKeys.HEARTBEAT_MESSAGE_BUILDER, new HeartbeatMessageBuilder(){

							@Override
							public String heartbeatMessage() {									
								return "<HEARTBEAT/>";
							}							
						});	
						
						configurationItems.put(SessionConfigurationKeys.HEARTBEAT_OPERATION_PROVIDER, new SendHeartbeatOperationProvider());
						configurationItems.put(SessionConfigurationKeys.READ_INPUT_OPERATION_PROVIDER, new SocketReadClientInputOperationProvider());
					}

					@SuppressWarnings("unchecked")
					@Override
					public <T> T getProperty(Object key) {						
						return (T) this.configurationItems.get(key);
					}
					
				};
			}

			/*@Override
			public SessionOperation getSessionOperation() {
				return  new DefaultSessionOperation();
			}*/};
	}

	@Override
	public <T> T getProperty(Object key) {
		if("sessionConfigurationKey".equalsIgnoreCase(key.toString())){
			return (T)getSessionConfiguration("");
		}
		return null;
	}





}
