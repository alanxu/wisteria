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

import com.thomsonreuters.trtn.wisteria.core.session.Session;



/**
 * An adapter class for {@link Filter}.  You can extend
 * this class and selectively override required event filter methods only.  All
 * methods forwards events to the next filter by default.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class FilterBase implements Filter {
    /**
     * {@inheritDoc}
     */
    public void init() throws Exception {
    }

    /**
     * {@inheritDoc}
     */
    public void destroy() throws Exception {
    }

    /**
     * {@inheritDoc}
     */
    public void onPreAdd(FilterChain parent, String name,
        NextFilter nextFilter) throws Exception {
    }

    /**
     * {@inheritDoc}
     */
    public void onPostAdd(FilterChain parent, String name,
        NextFilter nextFilter) throws Exception {
    }

    /**
     * {@inheritDoc}
     */
    public void onPreRemove(FilterChain parent, String name,
        NextFilter nextFilter) throws Exception {
    }

    /**
     * {@inheritDoc}
     */
    public void onPostRemove(FilterChain parent, String name,
        NextFilter nextFilter) throws Exception {
    }

    /**
     * {@inheritDoc}
     */
    public void sessionCreated(NextFilter nextFilter, Session session)
            throws Exception {
        nextFilter.sessionCreated(session);
    }

    /**
     * {@inheritDoc}
     */
    public void sessionOpened(NextFilter nextFilter, Session session)
            throws Exception {
        nextFilter.sessionOpened(session);
    }

    /**
     * {@inheritDoc}
     */
    public void sessionClosed(NextFilter nextFilter, Session session)
            throws Exception {
        nextFilter.sessionClosed(session);
    }

    /**
     * {@inheritDoc}
     */
/*    public void sessionIdle(NextFilter nextFilter, Session session,
            IdleStatus status) throws Exception {
        nextFilter.sessionIdle(session, status);
    }*/

    /**
     * {@inheritDoc}
     */
    public void exceptionCaught(NextFilter nextFilter, Session session,
            Throwable cause) throws Exception {
        nextFilter.exceptionCaught(session, cause);
    }

    /**
     * {@inheritDoc}
     */
    public void clientMessageReceived(NextFilter nextFilter, Session session,
            Object message) throws Exception {
        nextFilter.messageReceived(session, message);
    }

    /**
     * {@inheritDoc}
     */
    public void clientMessageSent(NextFilter nextFilter, Session session,
            Object writeRequest) throws Exception {
        nextFilter.messageSent(session, writeRequest);
    }

    /**
     * {@inheritDoc}
     */
    public void filterWrite(NextFilter nextFilter, Session session,
            Object writeRequest) throws Exception {
        nextFilter.filterWrite(session, writeRequest);
    }

    /**
     * {@inheritDoc}
     */
    public void filterClose(NextFilter nextFilter, Session session)
            throws Exception {
        nextFilter.filterClose(session);
    }
    
    public String toString() {
        return this.getClass().getSimpleName();
    }
}