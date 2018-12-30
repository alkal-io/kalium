package io.alkal.kalium.internals;

import io.alkal.kalium.Kalium;
import io.alkal.kalium.interfaces.KaliumQueueAdapter;
import io.alkal.kalium.internals.utils.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class KaliumBuilder {

    private KaliumQueueAdapter queueAdapter = null;
    private List<Object> reactors = new LinkedList<>();

    public KaliumBuilder setQueueAdapter(KaliumQueueAdapter queueAdapter) {
        this.queueAdapter = queueAdapter;
        return this;
    }

    public KaliumBuilder addReactor(Object reactor) {
        reactors.add(reactor);
        return this;
    }

    public Kalium build() {
        KaliumImpl kalium = new KaliumImpl();
        Map<Class<?>, Object> reactorsMap = new HashMap<>();
        Map<Class<?>,Map<Class<?>, List<Method>>>  reactorToObjectTypeToMethodMap = new HashMap<>();
        reactors.forEach(reactor -> {

            Class<?> reactorClass = reactor.getClass();
            Map<Class<?>, List<Method>> objectTypeToHandlersMap = new HashMap<>();
            reactorToObjectTypeToMethodMap.put(reactorClass, objectTypeToHandlersMap);
            reactorsMap.put(reactorClass, reactor);
            ReflectionUtils.getMethodsAnnotatedWithOn(reactorClass).forEach(method -> {
                        assert method.getParameterCount() == 1;
                        Class parameter = method.getParameterTypes()[0];
                        List<Method> handlers = objectTypeToHandlersMap.get(parameter);
                        if (handlers == null) {
                            handlers = new LinkedList<>();
                            objectTypeToHandlersMap.put(parameter, handlers);
                        }
                        handlers.add(method);

                    }
            );

        });

        queueAdapter.setQueueListener(kalium);
        kalium.setQueueAdapter(queueAdapter);
        kalium.setReactors(reactorsMap);
        kalium.setReactorToObjectTypeToMethodMap(reactorToObjectTypeToMethodMap);
        return kalium;
    }
}
