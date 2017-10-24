package com.thomsonreuters.trtn.wisteria.core.protocol;

import com.thomsonreuters.trtn.wisteria.core.Message;

public interface Protocol {
	int startIndex(StringBuffer message);
	
	int startIndex(String message);
	
	int endIndex(StringBuffer message);
	
	int endIndex(String message);
	
	boolean isValid(StringBuffer message);
	
	boolean isValid(String message);
	
	Message parse(StringBuffer message);
	
	Message parse(String message);
	
	String protocolName();
	
	String protocolVersion();
	
	String wrap(String message);
}
