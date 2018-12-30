package io.alkal.kalium.internals;

import java.util.Collection;
import java.util.Map;

public interface QueueListener {

    public Map<Class<?>, Collection<Class<?>>> getReactorToObjectTypeMap();

    void onObjectReceived(Class<?> reactorClass, Object object);
}
