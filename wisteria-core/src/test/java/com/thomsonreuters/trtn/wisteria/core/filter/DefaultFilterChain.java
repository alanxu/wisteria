/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package com.thomsonreuters.trtn.wisteria.core.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thomsonreuters.trtn.wisteria.core.filter.Filter.NextFilter;
import com.thomsonreuters.trtn.wisteria.core.session.Session;
import com.thomsonreuters.trtn.wisteria.core.session.listener.SessionErrorListener;
import com.thomsonreuters.trtn.wisteria.core.session.listener.SessionMessageListener;
import com.thomsonreuters.trtn.wisteria.core.session.listener.SessionStatusListener;



/**
 * A default implementation of {@link FilterChain} that provides
 * all operations for developers who want to implement their own
 * transport layer once used with {@link AbstractSession}.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class DefaultFilterChain implements FilterChain {


    /** The associated session */
    private Session session;

    private final Map<String, Entry> name2entry = new ConcurrentHashMap<String, Entry>();

    /** The chain head */
    private final EntryImpl head;

    /** The chain tail */
    private final EntryImpl tail;

    /** The logger for this class */
    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultFilterChain.class);


    /**
     * Create a new default chain, associated with a session. It will only contain a
     * HeadFilter and a TailFilter.
     *
     * @param session The session associated with the created filter chain
     */
    public DefaultFilterChain(Session session) {
        if (session == null) {
            throw new IllegalArgumentException("session");
        }

        this.session = session;
        head = new EntryImpl(null, null, "head", new HeadFilter());
        tail = new EntryImpl(head, null, "tail", new TailFilter());
        head.nextEntry = tail;
    }

    public Session getSession() {
        return session;
    }

    public Entry getEntry(String name) {
        Entry e = name2entry.get(name);
        if (e == null) {
            return null;
        }
        return e;
    }

    public Entry getEntry(Filter filter) {
        EntryImpl e = head.nextEntry;
        while (e != tail) {
            if (e.getFilter() == filter) {
                return e;
            }
            e = e.nextEntry;
        }
        return null;
    }

    public Entry getEntry(Class<? extends Filter> filterType) {
        EntryImpl e = head.nextEntry;
        while (e != tail) {
            if (filterType.isAssignableFrom(e.getFilter().getClass())) {
                return e;
            }
            e = e.nextEntry;
        }
        return null;
    }

    public Filter get(String name) {
        Entry e = getEntry(name);
        if (e == null) {
            return null;
        }

        return e.getFilter();
    }

    public Filter get(Class<? extends Filter> filterType) {
        Entry e = getEntry(filterType);
        if (e == null) {
            return null;
        }

        return e.getFilter();
    }

    public NextFilter getNextFilter(String name) {
        Entry e = getEntry(name);
        if (e == null) {
            return null;
        }

        return e.getNextFilter();
    }

    public NextFilter getNextFilter(Filter filter) {
        Entry e = getEntry(filter);
        if (e == null) {
            return null;
        }

        return e.getNextFilter();
    }

    public NextFilter getNextFilter(Class<? extends Filter> filterType) {
        Entry e = getEntry(filterType);
        if (e == null) {
            return null;
        }

        return e.getNextFilter();
    }

    public synchronized void addFirst(String name, Filter filter) {
        checkAddable(name);
        register(head, name, filter);
    }

    public synchronized void addLast(String name, Filter filter) {
        checkAddable(name);
        register(tail.prevEntry, name, filter);
    }

    public synchronized void addBefore(String baseName, String name,
            Filter filter) {
        EntryImpl baseEntry = checkOldName(baseName);
        checkAddable(name);
        register(baseEntry.prevEntry, name, filter);
    }

    public synchronized void addAfter(String baseName, String name,
            Filter filter) {
        EntryImpl baseEntry = checkOldName(baseName);
        checkAddable(name);
        register(baseEntry, name, filter);
    }

    public synchronized Filter remove(String name) {
        EntryImpl entry = checkOldName(name);
        deregister(entry);
        return entry.getFilter();
    }

    public synchronized void remove(Filter filter) {
        EntryImpl e = head.nextEntry;
        while (e != tail) {
            if (e.getFilter() == filter) {
                deregister(e);
                return;
            }
            e = e.nextEntry;
        }
        throw new IllegalArgumentException("Filter not found: "
                + filter.getClass().getName());
    }

    public synchronized Filter remove(Class<? extends Filter> filterType) {
        EntryImpl e = head.nextEntry;
        while (e != tail) {
            if (filterType.isAssignableFrom(e.getFilter().getClass())) {
                Filter oldFilter = e.getFilter();
                deregister(e);
                return oldFilter;
            }
            e = e.nextEntry;
        }
        throw new IllegalArgumentException("Filter not found: "
                + filterType.getName());
    }

    public synchronized Filter replace(String name, Filter newFilter) {
        EntryImpl entry = checkOldName(name);
        Filter oldFilter = entry.getFilter();
        entry.setFilter(newFilter);
        return oldFilter;
    }

    public synchronized void replace(Filter oldFilter, Filter newFilter) {
        EntryImpl e = head.nextEntry;
        while (e != tail) {
            if (e.getFilter() == oldFilter) {
                e.setFilter(newFilter);
                return;
            }
            e = e.nextEntry;
        }
        throw new IllegalArgumentException("Filter not found: "
                + oldFilter.getClass().getName());
    }

    public synchronized Filter replace(
            Class<? extends Filter> oldFilterType, Filter newFilter) {
        EntryImpl e = head.nextEntry;
        while (e != tail) {
            if (oldFilterType.isAssignableFrom(e.getFilter().getClass())) {
                Filter oldFilter = e.getFilter();
                e.setFilter(newFilter);
                return oldFilter;
            }
            e = e.nextEntry;
        }
        throw new IllegalArgumentException("Filter not found: "
                + oldFilterType.getName());
    }

    public synchronized void clear() throws Exception {
        List<FilterChain.Entry> l = new ArrayList<FilterChain.Entry>(
                name2entry.values());
        for (FilterChain.Entry entry : l) {
            try {
                deregister((EntryImpl) entry);
            } catch (Exception e) {
                throw new FilterLifeCycleException("clear(): "
                        + entry.getName() + " in " + getSession(), e);
            }
        }
    }

    private void register(EntryImpl prevEntry, String name, Filter filter) {
        EntryImpl newEntry = new EntryImpl(prevEntry, prevEntry.nextEntry,
                name, filter);

        try {
            filter.onPreAdd(this, name, newEntry.getNextFilter());
        } catch (Exception e) {
            throw new FilterLifeCycleException("onPreAdd(): " + name + ':'
                    + filter + " in " + getSession(), e);
        }

        prevEntry.nextEntry.prevEntry = newEntry;
        prevEntry.nextEntry = newEntry;
        name2entry.put(name, newEntry);

        try {
            filter.onPostAdd(this, name, newEntry.getNextFilter());
        } catch (Exception e) {
            deregister0(newEntry);
            throw new FilterLifeCycleException("onPostAdd(): " + name + ':'
                    + filter + " in " + getSession(), e);
        }
    }

    private void deregister(EntryImpl entry) {
        Filter filter = entry.getFilter();

        try {
            filter.onPreRemove(this, entry.getName(), entry.getNextFilter());
        } catch (Exception e) {
            throw new FilterLifeCycleException("onPreRemove(): "
                    + entry.getName() + ':' + filter + " in " + getSession(), e);
        }

        deregister0(entry);

        try {
            filter.onPostRemove(this, entry.getName(), entry.getNextFilter());
        } catch (Exception e) {
            throw new FilterLifeCycleException("onPostRemove(): "
                    + entry.getName() + ':' + filter + " in " + getSession(), e);
        }
    }

    private void deregister0(EntryImpl entry) {
        EntryImpl prevEntry = entry.prevEntry;
        EntryImpl nextEntry = entry.nextEntry;
        prevEntry.nextEntry = nextEntry;
        nextEntry.prevEntry = prevEntry;

        name2entry.remove(entry.name);
    }

    /**
     * Throws an exception when the specified filter name is not registered in this chain.
     *
     * @return An filter entry with the specified name.
     */
    private EntryImpl checkOldName(String baseName) {
        EntryImpl e = (EntryImpl) name2entry.get(baseName);
        if (e == null) {
            throw new IllegalArgumentException("Filter not found:" + baseName);
        }
        return e;
    }

    /**
     * Checks the specified filter name is already taken and throws an exception if already taken.
     */
    private void checkAddable(String name) {
        if (name2entry.containsKey(name)) {
            throw new IllegalArgumentException(
                    "Other filter is using the same name '" + name + "'");
        }
    }

    public void fireSessionCreated() {
        Entry head = this.head;
        callNextSessionCreated(head, session);
    }

    private void callNextSessionCreated(Entry entry, Session session) {
        try {
            Filter filter = entry.getFilter();
            NextFilter nextFilter = entry.getNextFilter();
            filter.sessionCreated(nextFilter, session);
        } catch (Throwable e) {
            fireExceptionCaught(e);
        }
    }

    public void fireSessionOpened() {
        Entry head = this.head;
        callNextSessionOpened(head, session);
    }

    private void callNextSessionOpened(Entry entry, Session session) {
        try {
            Filter filter = entry.getFilter();
            NextFilter nextFilter = entry.getNextFilter();
            filter.sessionOpened(nextFilter, session);
        } catch (Throwable e) {
            fireExceptionCaught(e);
        }
    }

    public void fireSessionClosed() {
        
        try {
            session.close();
        } catch (Throwable t) {
            fireExceptionCaught(t);
        }

        // And start the chain.
        Entry head = this.head;
        callNextSessionClosed(head, session);
    }

    private void callNextSessionClosed(Entry entry, Session session) {
        try {
            Filter filter = entry.getFilter();
            NextFilter nextFilter = entry.getNextFilter();
            filter.sessionClosed(nextFilter, session);
        } catch (Throwable e) {
            fireExceptionCaught(e);
        }
    }

/*    public void fireSessionIdle(IdleStatus status) {
        session.increaseIdleCount(status, System.currentTimeMillis());
        Entry head = this.head;
        callNextSessionIdle(head, session, status);
    }

    private void callNextSessionIdle(Entry entry, Session session,
            IdleStatus status) {
        try {
            IoFilter filter = entry.getFilter();
            NextFilter nextFilter = entry.getNextFilter();
            filter.sessionIdle(nextFilter, session,
                    status);
        } catch (Throwable e) {
            fireExceptionCaught(e);
        }
    }*/

    public void fireMessageReceivedFromClient(Object message) {        
        callNextMessageReceived(head, session, message);
    }

    private void callNextMessageReceived(Entry entry, Session session,
            Object message) {
        try {
            Filter filter = entry.getFilter();
            NextFilter nextFilter = entry.getNextFilter();
            filter.clientMessageReceived(nextFilter, session,
                    message);
        } catch (Throwable e) {
            fireExceptionCaught(e);
        }
    }

    public void fireMessageSent(Object request) {

        Entry head = this.head;
        callNextMessageSent(head, session, request);
    }

    private void callNextMessageSent(Entry entry, Session session,
            Object writeRequest) {
        try {
            Filter filter = entry.getFilter();
            NextFilter nextFilter = entry.getNextFilter();
            filter.clientMessageSent(nextFilter, session,
                    writeRequest);
        } catch (Throwable e) {
            fireExceptionCaught(e);
        }
    }

    public void fireExceptionCaught(Throwable cause) {
        Entry head = this.head;
        callNextExceptionCaught(head, session, cause);
    }

    private void callNextExceptionCaught(Entry entry, Session session,
            Throwable cause) {
        //boolean sessionIsBeingCreated = true;
        //if (!sessionIsBeingCreated) {
            try {
                Filter filter = entry.getFilter();
                NextFilter nextFilter = entry.getNextFilter();
                filter.exceptionCaught(nextFilter,
                        session, cause);
            } catch (Throwable e) {
                LOGGER
                        .warn(
                                "Unexpected exception from exceptionCaught handler.",
                                e);
            }
       /* } else {
            session.close();
        }*/
    }

    public void fireFilterWrite(Object writeRequest) {
        Entry tail = this.tail;
        callPreviousFilterWrite(tail, session, writeRequest);
    }

    private void callPreviousFilterWrite(Entry entry, Session session,
            Object writeRequest) {
        try {
            Filter filter = entry.getFilter();
            NextFilter nextFilter = entry.getNextFilter();
            filter.filterWrite(nextFilter, session, writeRequest);
        } catch (Throwable e) {
            fireExceptionCaught(e);
        }
    }

    public void fireFilterClose() {
        Entry tail = this.tail;
        callPreviousFilterClose(tail, session);
    }

    private void callPreviousFilterClose(Entry entry, Session session) {
        try {
            Filter filter = entry.getFilter();
            NextFilter nextFilter = entry.getNextFilter();
            filter.filterClose(nextFilter, session);
        } catch (Throwable e) {
            fireExceptionCaught(e);
        }
    }

    public List<Entry> getAll() {
        List<Entry> list = new ArrayList<Entry>();
        EntryImpl e = head.nextEntry;
        while (e != tail) {
            list.add(e);
            e = e.nextEntry;
        }

        return list;
    }

    public List<Entry> getAllReversed() {
        List<Entry> list = new ArrayList<Entry>();
        EntryImpl e = tail.prevEntry;
        while (e != head) {
            list.add(e);
            e = e.prevEntry;
        }
        return list;
    }

    public boolean contains(String name) {
        return getEntry(name) != null;
    }

    public boolean contains(Filter filter) {
        return getEntry(filter) != null;
    }

    public boolean contains(Class<? extends Filter> filterType) {
        return getEntry(filterType) != null;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("{ ");

        boolean empty = true;

        EntryImpl e = head.nextEntry;
        while (e != tail) {
            if (!empty) {
                buf.append(", ");
            } else {
                empty = false;
            }

            buf.append('(');
            buf.append(e.getName());
            buf.append(':');
            buf.append(e.getFilter());
            buf.append(')');

            e = e.nextEntry;
        }

        if (empty) {
            buf.append("empty");
        }

        buf.append(" }");

        return buf.toString();
    }

    private class HeadFilter extends FilterBase {
        @SuppressWarnings("unchecked")
        @Override
        public void filterWrite(NextFilter nextFilter, Session session,
                Object writeRequest) throws Exception {

           // really write messages
        	session.getTransport().sendDirectly(writeRequest);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void filterClose(NextFilter nextFilter, Session session)
                throws Exception {
            // TODO really clear session
        }
    }

    private static class TailFilter extends FilterBase {
        @Override
        public void sessionCreated(NextFilter nextFilter, Session session)
                throws Exception {
        	Set<SessionStatusListener> sessionMessageListeners = session.getSessionListeners(SessionStatusListener.class);
        	for(SessionStatusListener l : sessionMessageListeners){
        		l.onSessionCreated(session);
        	}
        }

        @Override
        public void sessionOpened(NextFilter nextFilter, Session session)
                throws Exception {
        	Set<SessionStatusListener> sessionMessageListeners = session.getSessionListeners(SessionStatusListener.class);
        	for(SessionStatusListener l : sessionMessageListeners){
        		l.onSessionActivated(session);
        	}
        }

        @Override
        public void sessionClosed(NextFilter nextFilter, Session session)
                throws Exception {
        	Set<SessionStatusListener> sessionMessageListeners = session.getSessionListeners(SessionStatusListener.class);
        	for(SessionStatusListener l : sessionMessageListeners){
        		l.onSessionClosed(session);
        	}
        }

/*        @Override
        public void sessionIdle(NextFilter nextFilter, Session session,
                IdleStatus status) throws Exception {
            session.getHandler().sessionIdle(session, status);
        }*/

        @Override
        public void exceptionCaught(NextFilter nextFilter, Session session,
                Throwable cause) throws Exception {
        	Set<SessionErrorListener> sessionMessageListeners = session.getSessionListeners(SessionErrorListener.class);
        	for(SessionErrorListener l : sessionMessageListeners){
        		l.onExceptionCaught(session, cause);
        	}
        }

        @Override
        public void clientMessageReceived(NextFilter nextFilter, Session session,
                Object message) throws Exception {
        	Set<SessionMessageListener> sessionMessageListeners = session.getSessionListeners(SessionMessageListener.class);
        	for(SessionMessageListener l : sessionMessageListeners){
        		l.onMessageReceived(session, message);
        	}
        }

        @Override
        public void clientMessageSent(NextFilter nextFilter, Session session,
                Object message) throws Exception {
        	Set<SessionMessageListener> sessionMessageListeners = session.getSessionListeners(SessionMessageListener.class);
        	for(SessionMessageListener l : sessionMessageListeners){
        		l.onMessageSent(session, message);
        	}
        }

        @Override
        public void filterWrite(NextFilter nextFilter, Session session,
                Object writeRequest) throws Exception {
            nextFilter.filterWrite(session, writeRequest);
        }

        @Override
        public void filterClose(NextFilter nextFilter, Session session)
                throws Exception {
            nextFilter.filterClose(session);
        }
    }

    private class EntryImpl implements Entry {
        private EntryImpl prevEntry;

        private EntryImpl nextEntry;

        private final String name;

        private Filter filter;

        private final NextFilter nextFilter;

        private EntryImpl(EntryImpl prevEntry, EntryImpl nextEntry,
                String name, Filter filter) {
            if (filter == null) {
                throw new IllegalArgumentException("filter");
            }
            if (name == null) {
                throw new IllegalArgumentException("name");
            }

            this.prevEntry = prevEntry;
            this.nextEntry = nextEntry;
            this.name = name;
            this.filter = filter;
            this.nextFilter = new NextFilter() {
                public void sessionCreated(Session session) {
                    Entry nextEntry = EntryImpl.this.nextEntry;
                    callNextSessionCreated(nextEntry, session);
                }

                public void sessionOpened(Session session) {
                    Entry nextEntry = EntryImpl.this.nextEntry;
                    callNextSessionOpened(nextEntry, session);
                }

                public void sessionClosed(Session session) {
                    Entry nextEntry = EntryImpl.this.nextEntry;
                    callNextSessionClosed(nextEntry, session);
                }

                /*public void sessionIdle(Session session, IdleStatus status) {
                    Entry nextEntry = EntryImpl.this.nextEntry;
                    callNextSessionIdle(nextEntry, session, status);
                }*/

                public void exceptionCaught(Session session, Throwable cause) {
                    Entry nextEntry = EntryImpl.this.nextEntry;
                    callNextExceptionCaught(nextEntry, session, cause);
                }

                public void messageReceived(Session session, Object message) {
                    Entry nextEntry = EntryImpl.this.nextEntry;
                    callNextMessageReceived(nextEntry, session, message);
                }

                public void messageSent(Session session,
                        Object writeRequest) {
                    Entry nextEntry = EntryImpl.this.nextEntry;
                    callNextMessageSent(nextEntry, session, writeRequest);
                }

                public void filterWrite(Session session,
                        Object writeRequest) {
                    Entry nextEntry = EntryImpl.this.prevEntry;
                    callPreviousFilterWrite(nextEntry, session, writeRequest);
                }

                public void filterClose(Session session) {
                    Entry nextEntry = EntryImpl.this.prevEntry;
                    callPreviousFilterClose(nextEntry, session);
                }

                public String toString() {
                    return EntryImpl.this.nextEntry.name;
                }
            };
        }

        public String getName() {
            return name;
        }

        public Filter getFilter() {
            return filter;
        }

        private void setFilter(Filter filter) {
            if (filter == null) {
                throw new IllegalArgumentException("filter");
            }

            this.filter = filter;
        }

        public NextFilter getNextFilter() {
            return nextFilter;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            // Add the current filter
            sb.append("('").append(getName()).append('\'');

            // Add the previous filter
            sb.append(", prev: '");

            if (prevEntry != null) {
                sb.append(prevEntry.name);
                sb.append(':');
                sb.append(prevEntry.getFilter().getClass().getSimpleName());
            } else {
                sb.append("null");
            }

            // Add the next filter
            sb.append("', next: '");

            if (nextEntry != null) {
                sb.append(nextEntry.name);
                sb.append(':');
                sb.append(nextEntry.getFilter().getClass().getSimpleName());
            } else {
                sb.append("null");
            }

            sb.append("')");
            return sb.toString();
        }

        public void addAfter(String name, Filter filter) {
            DefaultFilterChain.this.addAfter(getName(), name, filter);
        }

        public void addBefore(String name, Filter filter) {
            DefaultFilterChain.this.addBefore(getName(), name, filter);
        }

        public void remove() {
            DefaultFilterChain.this.remove(getName());
        }

        public void replace(Filter newFilter) {
            DefaultFilterChain.this.replace(getName(), newFilter);
        }
    }

	@Override
	public void setSession(Session session) {
		this.session = session;		
	}
}
