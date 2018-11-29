# Kalium
### Reactive framework

## What is Kalium
Kalium is simple client that help build reactive micro-services architechture on top of queues.
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

In addition to reacting to events, Kalium provides a simple way to broadcast events
``` java
 public class PaymentService {
    private On on;    
    
    public void publishNewPayment(Payment payment) {
        payment.processed = false;
        on.broadcast(payment);
    }
 }
```

## How Kalium works
Behind the scenes Kalium will use the ```@On``` annotations to define out-of-the-box serilizer/de-serilizer for the event classes. Based on the different queue it connects to it will generate implementation of the queue listener.

The default queue is an Apache Kafka. But it can be extended to any other queue like RabbitMQ, ApacheMQ, etc...
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
