package me.alanx.wisteria.core.protocol;

import java.nio.ByteBuffer;

import me.alanx.wisteria.core.session.Session;

public class Packet {
	private final ByteBuffer bytes;
	private final Session session;
	public Packet(Session session, ByteBuffer bytes) {
		super();
		this.bytes = bytes;
		this.session = session;
	}
	/**
	 * @return the bytes
	 */
	public ByteBuffer getBytes() {
		return bytes;
	}
	/**
	 * @return the session
	 */
	public Session getSession() {
		return session;
	}
	
	
}
