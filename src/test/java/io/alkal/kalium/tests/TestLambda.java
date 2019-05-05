package io.alkal.kalium.tests;

import io.alkal.kalium.Kalium;
import io.alkal.kalium.exceptions.KaliumBuilderException;
import io.alkal.kalium.exceptions.KaliumException;
import io.alkal.kalium.tests.utils.DummyKaliumQueueAdapter;
import io.alkal.kalium.tests.utils.DummyQueue;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.ref.Reference;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertTrue;


/**
 * @author Ziv Salzman
 * Created on 15-Feb-2019
 */
public class TestLambda {

    @Test
    public void test_lambdaOn_shouldInvoke_whenPublishingAnEvent() throws KaliumBuilderException, KaliumException {
        final AtomicReference<Boolean> messageArrived=new AtomicReference<>();
        messageArrived.set(false);

        DummyQueue dummyQueue = new DummyQueue();

        DummyKaliumQueueAdapter queueAdapter1 = new DummyKaliumQueueAdapter();
        queueAdapter1.setDummyQueue(dummyQueue);
        Kalium kalium1 = Kalium.Builder()
                .setQueueAdapter(queueAdapter1)
                .build();
        kalium1.on(Payment.class , payment -> {
            System.out.println(payment);
            messageArrived.set(true);
        });
        kalium1.start();

        DummyKaliumQueueAdapter queueAdapter2 = new DummyKaliumQueueAdapter();
        queueAdapter2.setDummyQueue(dummyQueue);
        Kalium kalium2 = Kalium.Builder()
                .setQueueAdapter(queueAdapter2)
                .build();
        kalium2.start();

        Payment payment = new Payment();
        kalium2.post(payment);

       assertTrue(messageArrived.get().booleanValue());
    }

    @Test
    public void test_lambdaOn_shouldInvokeInAllConsumersWithDifferentProcessingGroup_whenPublishingAnEvent() throws KaliumBuilderException, KaliumException {
        final AtomicReference<Boolean> message1Arrived=new AtomicReference<>();
        message1Arrived.set(false);
        final AtomicReference<Boolean> message2Arrived=new AtomicReference<>();
        message2Arrived.set(false);

        DummyQueue dummyQueue = new DummyQueue();

        //Consumer 1
        DummyKaliumQueueAdapter queueAdapter11 = new DummyKaliumQueueAdapter();
        queueAdapter11.setDummyQueue(dummyQueue);
        Kalium kalium11 = Kalium.Builder()
                .setQueueAdapter(queueAdapter11)
                .build();

        kalium11.on(Payment.class , payment -> {
            System.out.println(payment);
            message1Arrived.set(true);
        });

        kalium11.start();

        //Consumer 2
        DummyKaliumQueueAdapter queueAdapter12 = new DummyKaliumQueueAdapter();
        queueAdapter12.setDummyQueue(dummyQueue);
        Kalium kalium12 = Kalium.Builder()
                .setQueueAdapter(queueAdapter12)
                .build();

        kalium12.on(Payment.class , payment -> {
            System.out.println(payment);
            message2Arrived.set(true);
        });

        kalium12.start();



        DummyKaliumQueueAdapter queueAdapter2 = new DummyKaliumQueueAdapter();
        queueAdapter2.setDummyQueue(dummyQueue);
        Kalium kalium2 = Kalium.Builder()
                .setQueueAdapter(queueAdapter2)
                .build();
        kalium2.start();

        Payment payment = new Payment();
        kalium2.post(payment);

        assertTrue(message1Arrived.get().booleanValue());
        assertTrue(message2Arrived.get().booleanValue());
    }

//    @Test
//    public void test_lambdaOn_shouldInvokeOnlyOneConsumersWithSameProcessingGroup_whenPublishingAnEvent() {
//        final AtomicReference<Boolean> message1Arrived=new AtomicReference<>();
//        message1Arrived.set(false);
//        final AtomicReference<Boolean> message2Arrived=new AtomicReference<>();
//        message2Arrived.set(false);
//
//        DummyQueue dummyQueue = new DummyQueue();
//
//        final String PAYMENT_PROCESSOR="Payment Processor";
//
//        //Consumer 1
//        DummyKaliumQueueAdapter queueAdapter11 = new DummyKaliumQueueAdapter();
//        queueAdapter11.setDummyQueue(dummyQueue);
//        Kalium kalium11 = Kalium.Builder()
//                .setQueueAdapter(queueAdapter11)
//                .build();
//
//        kalium11.on("payment.processed==true", Payment.class , payment -> {
//            System.out.println(payment);
//            message1Arrived.set(true);
//        }, PAYMENT_PROCESSOR);
//
//        kalium11.start();
//
//        //Consumer 2
//        DummyKaliumQueueAdapter queueAdapter12 = new DummyKaliumQueueAdapter();
//        queueAdapter12.setDummyQueue(dummyQueue);
//        Kalium kalium12 = Kalium.Builder()
//                .setQueueAdapter(queueAdapter12)
//                .build();
//
//        kalium12.on("payment.processed==true", Payment.class , payment -> {
//            System.out.println(payment);
//            message2Arrived.set(true);
//        }, PAYMENT_PROCESSOR);
//
//        kalium12.start();
//
//
//
//        DummyKaliumQueueAdapter queueAdapter2 = new DummyKaliumQueueAdapter();
//        queueAdapter2.setDummyQueue(dummyQueue);
//        Kalium kalium2 = Kalium.Builder()
//                .setQueueAdapter(queueAdapter2)
//                .build();
//        kalium2.start();
//
//        Payment payment = new Payment();
//        kalium2.post(payment);
//
//        //using XOR operator to assert only one consumer processed the message.
//        assertTrue(message1Arrived.get().booleanValue() ^ message2Arrived.get().booleanValue());
//
//    }
}
