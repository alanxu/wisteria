package me.alanx.wisteria.core.protocol;

import java.nio.ByteBuffer;

public interface Protocol <V> {
	
	V[] encode(ByteBuffer buffer);
	
	ByteBuffer decode(V value);
	
	int version();
	
}
