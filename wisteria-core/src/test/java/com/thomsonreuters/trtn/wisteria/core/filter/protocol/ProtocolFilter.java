package com.thomsonreuters.trtn.wisteria.core.filter.protocol;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import com.thomsonreuters.trtn.wisteria.core.filter.FilterBase;
import com.thomsonreuters.trtn.wisteria.core.session.Session;

public class ProtocolFilter extends FilterBase{	
	
	private ThreadLocal<StringBuffer> buffer = new ThreadLocal<StringBuffer>();
	private Charset charset;
	private int bufferSize;
	
	public ProtocolFilter(int bufferSize, Charset charset){
		if(bufferSize <= 0){
			bufferSize = 4096;
		}
		this.bufferSize = bufferSize;
		
		if(charset == null){
			charset = Charset.forName("UTF-8");
		}
		this.charset = charset;
	}
	
	public ProtocolFilter(){
		this(0, null);
	}
	
	public ProtocolFilter(Charset charset){
		this(0, charset);
	}
	
	public ProtocolFilter(int bufferSize){
		this(bufferSize, null);
	}
	
	@Override
    public void clientMessageReceived(NextFilter nextFilter, Session session, Object message) throws Exception {
		if(writeToBuffer(message)){
			StringBuffer buffer = this.buffer.get();
			// TODO do protocol based filtering
			
			nextFilter.messageReceived(session, "<command><name>make_processed</name></command>");
			nextFilter.messageReceived(session, "<command><name>update_party</name></command>");
		}else{
			nextFilter.messageReceived(session, message);
		}
		
    }
	
	private boolean writeToBuffer(Object message){
		StringBuffer buffer = this.buffer.get();
		if(buffer == null){
			buffer = new StringBuffer(this.bufferSize);
			this.buffer.set(buffer);
		}
		boolean writeSuccess = false;
		if(message instanceof String 
				|| message instanceof Number
				|| message instanceof java.lang.Character
				|| message instanceof char[]
				|| message instanceof java.lang.Character[]
				|| message instanceof CharSequence
				|| message instanceof StringBuffer){
			buffer.append(message);
			writeSuccess = true;
		}else if(message instanceof ByteBuffer){
			ByteBuffer bb = (ByteBuffer)message;
			CharBuffer cb = bb.asCharBuffer();
			buffer.append(cb.array());
			writeSuccess = true;
		}else if(message instanceof byte[]){
			byte[] bArray = (byte[])message;
			ByteBuffer bb = ByteBuffer.wrap(bArray);
			CharBuffer cb = bb.asCharBuffer();
			buffer.append(cb.array());
			writeSuccess = true;
		}
		return writeSuccess;
	}
	
	static void test(Object i){
		System.out.println(i.getClass());
	}
	
	public static void main(String[] args){
		char c = 'd';
		test(c);
	}
}
