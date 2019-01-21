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

    //TODO should be changed to groupsAndTopicsToListenTo. we should use strings
    public Map<Class<?>, Collection<Class<?>>> getReactorToObjectTypeMap();

    //TODO we should use group/subscription group instead of reactor
    void onObjectReceived(Class<?> reactorClass, Object object);
}
