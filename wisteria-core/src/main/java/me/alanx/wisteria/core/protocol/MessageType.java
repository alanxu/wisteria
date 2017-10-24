package me.alanx.wisteria.core.protocol;

public enum MessageType {
	HANDSHAKE_REQ(1, HandshakeRequestMessage.class),
	HANDSHAKE_RSP(2, HandshakeResponseMessage.class),
	HEARTBEAT(3, HeartbeatMessage.class),
	APPLICATION_MSG(4, Message.class)
	;
	private int id;
	private Class<? extends Message> clazz;
	
	private MessageType(int id, Class<? extends Message> clazz) {
		this.id = id;
		this.clazz = clazz;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the clazz
	 */
	public Class<? extends Message> getClazz() {
		return clazz;
	}
	
	public static MessageType forNumber(int id) {
		for(MessageType t : values()) {
			if (id == t.getId()) {
				return t;
			}
		}
		throw new IllegalArgumentException();
	}
	
	public static MessageType forClass(Class<? extends Message> cls) {
		for(MessageType t : values()) {
			if (cls == t.getClazz()) {
				return t;
			}
		}
		return APPLICATION_MSG;
	}
	
}
