package me.alanx.wisteria.core;

public interface Server {
	
	void start();
	
	void shutdown();

	Server listenedBy(ServerListener listener);
}
