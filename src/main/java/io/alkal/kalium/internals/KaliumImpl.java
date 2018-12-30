package io.alkal.kalium.internals;

import io.alkal.kalium.Kalium;
import io.alkal.kalium.interfaces.KaliumQueueAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class KaliumImpl implements Kalium, QueueListener {
    private Map<Class<?>, Object> reactors;

    private Map<Class<?>, Map<Class<?>, List<Method>>> reactorToObjectTypeToMethodMap;

    private KaliumQueueAdapter queueAdapter;


    @Override
    public void start() {
    }

    @Override
    public void post(Object object) {
        queueAdapter.post(object);
    }

    public <T> T getReactorInstance(Class<T> clazz) {
        return (T) reactors.get(clazz);
    }


    void setReactors(Map<Class<?>, Object> reactors) {
        this.reactors = reactors;
    }

    void setQueueAdapter(KaliumQueueAdapter queueAdapter) {
        this.queueAdapter = queueAdapter;
    }

    void setReactorToObjectTypeToMethodMap(Map<Class<?>, Map<Class<?>, List<Method>>> reactorToObjectTypeToMethodMap) {
        this.reactorToObjectTypeToMethodMap = reactorToObjectTypeToMethodMap;
    }

    @Override
    public Collection<Class<?>> getClassesToListenTo() {
        return reactorToObjectTypeToMethodMap.keySet();
    }

    @Override
    public Collection<Class<?>> getReactorClasses() {
        return reactors.keySet();
    }

    @Override
    public void onObjectReceived(Class<?> reactorClass, Object object) {
        if (!reactorToObjectTypeToMethodMap.containsKey(reactorClass)) return;
        Map<Class<?>, List<Method>> objectTypeToHandlersMap = reactorToObjectTypeToMethodMap.get(reactorClass);
        if (!objectTypeToHandlersMap.containsKey(object.getClass())) return;
        //TODO filter based on annotations
        //TODO run in parallel
        objectTypeToHandlersMap.get(object.getClass()).stream().forEach(method -> {
            Object reactor = getReactorInstance(method.getDeclaringClass());
            try {
                method.invoke(reactor, object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        });

    }
}
