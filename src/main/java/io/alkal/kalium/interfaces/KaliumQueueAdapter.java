package io.alkal.kalium.interfaces;

import io.alkal.kalium.internals.QueueListener;

/**
 * Implement this interface to "glue" kalium to any underlying queue like implementation.
 *
 * @author Ziv Salzman
 * Created on 20-Jan-2019
 */
public interface KaliumQueueAdapter {

    /**
     * <code>start</code> is being called by kalium when kalium starts. Dependencies to the implementing class should be injected
     * passed in the constructor. <code>start</code> implementation should take care of things like issuing a connection.
     */
    void start();

    /**
     * <code>post</code> is called by kalium internally and shouldn't called by objects that are external to kalium.
     * Implementaion of <code>post</code> should take care of serializing the object.
     *
     * @param object the event/message to post
     */
    void post(Object object);

    /**
     * set the listener object for the events that are polled from the queue
     * @param queueListener
     */
    void setQueueListener(QueueListener queueListener);
}
