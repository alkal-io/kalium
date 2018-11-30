package io.alkal.kalium.interfaces;

import io.alkal.kalium.internals.QueueListener;

public interface KaliumQueueAdapter {
    void post(Object object);
    void setQueueListener(QueueListener queueListener);
}
