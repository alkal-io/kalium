package io.alkal.kalium.tests;

import io.alkal.kalium.interfaces.KaliumQueue;

import java.util.ArrayDeque;

public class DummyKaliumQueue extends ArrayDeque implements KaliumQueue {

    @Override
    public void post(Object object) {
        offer(object);
    }
}
