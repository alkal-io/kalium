package io.alkal.kalium.tests.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Ziv Salzman
 * Created on 20-Jan-2019
 */
public class DummyQueue {



    private Set<DummyKaliumQueueAdapter> queueAdapters = new HashSet<>();

    public Set<DummyKaliumQueueAdapter> getQueueAdapters() {
        return queueAdapters;
    }

    public void post(DummyKaliumQueueAdapter from, Object object) {
        queueAdapters.stream().filter( adapter -> {
            return adapter != from;
        }).forEach(adapter -> {
            adapter.objectArrived(object);
        });
    }
}
