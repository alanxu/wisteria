package com.thomsonreuters.trtn.wisteria.core.socket;

import java.nio.channels.SelectionKey;

public interface SocketMonitorListener {
	void onSelected(SelectionKey key);
}