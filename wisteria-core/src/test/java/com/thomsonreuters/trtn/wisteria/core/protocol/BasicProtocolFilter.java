package com.thomsonreuters.trtn.wisteria.core.protocol;


import com.thomsonreuters.trtn.wisteria.core.filter.Filter.NextFilter;
import com.thomsonreuters.trtn.wisteria.core.filter.FilterBase;
import com.thomsonreuters.trtn.wisteria.core.session.Session;

public class BasicProtocolFilter  extends FilterBase{
	private static final String KEY_PROTOCOL_FILTER = "KEY_PROTOCOL_FILTER";
	private static final int MAX_MESSAGE_LENGTH = 10000;
	
	private final Protocol protocol;
	private final String contextKey;
	
	public BasicProtocolFilter(Protocol protocol){
		this.protocol = protocol;
		contextKey = contextKey();
	}
	
	@Override
    public void clientMessageReceived(NextFilter nextFilter, Session session, Object message) throws Exception {
		String msg = message.toString();

		ProtocolParseContext parseContext = session.getAttribute(contextKey);
		if(parseContext == null){
			parseContext = new ProtocolParseContext();
			parseContext.setStatus(ProtocolParseStatus.WAITING_FOR_START);
			parseContext.setFilter(this);
			session.setAttribute(contextKey, parseContext);
		}
		
		//There should be only one filter for the same protocol.
		if(parseContext.getFilter() != this){
			throw new Exception("Configuration error! It is not allowed to have two filter for a same protocol! ");
		}
		
		StringBuffer buffer = parseContext.getBuffer();
		switch(parseContext.getStatus()){
			case WAITING_FOR_START:	
				flush(session, nextFilter);
				
				buffer.append(msg);
				
				boolean proceed = true;
				while(proceed){
					int startIndex = protocol.startIndex(buffer);
					int endIndex = protocol.endIndex(buffer);
					if(startIndex == 0 && endIndex > 0){
						//A perfect message coming in.
						String messageRead = buffer.substring(startIndex, endIndex + 1);
						nextFilter.messageReceived(session, this.protocol.parse(messageRead));
						buffer.delete(startIndex, endIndex + 1);
						proceed = true;
					}else if(startIndex >= endIndex && endIndex >= 0){
						//Invalid protocol message, consequent two start coming in.
						throw new RuntimeException("Invalid message! ");
					}else if(startIndex > 0 && endIndex > 0){
						//A complete protocol message coming in, but with some content at the head of it.
						String otherProtocolMessage = buffer.substring(0, startIndex);
						nextFilter.messageReceived(session, this.protocol.parse(otherProtocolMessage));
						
						String messageRead = buffer.substring(startIndex, endIndex + 1);
						nextFilter.messageReceived(session, this.protocol.parse(messageRead));
						buffer.delete(0, endIndex + 1);
						proceed = true;
					}else if(startIndex >= 0 && endIndex < 0){
						//A incomplete protocol message coming in.
						if(startIndex > 0){
							String otherProtocolMessage = buffer.substring(0, startIndex);
							nextFilter.messageReceived(session, otherProtocolMessage);
						}
						
						buffer.delete(0, startIndex);
						parseContext.setStatus(ProtocolParseStatus.WAITING_FOR_END);
						proceed = false;
					}else{
						//For other cased, foward it to next filter.
						flush(session, nextFilter);
						proceed = false;
					}
				}
				
				break;
			case WAITING_FOR_END:
				if(buffer.length() == 0){
					throw new Exception("Unexpected protocol error.");
				}
				buffer.append(msg);
				
				boolean proceed2 = true;
				while(proceed2){
					int startIndex = protocol.startIndex(buffer);
					int endIndex = protocol.endIndex(buffer);
					if(startIndex >= 0 && endIndex >= 0 && startIndex >= endIndex ){
						throw new RuntimeException("Invalid message! ");
					}else if(endIndex >= 0){
						String messageReade = buffer.substring(0, endIndex+1); 
						buffer.delete(0, endIndex+1);
						nextFilter.messageReceived(session, this.protocol.parse(messageReade));
						parseContext.setStatus(ProtocolParseStatus.WAITING_FOR_START);
						proceed2 = true;
					}else if(startIndex > 0 && endIndex > 0){
						String otherProtocolMessage = buffer.substring(0, startIndex);
						nextFilter.messageReceived(session, otherProtocolMessage);						
						String messageRead = buffer.substring(startIndex, endIndex + 1);
						nextFilter.messageReceived(session, this.protocol.parse(messageRead));
						buffer.delete(0, endIndex + 1);
						parseContext.setStatus(ProtocolParseStatus.WAITING_FOR_START);
						proceed2 = true;
					}else if(startIndex >= 0 && endIndex < 0){
						String otherProtocolMessage = buffer.substring(0, startIndex);
						nextFilter.messageReceived(session, this.protocol.parse(otherProtocolMessage));
						buffer.delete(0, startIndex);
						parseContext.setStatus(ProtocolParseStatus.WAITING_FOR_END);
						proceed2 = false;
					}else{
						proceed2 = false;
					}
				}				
		}		
    }
	
	protected void flush(Session session, NextFilter nextFilter ){
		ProtocolParseContext parseContext = session.getAttribute(this.contextKey);
		if(parseContext != null){
			StringBuffer buffer = parseContext.getBuffer();
			if(buffer.length() > 0){
				nextFilter.messageReceived(session, buffer.toString());
				buffer.setLength(0);
			}
		}		
	}
	
	private class ProtocolParseContext{
		ProtocolParseStatus status;
		StringBuffer buffer = new StringBuffer();
		BasicProtocolFilter filter;
		
		ProtocolParseStatus getStatus(){
			return this.status;
		}
		
		void setStatus(ProtocolParseStatus status){
			this.status = status;
		}
		
		StringBuffer getBuffer(){
			return this.buffer;
		}

		Object getFilter() {
			return filter;
		}

		void setFilter(BasicProtocolFilter filter) {
			this.filter = filter;
		}
		
	}
	
	@Override
	public void filterWrite(NextFilter nextFilter, Session session,
			Object writeRequest) throws Exception {
		nextFilter.filterWrite(session, this.protocol.wrap(writeRequest.toString()));
	}

	private enum ProtocolParseStatus{
		WAITING_FOR_START, WAITING_FOR_END
	}
	
	protected String contextKey(){
		return KEY_PROTOCOL_FILTER + "_" + this.protocol.protocolName() + "_" + this.protocol.protocolVersion();
	}
	
	public static void main(String[] args){
		StringBuffer sb = new StringBuffer("123");
		System.out.println(sb.indexOf("123"));
	}

}