package io.alkal.kalium;

import io.alkal.kalium.interfaces.KaliumQueueAdapter;
import io.alkal.kalium.internals.QueueListener;

import java.util.Map;

public class Kalium implements QueueListener {

    private Map<Class<?>,Object> reactors;
    private KaliumQueueAdapter queue;



    public static KaliumBuilder Builder() {
        return new KaliumBuilder();
    }

    public void start() {
    }

    public void post(Object object) {
        queue.post(object);
    }

    public <T> T getReactorInstance(Class<T> clazz) {
        return (T) reactors.get(clazz.getSimpleName());
    }

    void setReactors(Map<Class<?>, Object> reactors) {
        this.reactors = reactors;
    }

    @Override
    public void onObjectReceived(Object object) {
        object.getClass()
    }
}
