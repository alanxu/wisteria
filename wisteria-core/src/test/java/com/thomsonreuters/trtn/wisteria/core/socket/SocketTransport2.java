package com.thomsonreuters.trtn.wisteria.core.socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thomsonreuters.trtn.wisteria.core.session.Session;
import com.thomsonreuters.trtn.wisteria.core.transport.AbstractTransport2;
import com.thomsonreuters.trtn.wisteria.core.transport.TransportException;

public class SocketTransport2 extends AbstractTransport2<String, String>{
	
	Logger logger = LoggerFactory.getLogger(SocketTransport.class);
	private SocketChannel socketChannel;
	//Process the data sent from client
	ByteBuffer buf;
	ByteBuffer remainingCheckBuf;
	int bufferSize = 1024;
	
	Charset charset;
	CharsetDecoder decoder;
	CharsetEncoder encoder;
	
	public SocketTransport2(SocketChannel socketChannel, Charset charset, int bufferSize) {
		this.socketChannel = socketChannel;
		this.charset = charset;
		this.decoder = charset.newDecoder();
		this.encoder = charset.newEncoder();
		this.bufferSize = bufferSize;
		this.buf = ByteBuffer.allocateDirect(bufferSize);
	}

	@Override
	public void sendDirectly(String output) throws TransportException {
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
	
	public SocketChannel getChannel(){
		return this.socketChannel;
	}


}
