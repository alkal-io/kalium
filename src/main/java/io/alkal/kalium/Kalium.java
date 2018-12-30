package io.alkal.kalium;

import io.alkal.kalium.internals.KaliumBuilder;

public interface Kalium {

    static KaliumBuilder Builder(){
        return new KaliumBuilder();
    }

    void post(Object object);

    void start();
}
