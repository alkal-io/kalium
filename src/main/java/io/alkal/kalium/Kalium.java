package io.alkal.kalium;

import io.alkal.kalium.exceptions.KaliumException;
import io.alkal.kalium.internals.KaliumBuilder;

import java.util.function.Consumer;

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
     *     addReaction(...). //reaction1
     *     addReaction(...). //reaction2
     *     build()
     *
     * </code>
     *
     * @return a builder
     */
    static KaliumBuilder Builder() {
        return new KaliumBuilder();
    }

    /**
     * Call to post a message/event to the queue. Topic will be derived from the class name.
     *
     * @param object the object to post
     */
    void post(Object object);

    /**
     * Call start after building a Kalium object using the builder.
     */
    void start();

    /**
     * Stops the Kalium client and terminate any external connections
     */
    void stop();


    /**
     * Add a reaction object
     * @param reaction an instance of a class with methods annotated with @On annotation
     */
    void addReaction(Object reaction);

    /**
     * Defines a reaction to an object of type T. Use this method for events/object that suppose to be processed by
     * all kalium instances that "listen" to the same type of objects, AKA pub-sub processing.
     * @param objectType the class of the object that is processed
     * @param consumer   the actual processing logic in form of a lambda function
     * @param <T>        the class of the object that is processed
     */
    <T> void on(Class<T> objectType, Consumer<T> consumer) throws KaliumException;


    /**
     * Defines a reaction to an object of type T. Use this method for events/object that suppose to be processed by
     * only one kalium instances with the same processingGroup that "listen" to the same type of objects, AKA p2p processing.
     *
     * @param objectType      the class of the object that is processed
     * @param consumer        the actual processing logic in form of a lambda function
     * @param processingGroup the group name that all instances share
     * @param <T>             the class of the object that is processed
     */
    <T> void on(Class<T> objectType, Consumer<T> consumer, String processingGroup) throws KaliumException;
}
