package io.alkal.kalium.internals;

import java.util.Collection;

public interface QueueListener {

    Collection<Class<?>> getClassesToListenTo();

    Collection<Class<?>> getReactorClasses();

    void onObjectReceived(Class<?> reactorClass, Object object);
}
