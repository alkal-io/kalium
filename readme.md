# On: queues made simpler

## What is On
On is simple client that help build reactive micro-services architechture on top of queues.
Currently, supporting Java only, but will support many other popular languages.

On provides a simple way to react to queued events. We call a call that defines methods on how to treat each event as a Reactor. 

``` java
 public class MyReactor {
    
    @On("payment.processed == false")
    public void processPayment(Payment payment) {
        // Do something with the payment
        payment.processed = true;
    }
 }
```

In addition to reacting to events, On provides a simple way to broadcast events
``` java
 public class PaymentService {
    private On on;    
    
    public void publishNewPayment(Payment payment) {
        payment.processed = false;
        on.broadcast(payment);
    }
 }
```

## How On works
Behind the scenes On will use the ```@On``` annotations to define out-of-the-box serilizer/de-serilizer for the event classes. Based on the different queue it connects to it will generate implementation of the queue listener.

The default queue is an Apache Kafka. But it can be extended to any other queue like RabbitMQ, ApacheMQ, etc...
## Adding On to your build

On's Maven group ID is `io.on` and its artifact ID is `on`.
To add a dependency on On using Maven, use the following:

```xml
<dependency>
  <groupId>io.on</groupId>
  <artifactId>on</artifactId>
  <version>1.0.0</version>
</dependency>
```

To add a dependency using Gradle:

```gradle
dependencies {
  compile 'io.on:on:1.0.0'
}
```

## Initializing on
``` java
 public static void main(String[] args) {
    On on = On.Builder()
        .setQueue(new OnKafkaQueue("localhost:9092"))
        .addReactor(MyReactor1.class)
        .addReactor(MyReactor2.class)
        .build();
        
    on.start();
    ...
  }
```


``` java
public class MyReactor {
 
 
 
 @On("payment.processed == false")
 public void processPayment(Payment payment) {
    // Do something with the payment
    payment.processed = true;
    on.post(payment);
    
    //Produce and send receipt
    Reciept receipt = ...;
    On.post(receipt);
 }
}
```
