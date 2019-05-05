package io.alkal.kalium.internals.tests;

import io.alkal.kalium.Kalium;
import io.alkal.kalium.exceptions.KaliumBuilderException;
import io.alkal.kalium.exceptions.KaliumException;
import io.alkal.kalium.interfaces.KaliumQueueAdapter;
import io.alkal.kalium.internals.KaliumBuilder;
import io.alkal.kalium.internals.KaliumImpl;
import io.alkal.kalium.tests.MyReaction;
import io.alkal.kalium.tests.MyReactionThrows;
import io.alkal.kalium.tests.Payment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.Mockito.*;

/**
 * @author Ziv Salzman
 * Created on 04-May-2019
 */
public class KaliumImplTest {

    KaliumImpl target;

    @Before
    public void setup() {
        target = new KaliumImpl();
    }


    @Test
    public void test_addReaction_shouldNotThrowAnException_whenReactionIsNull() {
       boolean exceptionThrown = false;
       try {
           target.addReaction(null);
       } catch (Throwable t) {
           exceptionThrown = true;
       }

       Assert.assertFalse(exceptionThrown);
    }


    @Test
    public void test_on_shouldThrowAnException_whenClassIsNull() {

        boolean exceptionIsThrown = false;
        try {
            target.on(null, o -> {
            });
        } catch (KaliumException e) {
            exceptionIsThrown = true;
        }
        Assert.assertTrue("No KaliumException was thrown", exceptionIsThrown);
    }

    @Test
    public void test_on_shouldThrowAnException_whenConsumerIsNull() {

        boolean exceptionIsThrown = false;
        try {
            target.on(Object.class, null);
        } catch (KaliumException e) {
            exceptionIsThrown = true;
        }
        Assert.assertTrue("No KaliumException was thrown", exceptionIsThrown);
    }

    @Test
    public void test_on_shouldThrowAnException_whenReactionIdIsNull() {

        boolean exceptionIsThrown = false;
        try {
            target.on(Object.class, o -> {
            }, null);
        } catch (KaliumException e) {
            exceptionIsThrown = true;
        }
        Assert.assertTrue("No KaliumException was thrown", exceptionIsThrown);
    }

    @Test
    public void test_start_shouldCallStartOnKaliumAdapter() throws KaliumBuilderException {
        KaliumQueueAdapter adapterMock = mock(KaliumQueueAdapter.class);
        target = (KaliumImpl) Kalium.Builder().setQueueAdapter(adapterMock).build();
        target.start();
        verify(adapterMock).start();
    }

    @Test
    public void test_stop_shouldCallStopOnKaliumAdapter() throws KaliumBuilderException {
        KaliumQueueAdapter adapterMock = mock(KaliumQueueAdapter.class);
        target = (KaliumImpl) Kalium.Builder().setQueueAdapter(adapterMock).build();
        target.stop();
        verify(adapterMock).stop();
    }

    @Test
    public void test_post_shouldCallPostOnKaliumAdapter() throws KaliumBuilderException {
        KaliumQueueAdapter adapterMock = mock(KaliumQueueAdapter.class);
        target = (KaliumImpl) Kalium.Builder().setQueueAdapter(adapterMock).build();
        Object o = new Object();
        target.post(o);
        verify(adapterMock).post(o);
    }

    @Test
    public void test_post_shouldNotCallPostOnKaliumAdapter_whenObjectIsNull() throws KaliumBuilderException {
        KaliumQueueAdapter adapterMock = mock(KaliumQueueAdapter.class);
        target = (KaliumImpl) Kalium.Builder().setQueueAdapter(adapterMock).build();
        Object o = null;
        target.post(o);
        verify(adapterMock, never()).post(o);
    }

    @Test
    public void test_onObjectRecieved_shouldDoNothingWhenReactionIdIsNull() throws KaliumBuilderException {
        KaliumQueueAdapter adapterMock = mock(KaliumQueueAdapter.class);
        target = (KaliumImpl) Kalium.Builder().setQueueAdapter(adapterMock).build();
        MyReaction myReaction = spy(MyReaction.class);
        target.addReaction(myReaction);
        target.start();
        target.onObjectReceived(null, new Object());
        verify(myReaction,never()).doSomething(any());
    }

    @Test
    public void test_onObjectRecieved_shouldDoNothingWhenObjectTypeDoesNotMatch() throws KaliumBuilderException {
        KaliumQueueAdapter adapterMock = mock(KaliumQueueAdapter.class);
        target = (KaliumImpl) Kalium.Builder().setQueueAdapter(adapterMock).build();
        MyReaction myReaction = spy(MyReaction.class);
        target.addReaction(myReaction);
        target.start();
        target.onObjectReceived(myReaction.getClass().getSimpleName(), new Object());
        verify(myReaction,never()).doSomething(any());
    }

    @Test
    public void test_onObjectRecieved_shouldCallInvoke_whenReciveingAnObjectForRegisteredReaction() throws KaliumBuilderException {
        KaliumQueueAdapter adapterMock = mock(KaliumQueueAdapter.class);
        target = (KaliumImpl) Kalium.Builder().setQueueAdapter(adapterMock).build();
        MyReaction myReaction = spy(MyReaction.class);
        target.addReaction(myReaction);
        target.start();
        Payment payment = new Payment();
        target.onObjectReceived(myReaction.getClass().getSimpleName(), payment);
        verify(myReaction).doSomething(eq(payment));

    }

    @Test
    public void test_onObjectRecieved_shouldNotThrowException_whenReactionMethodThrowsException() throws KaliumBuilderException {
        KaliumQueueAdapter adapterMock = mock(KaliumQueueAdapter.class);
        target = (KaliumImpl) Kalium.Builder().setQueueAdapter(adapterMock).build();
        MyReactionThrows myReaction = spy(MyReactionThrows.class);
        target.addReaction(myReaction);
        target.start();
        Payment payment = new Payment();
        boolean exceptionThrown = false;
        try {
            target.onObjectReceived(myReaction.getClass().getSimpleName(), payment);
        } catch (Throwable t) {
            exceptionThrown = true;
        }
        Assert.assertFalse(exceptionThrown);


    }

    @Test
    public void test_onObjectRecieved_shouldCallInvoke_whenReciveingAnObjectForRegisteredReactionId() throws KaliumBuilderException, KaliumException {
        KaliumQueueAdapter adapterMock = mock(KaliumQueueAdapter.class);
        target = (KaliumImpl) Kalium.Builder().setQueueAdapter(adapterMock).build();
        AtomicReference<Boolean> lambdaExpressionInvoked = new AtomicReference<>();
        lambdaExpressionInvoked.set(false);
        target.on(Payment.class, payment -> {
            lambdaExpressionInvoked.set(true);
        },"reaction123");
        target.start();
        Payment payment = new Payment();
        target.onObjectReceived("reaction123", payment);
        Assert.assertTrue(lambdaExpressionInvoked.get());

    }




}
