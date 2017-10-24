package com.thomsonreuters.trtn.wisteria.heartbeat;

import com.thomsonreuters.trtn.wisteria.conf.SessionConfigurationKeys;
import com.thomsonreuters.trtn.wisteria.core.operation.Operation;
import com.thomsonreuters.trtn.wisteria.core.operation.OperationProvider;
import com.thomsonreuters.trtn.wisteria.core.session.Session;
import com.thomsonreuters.trtn.wisteria.core.session.SessionOperation;
import com.thomsonreuters.trtn.wisteria.heartbeat.HeartbeatMessageBuilder;

public class SendHeartbeatOperationProvider implements OperationProvider {

	@Override
	public Operation newOperation(Session session) {
		return new SessionOperation(session){
			@SuppressWarnings("unchecked")
			@Override
			protected void doOperation(Session session) {
				HeartbeatMessageBuilder heartbeatMessageBuilder = session.getConfiguration().getProperty(SessionConfigurationKeys.HEARTBEAT_MESSAGE_BUILDER);
				String heartbeatMessage = heartbeatMessageBuilder.heartbeatMessage();
				session.getTransport().send(heartbeatMessage);
			}
		};
	}
}
