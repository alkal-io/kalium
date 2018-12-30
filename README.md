# Kalium: a reactive framework for micro-services
https://travis-ci.org/alkal-io/kalium.svg?branch=master

## What is Kalium
Kalium is a simple client that can react to events and post new ones. Reacting and acting to events provides a way for micro-services to asynchronously interact with each other. 
Kalium act as an abstraction on top of existing frameworks that support actors model, such as different type of queues and frameworks like akka.io.


Kalium currently only supports Java services connected to Apache Kafka, but support for many other popular languages and frameworks is planned.


``` java
 public class MyReactor extends Reactor{
    
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

## Quick start
``` java
 public static void main(String[] args) {
    Kalium kalium = Kalium.Builder()
        .setQueue(new KafkaQueueKaliumAdapter("localhost:9092"))
        .addReactor(new PaymentProcessor())
        .addReactor(new ReceiptArchiver())
        .build();
        
    kalium.start();
    ...
  }
```


``` java

public class PaymentProcessor extends Reactor{

 @On("payment.processed == false")
 public void processPayment(Payment payment) {
    // Do something with the payment
    payment.processed = true;
    kalium.post(payment);
    
    //Produce and post a new receipt
    Reciept receipt = ...;
    kalium.post(receipt);
 }
}
```
``` java

public class ReceiptArchiver extends Reactor{

 @On("receipt")
 public void archiveReceipt(Receipt receipt) {
    //store the receipt in the DB
    db.persist(receipt)
 }
}
```
