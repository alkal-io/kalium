package io.alkal.kalium.tests;

import io.alkal.kalium.interfaces.KaliumQueueAdapter;
import io.alkal.kalium.internals.QueueListener;

import java.util.ArrayDeque;

public class DummyKaliumQueueAdapter implements KaliumQueueAdapter {

    private QueueListener queueListener;


    @Override
    public void post(Object object) {
        queueListener.onObjectReceived(object);
    }

    @Override
    public void setQueueListener(QueueListener queueListener){
        this.queueListener = queueListener;
    }


}
