package com.thomsonreuters.trtn.wisteria.core;

import com.thomsonreuters.trtn.wisteria.conf.Configuration;
import com.thomsonreuters.trtn.wisteria.core.operation.OperationExecutor;
import com.thomsonreuters.trtn.wisteria.core.operation.OperationScheduler;
import com.thomsonreuters.trtn.wisteria.core.plugin.ServiceRegistration;

public interface Application {
	OperationExecutor getOperationExecutor();
	OperationScheduler getOperationScheduler();
	Configuration getConfiguration();
	
	ServiceRegistration getServiceResgistration();	
}
