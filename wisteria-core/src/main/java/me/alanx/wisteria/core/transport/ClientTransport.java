package me.alanx.wisteria.core.transport;

public interface ClientTransport <I, O> extends Transport<I, O>{

	 ClientTransport <I, O> connect(String serverIp, int serverPort);


}
