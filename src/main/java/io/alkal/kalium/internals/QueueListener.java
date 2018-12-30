package io.alkal.kalium.internals;

import java.util.Collection;
import java.util.Map;

public interface QueueListener {

    //TODO should be changed to groupsAndTopicsToListenTo. we should use strings
    public Map<Class<?>, Collection<Class<?>>> getReactorToObjectTypeMap();

    //TODO we should use group/subscription group instead of reactor
    void onObjectReceived(Class<?> reactorClass, Object object);
}
