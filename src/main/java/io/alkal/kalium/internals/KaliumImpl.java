package io.alkal.kalium.internals;

import io.alkal.kalium.Kalium;
import io.alkal.kalium.interfaces.KaliumQueueAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ziv Salzman
 * Created on 20-Jan-2019
 */

public class KaliumImpl implements Kalium, QueueListener {
    private Map<Class<?>, Object> reactions;

    private Map<Class<?>, Map<Class<?>, List<Method>>> reactionToObjectTypeToMethodMap;

    private KaliumQueueAdapter queueAdapter;


    @Override
    public void start() {
        queueAdapter.start();
    }

    @Override
    public void post(Object object) {
        queueAdapter.post(object);
    }

    public <T> T getReactionInstance(Class<T> clazz) {
        return (T) reactions.get(clazz);
    }


    void setReactions(Map<Class<?>, Object> reactions) {
        this.reactions = reactions;
    }

    void setQueueAdapter(KaliumQueueAdapter queueAdapter) {
        this.queueAdapter = queueAdapter;
    }

    void setReactionToObjectTypeToMethodMap(Map<Class<?>, Map<Class<?>, List<Method>>> reactionToObjectTypeToMethodMap) {
        this.reactionToObjectTypeToMethodMap = reactionToObjectTypeToMethodMap;
    }


    @Override
    public void onObjectReceived(Class<?> reactionClass, Object object) {
        if (!reactionToObjectTypeToMethodMap.containsKey(reactionClass)) return;
        Map<Class<?>, List<Method>> objectTypeToHandlersMap = reactionToObjectTypeToMethodMap.get(reactionClass);
        if (!objectTypeToHandlersMap.containsKey(object.getClass())) return;
        //TODO filter based on annotations
        //TODO run in parallel
        objectTypeToHandlersMap.get(object.getClass()).stream().forEach(method -> {
            Object reaction = getReactionInstance(method.getDeclaringClass());
            try {
                method.invoke(reaction, object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    public Map<Class<?>, Collection<Class<?>>> getReactionToObjectTypeMap() {
        Map<Class<?>, Collection<Class<?>>> reactionToObjectTypes = new HashMap<>();
        reactionToObjectTypeToMethodMap.entrySet().forEach(entry -> {
            reactionToObjectTypes.put(entry.getKey(), entry.getValue().keySet());
        });
        return  reactionToObjectTypes;
    }
}
