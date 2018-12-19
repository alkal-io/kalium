package io.alkal.kalium.tests;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
