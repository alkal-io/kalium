package io.alkal.kalium.internals;

import io.alkal.kalium.Kalium;
import io.alkal.kalium.interfaces.KaliumQueueAdapter;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Ziv Salzman
 * Created on 20-Jan-2019
 */

public class KaliumBuilder {

    private KaliumQueueAdapter queueAdapter = null;
    private List<Object> reactors = new LinkedList<>();

    public KaliumBuilder setQueueAdapter(KaliumQueueAdapter queueAdapter) {
        this.queueAdapter = queueAdapter;
        return this;
    }

    public KaliumBuilder addReactor(Object reactor) {
        reactors.add(reactor);
        return this;
    }

    public Kalium build() {
        KaliumImpl kalium = new KaliumImpl();
        kalium.setReactorInstances(reactors);
        queueAdapter.setQueueListener(kalium);
        kalium.setQueueAdapter(queueAdapter);

        return kalium;
    }
}
