package me.alanx.wisteria.core.transport;

import me.alanx.wisteria.core.protocol.Message;
import me.alanx.wisteria.core.protocol.Protocol;
import me.alanx.wisteria.core.reactor.SubscribeMode;
import me.alanx.wisteria.core.reactor.Subscriber;
import me.alanx.wisteria.core.socket.AsyncClientSocketTransport;

public class BasicProtocoledClientTransport extends BasicProtocoledTransport implements ClientTransport<Message, Message> {

	private final IoClientTransport clientSocketTransport;
	
	private BasicProtocoledClientTransport(IoClientTransport clientSocketTransport) {
		super(clientSocketTransport);
		this.clientSocketTransport = clientSocketTransport;
	}

	@Override
	public synchronized BasicProtocoledClientTransport connect(String serverIp, int serverPort) {
		this.clientSocketTransport.connect(serverIp, serverPort);
		//this.getTransportListeners().forEach(l -> l.onConnected());
		return this;
	}

	/* (non-Javadoc)
	 * @see me.alanx.wisteria.protocol2.ProtocoledTransport#protocol(me.alanx.wisteria.protocol2.Protocol)
	 */
	@Override
	public synchronized BasicProtocoledClientTransport protocol(Protocol<Message> protocol) {
		super.protocol(protocol);
		return this;
	}


	
	/* (non-Javadoc)
	 * @see me.alanx.wisteria.core.socket.ProtocoledSocketTransport#withReadMode(me.alanx.wisteria.core.ReadMode)
	 */
	@Override
	public BasicProtocoledClientTransport withReadMode(SubscribeMode mode) {
		super.withReadMode(mode);
		return this;
	}


	/* (non-Javadoc)
	 * @see me.alanx.wisteria.core.socket.ProtocoledSocketTransport#listenedBy(me.alanx.wisteria.core.TransportListener)
	 */
	@Override
	public BasicProtocoledClientTransport listenedBy(TransportListener<Message> listener) {
		super.listenedBy(listener);
		return this;
	}

	public static BasicProtocoledClientTransport open() {
		BasicProtocoledClientTransport client = new BasicProtocoledClientTransport(
				AsyncClientSocketTransport.open());
		return client;
	}

	/* (non-Javadoc)
	 * @see me.alanx.wisteria.core.transport.AbstractProtocoledTransport#subscribedBy(me.alanx.wisteria.core.reactor.Subscriber)
	 */
	@Override
	public BasicProtocoledClientTransport subscribedBy(Subscriber<? super Message> s) {
		super.subscribedBy(s);
		return this;
	}
	
	

}
