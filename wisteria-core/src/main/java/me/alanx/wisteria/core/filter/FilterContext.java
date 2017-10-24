package me.alanx.wisteria.core.filter;

import java.util.HashMap;
import java.util.Map;

import me.alanx.wisteria.core.session.Session;

public class FilterContext {

	private Session session;

	private Object value;

	private Filter next;

	private boolean inInbound = true;

	private FilterChain chain;

	private Map<String, Object> parameters = new HashMap<String, Object>();

	public FilterContext(FilterChain chain) {
		super();
		this.chain = chain;
	}

	/**
	 * @return the chain
	 */
	public FilterChain getChain() {
		return chain;
	}

	@SuppressWarnings("unchecked")
	public <S extends Session> S getSession() {
		return (S) session;
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue() {
		return (T) value;
	}

	public Filter next() {
		return next;
	}

	public <T> void set(String key, T value) {
		this.parameters.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) this.parameters.get(key);
	}

	/**
	 * @param next
	 *            the next to set
	 */
	public void setNext(Filter next) {
		this.next = next;
	}

	/**
	 * @param session
	 *            the session to set
	 */
	public void setSession(Session session) {
		this.session = session;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	public void setInbound() {
		this.inInbound = true;
	}

	public void setOutbound() {
		this.inInbound = false;
	}

	public boolean isInbound() {
		return this.inInbound;
	}

	public boolean isOutbound() {
		return !this.inInbound;
	}

	public <T> T tryGet(String key, Class<?> clazz) {
		T value = get(key);
		if (clazz.isInstance(value)) {
			return value;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T> T tryGetValue(Class<?> clazz) {

		T value = (T) this.value;
		if (clazz.isInstance(value)) {
			return value;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T tryGetSession(Class<? extends Session> clazz) {

		T session = (T) this.session;
		if (clazz.isInstance(session)) {
			return session;
		}
		return null;
	}
}
