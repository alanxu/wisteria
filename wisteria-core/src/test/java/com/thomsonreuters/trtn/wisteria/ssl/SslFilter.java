package com.thomsonreuters.trtn.wisteria.ssl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.trtn.wisteria.core.filter.FilterBase;
import com.thomsonreuters.trtn.wisteria.core.session.Session;

public class SslFilter extends FilterBase{
	private static final String SSL_FILTER_SUPPORT_KEY = "SSL_FILTER_SUPPORT_KEY";
	private static final String SSL_FILTER_OUTBOUND_DATA_BUFFER_KEY = "SSL_FILTER_OUTBOUND_DATA_BUFFER_KEY";

	private static final int DEFAULT_BUFFER_SIZE = 1024;
	
	private SslConfiguration configuration;
	
	
    public SslFilter(SslConfiguration configuration) {
		super();
		this.configuration = configuration;
	}

	/**
     * {@inheritDoc}
     */
    public void clientMessageReceived(final NextFilter nextFilter, final Session session,
            Object message) throws Exception {
    	if(!(message instanceof ByteBuffer && message instanceof byte[])){
    		nextFilter.messageReceived(session, message);
    		return;
    	}
    	
    	ByteBuffer buffer = null;
    	if(message instanceof ByteBuffer){
    		buffer = (ByteBuffer)message;
    	}else{
    		buffer = ByteBuffer.wrap((byte[])message);
    	}
        SslSupport sslSupport = session.getAttribute(SSL_FILTER_SUPPORT_KEY);
        if(sslSupport == null){
        	sslSupport = new SslSupport(this.configuration, null){
				@Override
				protected void sendWrappedHandshakeData(ByteBuffer buffer)
						throws IOException {
					nextFilter.messageSent(session, buffer);
				}

				@Override
				protected void onHandshakeCompleted() {
					flushOutboundData(session);
				}};
			session.setAttribute(SSL_FILTER_SUPPORT_KEY, sslSupport);
        }
        
        ByteBuffer destBuf = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
        
        while(true){
        	destBuf.clear();
        	SslSupport.Result result = sslSupport.unwrap(buffer, destBuf);
        	destBuf.flip();
        	if(!destBuf.hasRemaining()){
        		return;
        	}
        	if(result == SslSupport.Result.OVERFLOW || result == SslSupport.Result.OK){
        		ByteBuffer output = ByteBuffer.allocate(destBuf.remaining());
        		output.put(destBuf);
        		nextFilter.messageReceived(session, output);
        	}
        	if(result == SslSupport.Result.OK || result == SslSupport.Result.UNDERFLOW){
        		break;
        	}
        }
    }

    /**
     * {@inheritDoc}
     */
    public void clientMessageSent(NextFilter nextFilter, Session session,
            Object writeRequest) throws Exception {
    	if(!(writeRequest instanceof ByteBuffer)){
    		writeRequest = readByte(writeRequest);
    	}
    	
    	SslSupport sslSupport = session.getAttribute(SSL_FILTER_SUPPORT_KEY);
    	if(sslSupport == null || !sslSupport.isHandshakeCompleted()){
    		List<PendingWrite> outboundBuffer = session.getAttribute(SSL_FILTER_OUTBOUND_DATA_BUFFER_KEY);
    		outboundBuffer = outboundBuffer == null ? new ArrayList<PendingWrite>() : outboundBuffer;
    		synchronized(outboundBuffer){
    			outboundBuffer.add(new PendingWrite(nextFilter, writeRequest));
    		}    		
    		session.setAttribute(SSL_FILTER_OUTBOUND_DATA_BUFFER_KEY, outboundBuffer);    		
    		return;
    	}
    	
    	ByteBuffer buf = (ByteBuffer) writeRequest;
        ByteBuffer destBuf = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
        
        flushOutboundData(session);
        
        label1:
        while(true){
        	SslSupport.Result result = sslSupport.wrap(buf, destBuf);
        	if(!destBuf.hasRemaining()){
        		break;
        	}
        	switch(result){
	        	case OK:
	        		destBuf.flip();
	        		nextFilter.messageSent(session, toByteArray(destBuf));
	        		destBuf.clear();
	        		break label1;
	        	case OVERFLOW:
	        		destBuf.flip();
	        		nextFilter.messageSent(session, toByteArray(destBuf));
	        		destBuf.clear();
	        		break;
	        	case UNDERFLOW:
	        		break label1;
	        	default:
	        		break label1;
        	}
        }
    }
    
    private static ByteBuffer readByte(Object writeRequest){
    	if(writeRequest instanceof byte[]){
    		return ByteBuffer.wrap((byte[])writeRequest);
    	}
    	return ByteBuffer.allocate(0);
    }
    
    private static byte[] toByteArray(ByteBuffer buffer){
    	byte[] result = new byte[buffer.remaining()];
    	int i = 0;
    	while(buffer.hasRemaining()){
    		result[i] = buffer.get();
    		i++;
    	}
    	return result;
    }
    
    public void flushOutboundData(Session session){
		List<PendingWrite> outboundBuffer = session.getAttribute(SSL_FILTER_OUTBOUND_DATA_BUFFER_KEY);
		synchronized(outboundBuffer){
			if(outboundBuffer != null){
				for(PendingWrite write : outboundBuffer){
					write.nextFilter.messageSent(session, write.writeRequest);
				}
			}
		}
    }
    
    private class PendingWrite{
    	private NextFilter nextFilter;
    	private Object writeRequest;
		public PendingWrite(NextFilter nextFilter, Object writeRequest) {
			super();
			this.nextFilter = nextFilter;
			this.writeRequest = writeRequest;
		}
    }
    
    
}
