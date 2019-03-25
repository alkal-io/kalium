package io.alkal.kalium.internals;

import io.alkal.kalium.Kalium;
import io.alkal.kalium.annotations.On;
import io.alkal.kalium.interfaces.KaliumQueueAdapter;
import io.alkal.kalium.internals.utils.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author Ziv Salzman
 * Created on 20-Jan-2019
 */

public class KaliumImpl implements Kalium, QueueListener {

    private List<Object> reactions;
    private Map<String, Object> reactionIdToReactionMap = new HashMap<>();
    private Map<String, Map<Class, List<Method>>> reactionIdToObjectTypeToMethodMap = new HashMap<>();
    private KaliumQueueAdapter queueAdapter;


    @Override
    public void start() {
        if(reactions !=null) {
            reactions.forEach(reaction -> addReactionInternal(
                    reaction.getClass().getSimpleName(), reaction));
        }
        queueAdapter.start();
    }

    @Override
    public void stop() {
        queueAdapter.stop();
    }

    @Override
    public void addReaction(Object reaction) {
        if(reactions == null){
            reactions = new LinkedList<>();
        }
        reactions.add(reaction);
    }


    @Override
    public void post(Object object) {
        queueAdapter.post(object);
    }

    public void setReactions(List<Object> reactions) {
        this.reactions = reactions;
    }

    void setQueueAdapter(KaliumQueueAdapter queueAdapter) {
        this.queueAdapter = queueAdapter;
    }


    @Override
    public void onObjectReceived(String reactionId, Object object) {
        if (!reactionIdToObjectTypeToMethodMap.containsKey(reactionId)) return;
        Map<Class, List<Method>> objectTypeToHandlersMap = reactionIdToObjectTypeToMethodMap.get(reactionId);
        if (!objectTypeToHandlersMap.containsKey(object.getClass())) return;
        //TODO run in parallel
        objectTypeToHandlersMap.get(object.getClass()).stream().forEach(method -> {
            Object reaction = reactionIdToReactionMap.get(reactionId);
            try {
                method.invoke(reaction, object);
            } catch (IllegalAccessException e) {
                //TODO log events
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                //TODO log events
                e.printStackTrace();
            }
        });

    }

    @Override
    public Map<String, Collection<Class>> getReactionIdsToObjectTypesMap() {
        Map<String, Collection<Class>> reactionIdsToObjectTypeMap = new HashMap<>();
        reactionIdToObjectTypeToMethodMap.entrySet().forEach(entry -> {
            Collection<Class> objectTypes = entry.getValue().keySet();

            reactionIdsToObjectTypeMap.put(entry.getKey(), objectTypes);
        });
        return reactionIdsToObjectTypeMap;
    }

    @Override
    public <T> void on(Class<T> tClass, Consumer<T> consumer) {
       on(tClass, consumer, UUID.randomUUID().toString());
    }

    @Override
    public <T> void on(Class<T> tClass, Consumer<T> consumer, String reactionId) {
        BaseReaction<T> reaction = new BaseReaction<T>() {
            @On
            public void doSomething(T t) {
                consumer.accept(t);
            }
        };
        Map<Class, List<Method>> objectTypeToHandlersMap = new HashMap<>();
        objectTypeToHandlersMap.put(tClass, Arrays.asList(reaction.getClass().getDeclaredMethods()));
        reactionIdToObjectTypeToMethodMap.put(reactionId, objectTypeToHandlersMap);
        reactionIdToReactionMap.put(reactionId, reaction);

    }

    private void addReactionInternal(String reactionId, Object reaction) {

        Class reactionClass = reaction.getClass();
        Map<Class, List<Method>> objectTypeToHandlersMap = new HashMap<>();
        reactionIdToObjectTypeToMethodMap.put(reactionId, objectTypeToHandlersMap);
        reactionIdToReactionMap.put(reactionId, reaction);
        ReflectionUtils.getMethodsAnnotatedWithOn(reactionClass).forEach(method -> {
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
