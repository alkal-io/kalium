package io.alkal.kalium.internals;

import io.alkal.kalium.Kalium;
import io.alkal.kalium.annotations.On;
import io.alkal.kalium.interfaces.KaliumQueueAdapter;
import io.alkal.kalium.internals.utils.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Ziv Salzman
 * Created on 20-Jan-2019
 */

public class KaliumImpl implements Kalium, QueueListener {

    private List<Object> reactorInstances;
    private Map<String, Object> reactorIdToReactorInstanceMap = new HashMap<>();
    private Map<String, Map<Class, List<Method>>> reactorIdToObjectTypeToMethodMap = new HashMap<>();
    private KaliumQueueAdapter queueAdapter;


    @Override
    public void start() {
        reactorInstances.forEach(reactorInstance -> addReactorInternal(
                reactorInstance.getClass().getSimpleName(), reactorInstance));
        queueAdapter.start();
    }

    @Override
    public void stop() {
        queueAdapter.stop();
    }

    @Override
    public void addReactor(Object reactor) {
        if(reactorInstances == null){
            reactorInstances = new LinkedList<>();
        }
        reactorInstances.add(reactor);
    }


    @Override
    public void post(Object object) {
        queueAdapter.post(object);
    }


    public void setReactorInstances(List<Object> reactorInstances) {
        this.reactorInstances = reactorInstances;
    }

    void setQueueAdapter(KaliumQueueAdapter queueAdapter) {
        this.queueAdapter = queueAdapter;
    }


    @Override
    public void onObjectReceived(String reactorId, Object object) {
        if (!reactorIdToObjectTypeToMethodMap.containsKey(reactorId)) return;
        Map<Class, List<Method>> objectTypeToHandlersMap = reactorIdToObjectTypeToMethodMap.get(reactorId);
        if (!objectTypeToHandlersMap.containsKey(object.getClass())) return;
        //TODO filter based on annotations
        //TODO run in parallel
        objectTypeToHandlersMap.get(object.getClass()).stream().forEach(method -> {
            Object reactorInstance = reactorIdToReactorInstanceMap.get(reactorId);
            try {
                method.invoke(reactorInstance, object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    public Map<String, Collection<Class>> getReactorIdsToObjectTypesMap() {
        Map<String, Collection<Class>> reactorIdsToObjectTypeMap = new HashMap<>();
        reactorIdToObjectTypeToMethodMap.entrySet().forEach(entry -> {
            Collection<Class> objectTypes = entry.getValue().keySet();

            reactorIdsToObjectTypeMap.put(entry.getKey(), objectTypes);
        });
        return reactorIdsToObjectTypeMap;
    }

    @Override
    public <T> void on(String condition, Class<T> tClass, Consumer<T> consumer) {
       on(condition, tClass, consumer, UUID.randomUUID().toString());
    }

    @Override
    public <T> void on(String condition, Class<T> tClass, Consumer<T> consumer, String reactorId) {
        BaseReactor<T> reactorInstance = new BaseReactor<T>() {
            @On
            public void doSomething(T t) {
                consumer.accept(t);
            }
        };
        Map<Class, List<Method>> objectTypeToHandlersMap = new HashMap<>();
        objectTypeToHandlersMap.put(tClass, Arrays.asList(reactorInstance.getClass().getDeclaredMethods()));
        reactorIdToObjectTypeToMethodMap.put(reactorId, objectTypeToHandlersMap);
        reactorIdToReactorInstanceMap.put(reactorId, reactorInstance);

    }

    private void addReactorInternal(String reactorId, Object reactorInstance) {

        Class reactorClass = reactorInstance.getClass();
        Map<Class, List<Method>> objectTypeToHandlersMap = new HashMap<>();
        reactorIdToObjectTypeToMethodMap.put(reactorId, objectTypeToHandlersMap);
        reactorIdToReactorInstanceMap.put(reactorId, reactorInstance);
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
    }
}
