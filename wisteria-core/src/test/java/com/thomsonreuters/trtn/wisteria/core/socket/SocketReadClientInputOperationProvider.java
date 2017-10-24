package com.thomsonreuters.trtn.wisteria.core.socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thomsonreuters.trtn.wisteria.core.operation.Operation;
import com.thomsonreuters.trtn.wisteria.core.operation.OperationProvider;
import com.thomsonreuters.trtn.wisteria.core.session.DefaultSession;
import com.thomsonreuters.trtn.wisteria.core.session.Session;
import com.thomsonreuters.trtn.wisteria.core.session.SessionOperation;
import com.thomsonreuters.trtn.wisteria.core.session.listener.SessionErrorListener;
import com.thomsonreuters.trtn.wisteria.core.transport.Transport;
import com.thomsonreuters.trtn.wisteria.core.transport.Transport2;
import com.thomsonreuters.trtn.wisteria.core.transport.TransportException;
import com.thomsonreuters.trtn.wisteria.core.transport.TransportListener;

public class SocketReadClientInputOperationProvider implements OperationProvider {
	private Charset charset;
	
	public SocketReadClientInputOperationProvider(){
		this.charset = Charset.forName("UTF-8");
	}
	
	public SocketReadClientInputOperationProvider(Charset charset){
		this.charset = charset;
	}
	
	@Override
	public Operation newOperation(Session session) {
		return new SocketReadClientInputOperation(session, charset);
	}
}

class SocketReadClientInputOperation extends SessionOperation{
	
	public SocketReadClientInputOperation(Session session, Charset charset) {
		super(session);
		this.buf = ByteBuffer.allocateDirect(bufferSize);
		this.charset = charset;
		this.decoder = charset.newDecoder();
		this.encoder = charset.newEncoder();
	}

	private static final Logger logger = LoggerFactory.getLogger(SocketReadClientInputOperation.class);
	ByteBuffer buf;
	protected final int bufferSize = 1024;
	Charset charset;
	CharsetDecoder decoder;
	CharsetEncoder encoder;
	

	@Override
	public void doOperation(Session session) {
		try {
			logger.debug("Begin read client data, session: "+session.getSessionId());
			
			Transport2<String, String> transport = session.getTransport();
			
			if(!(transport instanceof SocketTransport2)){
				throw new IllegalArgumentException("Invalid transport type. ");
			}
			
			SocketTransport2 socketTransport = (SocketTransport2)transport;
			
			String message = read(session, socketTransport.getChannel());
			
			while(message != null && !isEmptyString(message)){
				try{
					transport.receive(message);
				}catch(Exception e){
					for(SessionErrorListener sessionListener : session.getSessionListeners(SessionErrorListener.class)){
						sessionListener.onExceptionCaught(session, e);
					}
					return;
				}
				message = read(session, socketTransport.getChannel());
			}
			
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
	
	private String read(Session session, SocketChannel socketChannel) throws TransportException {
		//
		int c = 0;
		try {
			if((session.getSelectMode() & DefaultSession.SelectMode.SOCKET) > 0){
				//buffer.clear();
				//buf.flip();
				synchronized(socketChannel){
					c = socketChannel.read(buf);
				}				
				CharBuffer cb = null;
				logger.info(c+"");
				if(c > 0){
					buf.flip();
					cb = decoder.decode(buf);
				}else if(c < 0){
					throw new TransportException("Error reading client data.");
				}else{
					return null;
				}
				
				return new String(cb.array());
			}
		} catch (CharacterCodingException e) {
			throw new TransportException(e);
		} catch (IOException e) {
			throw new TransportException(e);
		} finally {
			buf.clear();
		}
		return null;
		//logger.debug("[" + Thread.currentThread().getName() + "]" + "Finish reading input stream, session: "+session.getSessionId());
	}
}