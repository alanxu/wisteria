package me.alanx.wisteria.core.transport;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

import me.alanx.wisteria.core.reactor.SubscribeMode;

public interface Transport <I, O> {

	/**
	 * 
	 * @param data
	 * @return Future<Integer> which's get() method returns result > 0: success0: success but no data write, 
	 * 			or throws an ExecutionException or InterruptedException if the write task fails or get() method 
	 * 			failed or interrupted.
	 */
	 Future<Integer> write(O data);
	
//	 <T> void read(long timeout, TimeUnit unit, T attachement, CompletionHandler<I, T> handler);
	
	 Future<I> read();
	 
	 Transport <I, O> withReadMode(SubscribeMode mode);
	 
	// Transport <I, O> listenedBy(SessionListener<I> sessionListener);
	 
	 Transport <I, O> listenedBy(TransportListener<I> listener);
	 
	 List<TransportListener<I>> getTransportListeners();
	 
	 void close() throws IOException;

}
