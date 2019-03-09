package io.alkal.kalium.internals;

import java.util.Collection;
import java.util.Map;

/**
 * <tt>QueueListener</tt> provides the Queue Adapter with information on which topics to listen to, and which groups
 * to assign reactor classes to.
 * By definition, only one instance of a reactor class, across multiple consumers, should process a message.
 * In addition, the interface provide a way to be notified whenever a message arrive
 *
 * @author Ziv Salzman
 * Created on 20-Jan-2019
 */
public interface QueueListener {

    /**
     *
     * @return map from a processingGroup id, AKA a reactor id, to a list of topics, AKA objectTypes
     */
    Map<String, Collection<Class>> getReactorIdsToObjectTypesMap();

    void onObjectReceived(String reactorId, Object object);
}
