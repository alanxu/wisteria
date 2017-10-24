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

import java.util.List;


import com.thomsonreuters.trtn.wisteria.core.filter.Filter.NextFilter;
import com.thomsonreuters.trtn.wisteria.core.session.Session;

/**
 * A container of {@link Filter}s that forwards {@link IoHandler} events
 * to the consisting filters and terminal {@link IoHandler} sequentially.
 * Every {@link IoSession} has its own {@link FilterChain} (1-to-1 relationship).
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public interface FilterChain {
    /**
     * Returns the parent {@link IoSession} of this chain.
     * 
     * @return {@link IoSession}
     */
    Session getSession();
    
    /**
     * This method is useful when the {@link Session} has multiple herachy 
     * @param session
     */
    void setSession(Session session);

    /**
     * Returns the {@link Entry} with the specified <tt>name</tt> in this chain.
     * 
     * @param name The filter's name we are looking for
     * @return <tt>null</tt> if there's no such name in this chain
     */
    Entry getEntry(String name);

    /**
     * Returns the {@link Entry} with the specified <tt>filter</tt> in this chain.
     * 
     * @param filter  The Filter we are looking for
     * @return <tt>null</tt> if there's no such filter in this chain
     */
    Entry getEntry(Filter filter);

    /**
     * Returns the {@link Entry} with the specified <tt>filterType</tt>
     * in this chain. If there's more than one filter with the specified
     * type, the first match will be chosen.
     * 
     * @param filterType The filter class we are looking for
     * @return <tt>null</tt> if there's no such name in this chain
     */
    Entry getEntry(Class<? extends Filter> filterType);

    /**
     * Returns the {@link Filter} with the specified <tt>name</tt> in this chain.
     * 
     * @param name the filter's name
     * @return <tt>null</tt> if there's no such name in this chain
     */
    Filter get(String name);

    /**
     * Returns the {@link Filter} with the specified <tt>filterType</tt>
     * in this chain. If there's more than one filter with the specified
     * type, the first match will be chosen.
     * 
     * @param filterType The filter class
     * @return <tt>null</tt> if there's no such name in this chain
     */
    Filter get(Class<? extends Filter> filterType);

    /**
     * Returns the {@link NextFilter} of the {@link Filter} with the
     * specified <tt>name</tt> in this chain.
     * 
     * @param name The filter's name we want the next filter
     * @return <tt>null</tt> if there's no such name in this chain
     */
    NextFilter getNextFilter(String name);

    /**
     * Returns the {@link NextFilter} of the specified {@link Filter}
     * in this chain.
     * 
     * @param filter The filter for which we want the next filter
     * @return <tt>null</tt> if there's no such name in this chain
     */
    NextFilter getNextFilter(Filter filter);

    /**
     * Returns the {@link NextFilter} of the specified <tt>filterType</tt>
     * in this chain.  If there's more than one filter with the specified
     * type, the first match will be chosen.
     * 
     * @param filterType The Filter class for which we want the next filter
     * @return <tt>null</tt> if there's no such name in this chain
     */
    NextFilter getNextFilter(Class<? extends Filter> filterType);

    /**
     * @return The list of all {@link Entry}s this chain contains.
     */
    List<Entry> getAll();

    /**
     * @return The reversed list of all {@link Entry}s this chain contains.
     */
    List<Entry> getAllReversed();

    /**
     * @param name The filter's name we are looking for
     * 
     * @return <tt>true</tt> if this chain contains an {@link Filter} with the
     * specified <tt>name</tt>.
     */
    boolean contains(String name);

    /**
     * @param filter The filter we are looking for
     * 
     * @return <tt>true</tt> if this chain contains the specified <tt>filter</tt>.
     */
    boolean contains(Filter filter);

    /**
     * @param  filterType The filter's class we are looking for
     * 
     * @return <tt>true</tt> if this chain contains an {@link Filter} of the
     * specified <tt>filterType</tt>.
     */
    boolean contains(Class<? extends Filter> filterType);

    /**
     * Adds the specified filter with the specified name at the beginning of this chain.
     * 
     * @param name The filter's name
     * @param filter The filter to add
     */
    void addFirst(String name, Filter filter);

    /**
     * Adds the specified filter with the specified name at the end of this chain.
     * 
     * @param name The filter's name
     * @param filter The filter to add
     */
    void addLast(String name, Filter filter);

    /**
     * Adds the specified filter with the specified name just before the filter whose name is
     * <code>baseName</code> in this chain.
     * 
     * @param baseName The targeted Filter's name
     * @param name The filter's name
     * @param filter The filter to add
     */
    void addBefore(String baseName, String name, Filter filter);

    /**
     * Adds the specified filter with the specified name just after the filter whose name is
     * <code>baseName</code> in this chain.
     * 
     * @param baseName The targeted Filter's name
     * @param name The filter's name
     * @param filter The filter to add
     */
    void addAfter(String baseName, String name, Filter filter);

    /**
     * Replace the filter with the specified name with the specified new
     * filter.
     *
     * @param name The name of the filter we want to replace
     * @param newFilter The new filter
     * @return the old filter
     */
    Filter replace(String name, Filter newFilter);

    /**
     * Replace the filter with the specified name with the specified new
     * filter.
     *
     * @param oldFilter The filter we want to replace
     * @param newFilter The new filter
     */
    void replace(Filter oldFilter, Filter newFilter);

    /**
     * Replace the filter of the specified type with the specified new
     * filter.  If there's more than one filter with the specified type,
     * the first match will be replaced.
     *
     * @param oldFilterType The filter class we want to replace
     * @param newFilter The new filter
     */
    Filter replace(Class<? extends Filter> oldFilterType, Filter newFilter);

    /**
     * Removes the filter with the specified name from this chain.
     * 
     * @param name The name of the filter to remove
     * @return The removed filter
     */
    Filter remove(String name);

    /**
     * Replace the filter with the specified name with the specified new
     * filter.
     *
     * @param name The filter to remove
     */
    void remove(Filter filter);

    /**
     * Replace the filter of the specified type with the specified new
     * filter.  If there's more than one filter with the specified type,
     * the first match will be replaced.
     *
     * @param name The filter class to remove
     * @return The removed filter
     */
    Filter remove(Class<? extends Filter> filterType);

    /**
     * Removes all filters added to this chain.
     */
    void clear() throws Exception;

    /**
     * Fires a {@link IoHandler#onSessionCreated(IoSession)} event. Most users don't need to
     * call this method at all. Please use this method only when you implement a new transport
     * or fire a virtual event.
     */
    public void fireSessionCreated();

    /**
     * Fires a {@link IoHandler#sessionOpened(IoSession)} event. Most users don't need to call
     * this method at all. Please use this method only when you implement a new transport or
     * fire a virtual event.
     */
    public void fireSessionOpened();

    /**
     * Fires a {@link IoHandler#onSessionClosed(IoSession)} event. Most users don't need to call
     * this method at all. Please use this method only when you implement a new transport or
     * fire a virtual event.
     */
    public void fireSessionClosed();

    /**
     * Fires a {@link IoHandler#sessionIdle(IoSession, IdleStatus)} event. Most users don't
     * need to call this method at all. Please use this method only when you implement a new
     * transport or fire a virtual event.
     * 
     * @param status The current status to propagate
     */
    //public void fireSessionIdle(IdleStatus status);

    /**
     * Fires a {@link IoHandler#messageReceived(Object)} event. Most users don't need to
     * call this method at all. Please use this method only when you implement a new transport
     * or fire a virtual event.
     * 
     * @param message The received message
     */
    public void fireMessageReceivedFromClient(Object message);

    /**
     * Fires a {@link IoHandler#messageSent(IoSession)} event. Most users don't need to call
     * this method at all. Please use this method only when you implement a new transport or
     * fire a virtual event.
     * 
     * @param request The sent request
     */
    public void fireMessageSent(Object request);

    /**
     * Fires a {@link IoHandler#onExceptionCaught(IoSession, Throwable)} event. Most users don't
     * need to call this method at all. Please use this method only when you implement a new
     * transport or fire a virtual event.
     * 
     * @param cause The exception cause
     */
    public void fireExceptionCaught(Throwable cause);

    /**
     * Fires a {@link IoSession#write(Object)} event. Most users don't need to call this
     * method at all. Please use this method only when you implement a new transport or fire a
     * virtual event.
     * 
     * @param writeRequest The message to write
     */
    public void fireFilterWrite(Object writeRequest);

    /**
     * Fires a {@link IoSession#close()} event. Most users don't need to call this method at
     * all. Please use this method only when you implement a new transport or fire a virtual
     * event.
     */
    public void fireFilterClose();

    /**
     * Represents a name-filter pair that an {@link FilterChain} contains.
     *
     * @author <a href="http://mina.apache.org">Apache MINA Project</a>
     */
    public interface Entry {
        /**
         * Returns the name of the filter.
         */
        String getName();

        /**
         * Returns the filter.
         */
        Filter getFilter();

        /**
         * @return The {@link NextFilter} of the filter.
         */
        NextFilter getNextFilter();
        
        /**
         * Adds the specified filter with the specified name just before this entry.
         */
        void addBefore(String name, Filter filter);

        /**
         * Adds the specified filter with the specified name just after this entry.
         */
        void addAfter(String name, Filter filter);

        /**
         * Replace the filter of this entry with the specified new filter.
         */
        void replace(Filter newFilter);
        
        /**
         * Removes this entry from the chain it belongs to.
         */
        void remove();
    }
}
