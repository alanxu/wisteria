package com.thomsonreuters.trtn.wisteria.core.session;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thomsonreuters.trtn.wisteria.conf.Configuration;
import com.thomsonreuters.trtn.wisteria.conf.Profile;
import com.thomsonreuters.trtn.wisteria.core.LoginToken;
import com.thomsonreuters.trtn.wisteria.core.Message;
import com.thomsonreuters.trtn.wisteria.core.filter.FilterBase;
import com.thomsonreuters.trtn.wisteria.core.session.listener.SessionStatusListener;

public class SessionInitiationFilter extends FilterBase{
	
	private Logger logger = LoggerFactory.getLogger(SessionInitiationFilter.class);
	
	//TODO need synchronize
	private Configuration configuration;	
	
	public SessionInitiationFilter(Configuration configuration) {
		super();
		this.configuration = configuration;
	}

	@Override
    public void clientMessageReceived(NextFilter nextFilter, Session session, Object message) throws Exception {
		
		if(session.getStatus() == SessionStatus.NEW){
			if(message instanceof Message){
				logger.debug(message.toString());
				try {
					doInitiation(session, message);
				} catch (ClassCastException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				logger.warn("Message rejected: " + message + " Reason: the session is not initiated. ");
			}
			return;
		}
		nextFilter.messageReceived(session, message);
    }
	
	private void doInitiation(Session session, Object message){
		LoginToken loginToken = ((Message)message).getAttachement();
		if(loginToken != null){
			String profileKey = getProfileKey(session, loginToken);
			Profile profile = this.configuration.getProperty(profileKey);
			if(profile != null){
				session.init(profile);
				session.setStatus(SessionStatus.INITIATED);				
				for(SessionStatusListener sessionListener : session.getSessionListeners(SessionStatusListener.class)){
					sessionListener.onSessionInitiated(session);
				}
			}
		}
	}
	
	private String getProfileKey(Session session, LoginToken loginToken){
		//return loginToken == null ? null : loginToken.getUsername();
		return "sessionConfigurationKey";
	}

}
