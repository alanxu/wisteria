package me.alanx.wisteria.core.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.alanx.wisteria.core.protocol.HeartbeatMessage;
import me.alanx.wisteria.core.protocol.Message;

public class HeartbeatHandlingFilter extends Filter {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	protected boolean filterInbound(FilterContext context) {
		
		Message[] msgs = context.tryGetValue(Message[].class);
		
		if (msgs == null) {
			
			Message m = context.tryGetValue(Message.class);
			
			if (m == null) {
				return true;
			}
			msgs = new Message[1];
			
			msgs[0] = m;
		}
		
		
		for (Message m : msgs) {
			if (m != null && m instanceof HeartbeatMessage) {
				log.debug("beat..");
				
				// Prevent the message to be get by subsequent processes.
				context.setValue(null);
				
				return false;
			}
		}
		
		return true;
	}

	@Override
	protected boolean filterOutbound(FilterContext context) {
		return true;
	}

}
