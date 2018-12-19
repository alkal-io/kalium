package io.alkal.kalium;

import io.alkal.kalium.interfaces.KaliumQueueAdapter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class KaliumBuilder {

    private KaliumQueueAdapter queueAdapter = null;
    private List<Class<?>> reactors = new LinkedList<>();

    public KaliumBuilder setQueueAdapter(KaliumQueueAdapter queueAdapter) {
        this.queueAdapter = queueAdapter;
        return this;
    }

    public KaliumBuilder addReactor(Class reactorClazz) {
        reactors.add(reactorClazz);
        return this;
    }

    public Kalium build() {
        Kalium kalium = new Kalium();
        Map<Class<?>, Object> reactorsMap = new HashMap<>();
        Map<Class<?>, List<Method>> objectTypeToHandlersMap = new HashMap<>();
        for (Class reactorClass : reactors) {
            try {
                reactorsMap.put(reactorClass, reactorClass.newInstance());
                for (Method method:reactorClass.getDeclaredMethods()) {
                    assert method.getParameterCount() == 1;
                    Class parameter = method.getParameterTypes()[0];
                    List<Method> handlers = objectTypeToHandlersMap.get(parameter);
                    if(handlers == null) {
                        handlers = new LinkedList<>();
                        objectTypeToHandlersMap.put(parameter, handlers);
                    }
                    handlers.add(method);
                }
            } catch (InstantiationException ie) {

            } catch (IllegalAccessException iae) {

            }

        }
        queueAdapter.setQueueListener(kalium);
        kalium.setQueueAdapter(queueAdapter);
        kalium.setReactors(reactorsMap);
        kalium.setObjectTypeToHandlersMap(objectTypeToHandlersMap);
        return kalium;
    }
}
