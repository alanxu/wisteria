package me.alanx.wisteria.core.transport;

import java.nio.ByteBuffer;

import me.alanx.wisteria.core.protocol.Message;

public class BasicProtocoledTransport extends AbstractProtocoledTransport {

	
	public BasicProtocoledTransport(IoTransport socketTransport) {
		super(socketTransport);
	}

	@Override
	protected Message[] encode(ByteBuffer buf) {
		buf.flip();
		return this.getProtocol().encode(buf);
	}

	@Override
	protected ByteBuffer decode(Message msg) {
		return this.getProtocol().decode(msg);
	}

}
