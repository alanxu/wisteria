package com.thomsonreuters.trtn.wisteria.core.plugin;

import com.thomsonreuters.trtn.wisteria.core.Application;

public interface Plugin {
	void initiate(Application application);
	void dispose(Application application);
}
