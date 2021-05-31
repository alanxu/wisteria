package me.alanx.wisteria.core;

import me.alanx.wisteria.core.transport.IoTransport;

public interface ServerListener {
    void onTransportCreated(IoTransport transport);
    void onServerStarted();
    void onServerStopped();
}
