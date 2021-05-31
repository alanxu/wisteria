package me.alanx.wisteria.core;

import me.alanx.wisteria.core.transport.IoTransport;
import me.alanx.wisteria.core.transport.IoTransportListener;
import me.alanx.wisteria.utils.BufferUtil;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

public class MessageQueuePusher implements ServerListener, IoTransportListener {
    private final ExecutorService threadPool;
    private final Queue<ByteBuffer> queue;

    public MessageQueuePusher(ExecutorService executorService, Queue<ByteBuffer> messageQueue) {
        this.threadPool = executorService;
        this.queue = messageQueue;
    }

    @Override
    public void onTransportCreated(IoTransport transport) {
        transport.listenedBy(this);
        transport.getTransportListeners().forEach(l -> l.onConnected());
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onReceived(IoTransport transport, ByteBuffer data) {
//        if (session != null) {
        data.flip();
            System.out.println(BufferUtil.readString(data));
            this.queue.offer(data);
            transport.write(ByteBuffer.wrap("ACK. ".getBytes(Charset.defaultCharset())));
//            this.threadPool.submit(new InboundProcessTask(packet, this.subscriber, filterChainBuilder));
//        }
    }

    @Override
    public void onSent(IoTransport transport, ByteBuffer data) {

    }

    @Override
    public void onServerStarted() {

    }

    @Override
    public void onServerStopped() {

    }
}
