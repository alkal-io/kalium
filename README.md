# Kalium: a reactive framework for micro-services
[![Build Status](https://travis-ci.org/alkal-io/kalium.svg?branch=master)](https://travis-ci.org/alkal-io/kalium)

## What is Kalium
Kalium is a framework the make it easier for micro-services to asynchronously interact with each other.

Here's an example in Java. Let's assume we have an e-commerce site that accepts payments and send receipts to customers.
Upon checkout, the customer is sending a ```Payment``` that is processed. Once a ```Payment``` is processed, a ```Receipt``` need to be sent to the customer.

For scale purposes, we assume paymentד are processed on one micro-service, while receipts are produced and sent from diffrenet micro-services.

Here is how we would use Kalium to help us with these data flows.

Processing the payment
``` java
 public class PaymentProcessor {
    ...
    @On("payment.processed == false")
    public void processPayment(Payment payment) {
        // Do something with the payment, e.g. call Stripe to make the actual payment
        payment.processed = true;
        kalium.post(payment);
    }
 }
```

Preparing the receipt
``` java
 public class ReceiptProducer {
    ...
    @On("payment.processed == true")
    public void prepareReceipt(Payment payment) {
        //convert processed payment into receipt
        Receipt newReceipt = converPaymentToReceipt(payment);
        kalium.post(newReceipt);
    }
 }
```

email the receipt to the customer
``` java
 public class ReceiptMailer {
    ...
    @On("receipt")
    public void sendReceipt(Receipt receipt) {
        //send the receipt, e.g. convert it to HTML and send it with SendGrid
        prepareReceiptEmailAndSend(receipt);
        receipt.sent = true;
        kalium.post(receipt);
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
Behind the scenes, Kalium is using existing queueu technology as a scalable event bus. Kalium uses the ```@On``` annotations to define out-of-the-box serializer/de-serializer for the event classes. The condition specified inside the ```@On``` annotation is translated to a generated queue listener, based on the underlying queue it uses.

Currently, Kalium is supporting Java, and can only be used with Apache Kafka as the underlying event bus. But it is planned to have implementation for other lanaguages like Javascript and Python, and to be used with AWS Kinesis as well.


## Adding Kalium to your build

Kalium's Maven group ID is `io.alkal` and its artifact ID is `kalium`.
To add a dependency on On using Maven, use the following:

```xml
<dependency>
  <groupId>io.alkal</groupId>
  <artifactId>kalium-kafka</artifactId>
  <version>0.0.1</version>
</dependency>
```

To add a dependency using Gradle:

```gradle
dependencies {
  compile 'io.alkal:kalium-kafka:0.0.1'
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

contact me: ziv@alkal.io
