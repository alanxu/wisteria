package me.alanx.wisteria.core.concurrent;

import java.nio.channels.CompletionHandler;
import java.util.concurrent.Future;

public interface CompletionFuture<V, O> extends Future<V>, CompletionHandler<V, O> {}
