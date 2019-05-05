package io.alkal.kalium.tests;

import io.alkal.kalium.annotations.On;

/**
 * @author Ziv Salzman
 * Created on 20-Jan-2019
 */
public class MyReactionThrows {

    @On
    public void doSomething(Payment payment){
        throw new RuntimeException();
    }

}
