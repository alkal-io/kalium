package io.alkal.kalium.internals;

import io.alkal.kalium.Kalium;
import io.alkal.kalium.annotations.On;
import io.alkal.kalium.exceptions.KaliumException;
import io.alkal.kalium.interfaces.KaliumQueueAdapter;
import io.alkal.kalium.internals.utils.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Ziv Salzman
 * Created on 20-Jan-2019
 */

public class KaliumImpl implements Kalium, QueueListener {

    private static final Logger logger = Logger.getLogger(KaliumImpl.class.getName());

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
        logger.info("Kalium started :)");
    }

    @Override
    public void stop() {
        queueAdapter.stop();
        logger.info("Kalium stopped :(");
    }

    @Override
    public void addReaction(Object reaction) {
        if(reaction == null) {
            logger.warning("Reaction is null! No new reaction will be added.");
            return;
        }
        if(reactions == null){
            reactions = new LinkedList<>();
        }
        reactions.add(reaction);

        logger.info("New Reaction object was added [class="+reaction.getClass().getName()+"]");
    }


    @Override
    public void post(Object object) {
        if(object != null) {
            logger.log(Level.FINEST, "Posting object: " + object.toString());
            queueAdapter.post(object);
        } else {
            logger.info("Object is null, no object will be posted!");
        }
    }

    void setQueueAdapter(KaliumQueueAdapter queueAdapter) {
        this.queueAdapter = queueAdapter;
    }


    @Override
    public void onObjectReceived(String reactionId, Object object) {
        if (!reactionIdToObjectTypeToMethodMap.containsKey(reactionId)) {
            logger.info("[reactionId="+reactionId+"] does not match any of the registered reactions");
            return;
        }
        Map<Class, List<Method>> objectTypeToHandlersMap = reactionIdToObjectTypeToMethodMap.get(reactionId);
        if (!objectTypeToHandlersMap.containsKey(object.getClass())) return;
        //TODO run in parallel
        objectTypeToHandlersMap.get(object.getClass()).stream().forEach(method -> {
            Object reaction = reactionIdToReactionMap.get(reactionId);
            try {
                method.invoke(reaction, object);
            } catch (IllegalAccessException|InvocationTargetException e) {
                logger.log(Level.WARNING, "Failed to invoke [method=" +
                        method.getName()+"],[reactionId="+reactionId+"]", e);
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
        logger.finest(reactionIdsToObjectTypeMap.toString());
        return reactionIdsToObjectTypeMap;
    }

    @Override
    public <T> void on(Class<T> tClass, Consumer<T> consumer) throws KaliumException {
       on(tClass, consumer, UUID.randomUUID().toString());
    }

    @Override
    public <T> void on(Class<T> tClass, Consumer<T> consumer, String reactionId)  throws KaliumException{
        validateOnInputs(tClass,consumer,reactionId);
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
        logger.info("Reaction in form of lambda expression was added. " +
                "[class="+tClass.getName()+"],[reactionId="+reactionId+"]");

    }

    private void validateOnInputs(Class tClass, Consumer consumer, String reactionId)  throws KaliumException {
        KaliumException exception = null;
        if(tClass == null) {
            exception = new KaliumException(".on(...) cannot use null class!");
        } else if (consumer == null) {
             exception = new KaliumException(".on(...) cannot use null reaction lambda expression!");
        } else if (reactionId == null || reactionId.isEmpty()) {
             exception = new KaliumException(".on(...) cannot use null or empty reactionId!");
        }

        if(exception!=null) {
            logger.log(Level.WARNING, exception.getMessage(), exception);
            throw exception;
        }
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
