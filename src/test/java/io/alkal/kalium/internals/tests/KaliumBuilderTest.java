package io.alkal.kalium.internals.tests;

import io.alkal.kalium.Kalium;
import io.alkal.kalium.exceptions.KaliumBuilderException;
import io.alkal.kalium.internals.KaliumBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Ziv Salzman
 * Created on 04-May-2019
 */
public class KaliumBuilderTest {

    KaliumBuilder target;

    @Before
    public void setup() {
        target = new KaliumBuilder();
    }

    @Test
    public void test_build_shouldThrowKaliumBuilderException_whenNoQueueAdapterIsSet() {
        boolean exceptionIsThrown = false;
        try {
            target.build();
        } catch (KaliumBuilderException e) {
            exceptionIsThrown = true;
        }
        Assert.assertTrue("No KaliumBuilderException was thrown", exceptionIsThrown);
    }
}
