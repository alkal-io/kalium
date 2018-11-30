package io.alkal.kalium.tests;

import io.alkal.kalium.annotations.On;

public class MyReactor {

    @On("payment.processed == false")
    public void doSomething(Payment payment){
        payment.setProcessed(true);
    }

}
