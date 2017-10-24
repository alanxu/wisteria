package me.alanx.wisteria.core.reactor;

public interface Processor <T, S> extends Publisher<S>, Subscriber<T>{

}
