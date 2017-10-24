package me.alanx.wisteria.core.transport;

import me.alanx.wisteria.core.protocol.Message;

public interface ProtocoledTransport extends Transport<Message, Message>{
	
	public IoTransport getIoTransport();

	/* (non-Javadoc)
	 * @see me.alanx.wisteria.core.Transport#listenedBy(me.alanx.wisteria.core.TransportListener)
	 */
	@Override
	public ProtocoledTransport listenedBy(TransportListener<Message> listener);
	
	
}
