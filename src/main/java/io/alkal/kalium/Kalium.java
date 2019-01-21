package io.alkal.kalium;

import io.alkal.kalium.internals.KaliumBuilder;

/**
 * Kalium client interface
 *
 * @author Ziv Salzman
 * Created on 20-Jan-2019
 */

public interface Kalium {

    /**
     * Use as follow:
     * <code>
     *     Kalium.Builder().
     *     setQueueAdapter(...).
     *     addReactor(...). //reactor1
     *     addReactor(...). //reactor2
     *     build()
     *
     * </code>
     * @return a builder
     */
    static KaliumBuilder Builder(){
        return new KaliumBuilder();
    }

    /**
     * Call to post a message/event to the queue. Topic will be derived from the class name.
     * @param object the object to post
     */
    void post(Object object);

    /**
     * Call start after building a Kalium object using the builder.
     */
    void start();
}
