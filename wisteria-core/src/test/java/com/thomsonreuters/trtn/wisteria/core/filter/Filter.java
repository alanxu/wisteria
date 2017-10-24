package com.thomsonreuters.trtn.wisteria.core.filter;


import com.thomsonreuters.trtn.wisteria.core.session.Session;

public interface Filter {

    void init() throws Exception;

    void destroy() throws Exception;

    void onPreAdd(FilterChain parent, String name, NextFilter nextFilter)
            throws Exception;

    void onPostAdd(FilterChain parent, String name, NextFilter nextFilter)
            throws Exception;

    void onPreRemove(FilterChain parent, String name, NextFilter nextFilter)
            throws Exception;

    void onPostRemove(FilterChain parent, String name, NextFilter nextFilter)
            throws Exception;

    void sessionCreated(NextFilter nextFilter, Session session)
            throws Exception;

    void sessionOpened(NextFilter nextFilter, Session session)
            throws Exception;

    void sessionClosed(NextFilter nextFilter, Session session)
            throws Exception;

    /*void sessionIdle(NextFilter nextFilter, Session session, IdleStatus status)
            throws Exception;*/

    void exceptionCaught(NextFilter nextFilter, Session session,
            Throwable cause) throws Exception;

    void clientMessageReceived(NextFilter nextFilter, Session session,
            Object message) throws Exception;

    void clientMessageSent(NextFilter nextFilter, Session session,
            Object message) throws Exception;

    void filterClose(NextFilter nextFilter, Session session) throws Exception;

    void filterWrite(NextFilter nextFilter, Session session,
            Object writeRequest) throws Exception;

    public interface NextFilter {

        void sessionCreated(Session session);

        void sessionOpened(Session session);

        void sessionClosed(Session session);

        //void sessionIdle(Session session, IdleStatus status);

        void exceptionCaught(Session session, Throwable cause);

        void messageReceived(Session session, Object message);

        void messageSent(Session session, Object writeRequest);

        void filterWrite(Session session, Object writeRequest);

        void filterClose(Session session);
        
    }
}
