package io.alkal.kalium.internals;

import io.alkal.kalium.Kalium;
import io.alkal.kalium.interfaces.KaliumQueueAdapter;
import io.alkal.kalium.internals.utils.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Ziv Salzman
 * Created on 20-Jan-2019
 */

public class KaliumBuilder {

    private KaliumQueueAdapter queueAdapter = null;
    private List<Object> reactions = new LinkedList<>();

    public KaliumBuilder setQueueAdapter(KaliumQueueAdapter queueAdapter) {
        this.queueAdapter = queueAdapter;
        return this;
    }

    public KaliumBuilder addReaction(Object reaction) {
        reactions.add(reaction);
        return this;
    }

    public Kalium build() {
        KaliumImpl kalium = new KaliumImpl();
        Map<Class<?>, Object> reactionsMap = new HashMap<>();
        Map<Class<?>,Map<Class<?>, List<Method>>>  reactionToObjectTypeToMethodMap = new HashMap<>();
        reactions.forEach(reaction -> {

            Class<?> reactionClass = reaction.getClass();
            Map<Class<?>, List<Method>> objectTypeToHandlersMap = new HashMap<>();
            reactionToObjectTypeToMethodMap.put(reactionClass, objectTypeToHandlersMap);
            reactionsMap.put(reactionClass, reaction);
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

        });
        kalium.setReactions(reactionsMap);
        kalium.setReactionToObjectTypeToMethodMap(reactionToObjectTypeToMethodMap);
        queueAdapter.setQueueListener(kalium);
        kalium.setQueueAdapter(queueAdapter);

        return kalium;
    }
}
