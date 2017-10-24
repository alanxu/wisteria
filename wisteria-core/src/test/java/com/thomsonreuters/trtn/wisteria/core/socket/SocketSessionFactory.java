package com.thomsonreuters.trtn.wisteria.core.socket;

import java.nio.channels.SocketChannel;

import com.thomsonreuters.trtn.wisteria.core.session.Session;

public interface SocketSessionFactory {
	Session newSession(SocketChannel socketChannel);
}
