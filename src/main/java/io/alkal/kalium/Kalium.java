package io.alkal.kalium;

import io.alkal.kalium.interfaces.KaliumQueueAdapter;
import io.alkal.kalium.internals.QueueListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class Kalium implements QueueListener {

    private Map<Class<?>,Object> reactors;

    private Map<Class<?>, List<Method>> objectTypeToHandlersMap;
    private KaliumQueueAdapter queueAdapter;



    public static KaliumBuilder Builder() {
        return new KaliumBuilder();
    }

    public void start() {
    }

    public void post(Object object) {
        queueAdapter.post(object);
    }

    public <T> T getReactorInstance(Class<T> clazz) {
        return (T) reactors.get(clazz);
    }

    void setReactors(Map<Class<?>, Object> reactors) {
        this.reactors = reactors;
    }

    public void setQueueAdapter(KaliumQueueAdapter queueAdapter) {
        this.queueAdapter = queueAdapter;
    }

    void setObjectTypeToHandlersMap(Map<Class<?>, List<Method>> objectTypeToHandlersMap) {
        this.objectTypeToHandlersMap = objectTypeToHandlersMap;
    }

    @Override
    public void onObjectReceived(Object object) {
        if(!objectTypeToHandlersMap.keySet().contains(object.getClass())) return;
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
