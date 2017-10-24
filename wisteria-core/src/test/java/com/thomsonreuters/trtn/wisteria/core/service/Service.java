package com.thomsonreuters.trtn.wisteria.core.service;


public interface Service {
	boolean isActive();
	boolean isStopping();
	boolean isStopped();
	void start();
	void stop();
}
