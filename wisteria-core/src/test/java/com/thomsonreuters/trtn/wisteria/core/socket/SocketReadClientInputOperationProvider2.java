package com.thomsonreuters.trtn.wisteria.core.socket;

import java.nio.channels.SelectionKey;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thomsonreuters.trtn.wisteria.core.operation.Operation;
import com.thomsonreuters.trtn.wisteria.core.operation.OperationProvider;
import com.thomsonreuters.trtn.wisteria.core.session.Session;
import com.thomsonreuters.trtn.wisteria.core.session.SessionOperation;
import com.thomsonreuters.trtn.wisteria.core.session.listener.SessionErrorListener;
import com.thomsonreuters.trtn.wisteria.core.transport.Transport;
import com.thomsonreuters.trtn.wisteria.core.transport.Transport2;
import com.thomsonreuters.trtn.wisteria.core.transport.TransportException;
import com.thomsonreuters.trtn.wisteria.core.transport.TransportListener;

public class SocketReadClientInputOperationProvider2 implements OperationProvider {
	@Override
	public Operation newOperation(Session session) {
		return new SocketReadClientInputOperation1(session);
	}
}

class SocketReadClientInputOperation1 extends SessionOperation{
	
	public SocketReadClientInputOperation1(Session session) {
		super(session);
	}

	private static final Logger logger = LoggerFactory.getLogger(SocketReadClientInputOperation.class);
	
	protected final int bufferSize = 1024;

	@Override
	public void doOperation(Session session) {
		try {
			logger.debug("Begin read client data, session: "+session.getSessionId());
			
			Transport2<?, ?> transport = session.getTransport();
			if(!(transport instanceof SocketTransport)){
				throw new IllegalArgumentException("Invalid transport type. ");
			}
			
/*			Object message = transport.read();
			
			if(message == null){
				
				return;
			}
			
			while(message != null && !isEmptyString(message)){
				try{
					session.getFilterChain().fireMessageReceivedFromClient(message);
				}catch(Exception e){
					for(SessionErrorListener sessionListener : session.getSessionListeners(SessionErrorListener.class)){
						sessionListener.onExceptionCaught(session, e);
					}
					return;
				}
				message = transport.read();
			}*/
			
			logger.debug("Finish reading client data, session: "+session.getSessionId());
		}catch (TransportException e) {
			e.printStackTrace();
			handleConnectionLost(session);
		} 
		catch (Exception e) {
			logger.error("Error reading client data. Session: "+session.getSessionId(), e);
		} finally {
			//Remove the channel from selected key set.
			Set<SelectionKey> selectedKeys = session.getAttribute(SocketSessionAttributeKeys.KEY_SELECTEDKEYSET);
			SelectionKey key = session.getAttribute(SocketSessionAttributeKeys.KEY_SELECTIONKEY);
			if(selectedKeys != null)
				selectedKeys.remove(key);
			
			//Reset the select mode, because that it has been processed.
			session.setSelectMode(0);
			
		}
	}

	private boolean isEmptyString(Object message){
		if(!(message instanceof String))
			return false;
		String str = (String)message;
		return str.isEmpty();
	}

	protected void handleConnectionLost(Session session){
		logger.warn("Connection of session " + session.getSessionId() + " has been lost! ");		

		session.close();
		for(TransportListener l : session.getSessionListeners(TransportListener.class)){
			l.onConnectionLost(session);
		}
	}
}