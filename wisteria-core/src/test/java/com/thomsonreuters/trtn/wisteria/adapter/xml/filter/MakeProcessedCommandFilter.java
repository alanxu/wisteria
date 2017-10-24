package com.thomsonreuters.trtn.wisteria.adapter.xml.filter;

import com.thomsonreuters.trtn.wisteria.core.event.Event;
import com.thomsonreuters.trtn.wisteria.core.session.Session;

public class MakeProcessedCommandFilter extends CommandFilter{

	@Override
	protected boolean accept(Session session, Object message) {
		if(message instanceof String){
			return ((String)message).indexOf("<name>make_processed</name>") > 0;
		}
		return false;
	}

	@Override
	protected Event wrap(Session session, Object message) {
		return null;
	}

}
