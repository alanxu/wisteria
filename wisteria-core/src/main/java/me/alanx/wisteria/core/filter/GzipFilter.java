package me.alanx.wisteria.core.filter;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GzipFilter extends Filter{

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	protected boolean filterInbound(FilterContext context) {
		ByteBuffer buf = context.tryGetValue(ByteBuffer.class);
		if (buf != null) {
			log.trace("do unzip..");
		}
		
		return true;
	}

	@Override
	protected boolean filterOutbound(FilterContext context) {
		ByteBuffer buf = context.tryGetValue(ByteBuffer.class);
		if (buf != null) {
			log.trace("do zip..");
		}
		
		return true;
	}

}
