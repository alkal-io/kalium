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

    public KaliumBuilder setQueueAdapter(KaliumQueueAdapter queueAdapter) {
        this.queueAdapter = queueAdapter;
        return this;
    }


    public Kalium build() {
        KaliumImpl kalium = new KaliumImpl();
        queueAdapter.setQueueListener(kalium);
        kalium.setQueueAdapter(queueAdapter);

        return kalium;
    }
}
