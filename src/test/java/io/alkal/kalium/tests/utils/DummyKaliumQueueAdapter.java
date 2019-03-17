package io.alkal.kalium.tests.utils;

import io.alkal.kalium.interfaces.KaliumQueueAdapter;
import io.alkal.kalium.internals.QueueListener;

/**
 * @author Ziv Salzman
 * Created on 20-Jan-2019
 */
public class DummyKaliumQueueAdapter implements KaliumQueueAdapter {

    private QueueListener queueListener;
    private DummyQueue dummyQueue;

    public void setDummyQueue(DummyQueue dummyQueue) {
        this.dummyQueue = dummyQueue;
        dummyQueue.getQueueAdapters().add(this);
    }

    @Override
    public void start() {
        //do nothing
    }

    @Override
    public void post(Object object) {
        dummyQueue.post(this, object);
    }

    @Override
    public void setQueueListener(QueueListener queueListener){
        this.queueListener = queueListener;
    }

    @Override
    public void stop() {
        //do nothing
    }

    public void objectArrived(Object object) {
        queueListener.getReactorIdsToObjectTypesMap().keySet().forEach(reactorId -> {
            queueListener.onObjectReceived(reactorId, object);
        });
    }


}
