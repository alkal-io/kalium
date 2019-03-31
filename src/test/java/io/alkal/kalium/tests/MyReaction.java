package io.alkal.kalium.tests;

import io.alkal.kalium.annotations.On;

/**
 * @author Ziv Salzman
 * Created on 20-Jan-2019
 */
public class MyReaction {

    @On
    public void doSomething(Payment payment){
        payment.setProcessed(true);
    }

}
