package io.alkal.kalium.interfaces;

import io.alkal.kalium.internals.QueueListener;

public interface KaliumQueueAdapter {

    void start();

    void post(Object object);

    void setQueueListener(QueueListener queueListener);
}
