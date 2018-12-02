# Kalium: a reactive framework for micro-services

## What is Kalium
Kalium is a simple client that help build reactive micro-services architecture on top of queues.
Currently, supporting Java only, but will support many other popular languages.

Kalium provides a simple way to react to queued events. We call a call that defines methods on how to treat each event as a Reactor. 

``` java
 public class MyReactor {
    
    @On("payment.processed == false")
    public void processPayment(Payment payment) {
        // Do something with the payment
        payment.processed = true;
    }
 }
```

In addition to reacting to events, Kalium provides a simple way to post events
``` java
 public class PaymentService {
    private Kalium kalium;    
    
    public void publishNewPayment(Payment payment) {
        payment.processed = false;
        kalium.post(payment);
    }
 }
```

## How Kalium works
Behind the scenes, Kalium uses the ```@On``` annotations to define out-of-the-box serializer/de-serializer for the event classes. The condition specified inside the ```@On``` annotation is translated to a generated queue listener,  based on the underlying queue it uses.

The default queue is an Apache Kafka. However, it can be extended to any other queue like RabbitMQ, ApacheMQ, etc...
## Adding Kalium to your build

Kalium's Maven group ID is `io.alkal` and its artifact ID is `kalium`.
To add a dependency on On using Maven, use the following:

```xml
<dependency>
  <groupId>io.alkal</groupId>
  <artifactId>kalium</artifactId>
  <version>1.0.0</version>
</dependency>
```

To add a dependency using Gradle:

```gradle
dependencies {
  compile 'io.alkal:kalium:1.0.0'
}
```

## Initializing Kalium
``` java
 public static void main(String[] args) {
    Kalium kalium = Kalium.Builder()
        .setQueue(new KaliumKafkaQueue("localhost:9092"))
        .addReactor(MyReactor1.class)
        .addReactor(MyReactor2.class)
        .build();
        
    kalium.start();
    ...
  }
```


``` java
public class MyReactor {
 
 
 
 @On("payment.processed == false")
 public void processPayment(Payment payment) {
    // Do something with the payment
    payment.processed = true;
    kalium.post(payment);
    
    //Produce and send receipt
    Reciept receipt = ...;
    kalium.post(receipt);
 }
}
```
