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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thomsonreuters.trtn.wisteria.core.session.DefaultSession;
import com.thomsonreuters.trtn.wisteria.core.transport.AbstractTransport;
import com.thomsonreuters.trtn.wisteria.core.transport.TransportException;
import com.thomsonreuters.trtn.wisteria.core.transport.TransportListener;

public class SocketTransport extends AbstractTransport<String, String>{

	Logger logger = LoggerFactory.getLogger(SocketTransport.class);
	private SocketChannel socketChannel;
	//Process the data sent from client
	ByteBuffer buf;
	ByteBuffer remainingCheckBuf;
	int bufferSize = 1024;
	
	Charset charset;
	CharsetDecoder decoder;
	CharsetEncoder encoder;
	
	public SocketTransport(SocketChannel socketChannel, Charset charset, int bufferSize){
		this.socketChannel = socketChannel;
		this.charset = charset;
		this.decoder = charset.newDecoder();
		this.encoder = charset.newEncoder();
		this.bufferSize = bufferSize;
		this.buf = ByteBuffer.allocateDirect(bufferSize);
	}
	
	@Override
	public synchronized String read() throws TransportException {
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

	@Override
	public synchronized void write(String output) throws TransportException {
		CharBuffer cb = CharBuffer.wrap(output);
		try {
			ByteBuffer bf = this.encoder.encode(cb);
			synchronized(socketChannel){
				this.socketChannel.write(bf);
			}
		} catch (CharacterCodingException e) {
			throw new TransportException(e);
		} catch (IOException e) {
			throw new TransportException(e);
		} catch (Throwable t){
			throw new TransportException(t);
		}
	}

	@Override
	public synchronized void open() throws TransportException {}

	@Override
	public synchronized void close() throws TransportException {		
		try {			
			SelectionKey key = session.getAttribute(SocketSessionAttributeKeys.KEY_SELECTIONKEY);
			key.cancel();
			if(socketChannel.isConnected() && socketChannel.isOpen()){
				socketChannel.close();
			}
		} catch (IOException e) {
			throw new TransportException(e);
		}
	}
	
	protected void handleConnectionLost(){
		close();
		for(TransportListener l : this.listeners){
			l.onConnectionLost(session);
		}
		return;
	}

	@Override
	public synchronized boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public synchronized SocketChannel getSocketChannel(){
		return this.socketChannel;
	}

}
