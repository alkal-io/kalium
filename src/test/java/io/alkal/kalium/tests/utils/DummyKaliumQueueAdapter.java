package io.alkal.kalium.tests.utils;

import io.alkal.kalium.interfaces.KaliumQueueAdapter;
import io.alkal.kalium.internals.QueueListener;

public class DummyKaliumQueueAdapter implements KaliumQueueAdapter {

    private QueueListener queueListener;
    private DummyQueue dummyQueue;

    public void setDummyQueue(DummyQueue dummyQueue) {
        this.dummyQueue = dummyQueue;
        dummyQueue.getQueueAdapters().add(this);
    }

    @Override
    public void post(Object object) {
        dummyQueue.post(this, object);
    }

    @Override
    public void setQueueListener(QueueListener queueListener){
        this.queueListener = queueListener;
    }

    public void objectArrived(Object object) {
        queueListener.getReactorToObjectTypeMap().keySet().forEach(reactorClass -> {
            queueListener.onObjectReceived(reactorClass, object);
        });
    }


}
