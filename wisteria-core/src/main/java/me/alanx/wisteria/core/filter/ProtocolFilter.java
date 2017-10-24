package me.alanx.wisteria.core.filter;

import java.nio.ByteBuffer;

import me.alanx.wisteria.core.protocol.Message;
import me.alanx.wisteria.core.protocol.Protocol;

public class ProtocolFilter extends Filter {

	private final Protocol<Message> procotol;
	
	public ProtocolFilter(Protocol<Message> procotol) {
		super();
		this.procotol = procotol;
	}

	@Override
	protected boolean filterInbound(FilterContext context) {
		
		ByteBuffer buf = context.tryGetValue(ByteBuffer.class);
		
		if (buf != null) {
			//buf.flip();
			
			Message[] msgs = this.procotol.encode(buf);
			
			context.setValue(msgs);
			
			return true;
		}
		
		return true;
	}

	@Override
	protected boolean filterOutbound(FilterContext context) {
		
		Message m = context.tryGetValue(Message.class);
		
		if (m != null) {
			ByteBuffer buf = this.procotol.decode(m);
			context.setValue(buf);
			return true;
		} 
		
		Message[] msgs = context.tryGetValue(Message[].class);
		
		if (msgs != null) {
			//TODO
			return true;
		}
		
		return true;
	}

}
