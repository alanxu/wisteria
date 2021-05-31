package a;

import me.alanx.wisteria.core.MessageQueuePusher;
import me.alanx.wisteria.core.socket.AsyncClientSocketTransport;
import me.alanx.wisteria.core.socket.AsyncSocketServer;
import me.alanx.wisteria.core.transport.IoTransport;
import me.alanx.wisteria.core.transport.TransportListener;
import me.alanx.wisteria.utils.BufferUtil;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {

	public App() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {

		
		ExecutorService serverExecutor = Executors.newCachedThreadPool();

		Queue<ByteBuffer> messageQueue = new ArrayBlockingQueue<>(1024);

		MessageQueuePusher queuePusher = new MessageQueuePusher(serverExecutor, messageQueue);
		


		AsyncSocketServer server = new AsyncSocketServer("localhost", 1234, serverExecutor);

		server.listenedBy(queuePusher);
		
		server.start();


		TransportListener<ByteBuffer> clientListener = new TransportListener<ByteBuffer>() {
			@Override
			public void onConnected() {

			}

			@Override
			public void onDisconnected() {

			}

			@Override
			public void onReceived(IoTransport transport, ByteBuffer data) {
				data.flip();
				System.out.println("Client Received: " + BufferUtil.readString(data));
			}

			@Override
			public void onSent(IoTransport transport, ByteBuffer data) {

			}
		};
			
		Runnable writer = () -> {
			AsyncClientSocketTransport client = AsyncClientSocketTransport.open();
			client.listenedBy(clientListener).connect("localhost", 1234);


			while (true) {
				client.write(ByteBuffer.wrap("Test Message. ".getBytes(Charset.defaultCharset())));

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		};
			



		Thread t1 = new Thread(writer);
		t1.start();

		Thread t2 = new Thread(writer);
		t2.start();

		Thread t3 = new Thread(writer);
		t3.start();

		Thread t4 = new Thread(writer);
		t4.start();

		Thread t5 = new Thread(writer);
		t5.start();

		try {
			Thread.sleep(Long.MAX_VALUE);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	static String encodeBytes(ByteBuffer bs) {
		try {
			return "" + Charset.forName("UTF-8").newDecoder().decode(bs);
		} catch (CharacterCodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
