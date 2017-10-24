package me.alanx.wisteria.core.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class BasicProtocol implements Protocol<Message> {

	private static final int VERSION = 1;

	private static final int HEAD_LENTH = 5;


	
	public Message encode0(ByteBuffer buffer) {
		
		if (buffer == null) {
			throw new NullPointerException("Packet buffer cannot be null. ");
		}
		
		if (buffer.limit() < HEAD_LENTH) {
			throw new ProtocolException("Packet is not complete");
		}
		
		// Get and check version
		int version = buffer.get();
		if (version != VERSION) {
			throw new ProtocolException(String.format("Version not match. Expected: %d, Actual: %d", VERSION, version));
		}
		
		// Get and return system message without encoding body
		int command = buffer.get();
		MessageType type = MessageType.forNumber(command);
		
		// Get body lenth
		int bodyLen = buffer.getShort();
		
		// Get compressed flag
		boolean compressed = buffer.get() == 1;
		
		// ------- Above steps must be performed to proceed --------
		if(type != MessageType.APPLICATION_MSG) {
			try {
					return type.getClazz().newInstance();
			} catch (Exception e) {
				throw new ProtocolException("Cannot create instance for message by newInstance()", e);
			} 
		} else {
		// Encode body
			
			//ByteBuffer bodyBuf = buffer.slice();
			ByteBuffer bodyBuf = ByteBuffer.allocate(bodyLen);

			byte[] bodyBytes = new byte[bodyLen];
			buffer.get(bodyBytes, 0, bodyLen);
			bodyBuf.put(bodyBytes);
			
			return this.encodeBody(bodyBuf);
		}
		
		
	}

	@Override
	public Message[] encode(ByteBuffer buffer) {
			List<Message> msgs = new ArrayList<>();
			while(buffer.hasRemaining()) {
				Message m = encode0(buffer);
				msgs.add(m);
			}
			Message[] msgArray = new Message[msgs.size()]; 
			return msgs.toArray(msgArray);
	}
	
	@Override
	public ByteBuffer decode(Message value) {

		// Identify the type of the message
		MessageType type = MessageType.forClass(value.getClass());

		// If the message type needs a packet body, decode the message as
		// packet body by invoking overridable method
		int bodyLen = 0;
		ByteBuffer bodyBuf = null;
		if (type == MessageType.APPLICATION_MSG) {
			bodyBuf = decodeBody(value);
			//bodyBuf.flip();
			bodyLen = bodyBuf == null? 0 : bodyBuf.limit();
		}

		// Construct packet head items
		List<ByteBuffer> headBytes = new ArrayList<>();

		// - Protocol version, len = 1
		headBytes.add(ByteBuffer.allocate(1).put((byte) VERSION));
		// - Command id, len = 1
		headBytes.add(ByteBuffer.allocate(1).put((byte) (byte) type.getId()));
		// - Body length, len = 2
		headBytes.add(ByteBuffer.allocate(2).putShort((short) bodyLen));
		// - Compressed, len = 1
		headBytes.add(ByteBuffer.allocate(1).put((byte) 0));

		// Calculate buffer size
		int packetLen = 5 + bodyLen;

		// Allocate buffer
		ByteBuffer packetBuf = ByteBuffer.allocate(packetLen);

		// Write head
		headBytes.forEach(h -> {
			h.flip();
			packetBuf.put(h);
		});

		// Write body
		if (bodyBuf != null)
			packetBuf.put(bodyBuf);

		return packetBuf;
	}


	@Override
	public int version() {
		return VERSION;
	}

	protected ByteBuffer decodeBody(Message value) {
		// Default implementation

		ByteArrayOutputStream bo = new ByteArrayOutputStream();

		try {
			ObjectOutputStream oo = new ObjectOutputStream(bo);
			oo.writeObject(value);
			byte[] ba = bo.toByteArray();
			return ByteBuffer.wrap(ba);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	protected Message encodeBody(ByteBuffer buffer) {
		// Default implementation

		if (!buffer.hasArray()) {
			throw new IllegalArgumentException("Buffer has no array.");
		}

		buffer.flip();

		ByteArrayInputStream bi = new ByteArrayInputStream(buffer.array());
		try {
			ObjectInputStream oi = new ObjectInputStream(bi);
			Object msg = oi.readObject();
			return (Message) msg;
		} catch (Exception e) {
			new ProtocolException("Cannot serialize body.", e);
		}

		return null;
	}
	

	public static void main(String[] args) {
		BasicProtocol p = new BasicProtocol();
		
		//Message hbMsg = new HeartbeatMessage();
		Message hbMsg = new Message() {
			
		};
		
		
		ByteBuffer b = p.decode(hbMsg);
		
		System.out.println(b.limit());
		
		b.flip();
		Message[] hbMsg2 = p.encode(b);
		
		System.out.println(hbMsg2.getClass().getName());
	}



}
