# Kalium: a reactive framework for micro-services
[![Build Status](https://travis-ci.org/alkal-io/kalium.svg?branch=master)](https://travis-ci.org/alkal-io/kalium)

#### _Kalium in neo-latin is the word for Pottasium- the chemical element of atomic number 19, a soft silvery-white reactive metal of the alkali metal group._

## What is Kalium?

Kalium is a framework that makes it easier for micro-services asynchronously to interact with each other.

Here's an example in Java. Let's assume we have an e-commerce site that accepts payments and sends receipts to customers. Upon checkout, a customer is sending a ```Payment```. Once the ```Payment``` is processed, a ```Receipt``` needs to be sent to the customer.

For scale purposes, we assume payments are processed on one micro-service, while receipts are produced and sent from different micro-services.

Here is how we would use Kalium to help us with these data flows.

#### Processing a payment
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

#### Preparing a receipt
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

#### Emailing a receipt to a customer
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

## How Kalium works?
Behind the scenes, Kalium is using existing queue technology as a scalable event bus. Kalium uses the ```@On``` annotations to define out-of-the-box serializer/de-serializer for the event classes. The condition specified inside the ```@On``` annotation is translated to a generated queue listener, based on the underlying queue it uses.

Currently, Kalium is supporting Java, and can only be used with Apache Kafka as the underlying event bus. However, it is planned to have an implementation for other languages like Javascript and Python and to be used with AWS Kinesis as well.


## Adding Kalium to your build

Kalium's Maven group ID is `io.alkal` and its artifact ID is `kalium-kafka`.
To add a dependency on On using Maven, use the following:

```xml
<dependency>
  <groupId>io.alkal</groupId>
  <artifactId>kalium-kafka</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

To add a dependency using Gradle:

```gradle
dependencies {
  compile 'io.alkal:kalium-kafka:0.0.1-SNAPSHOT'
}
```

First you have to build it though:
```
git clone git@github.com:alkal-io/kalium-kafka.git
cd kalium-kafka
./gradlew publishToMavenLocal
```
Note: on gradle you might have to add your ```mavenLocal()``` repository

## Quick start
#### Sending ```Hello``` object between services
``` java
public class Hello {
    
    private String value;
    
    public Hello(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
```

#### Service 1 - greeter
``` java
public class Greeter {

    public static void main(String[] args) {
        Kalium kalium = Kalium.Builder()
            .setQueue(new KaliumKafkaQueueAdapter("localhost:9092"))
            .build();
        kalium.start();
        Hello hello = new Hello("world");
        kalium.post(hello);
   }
}
```

#### Service 2- printing greeting on another service
``` java

public class HelloPrintingService {

    public static void main(String[] args) {
        Kalium kalium = Kalium.Builder()
            .setQueue(new KaliumKafkaQueueAdapter("localhost:9092"))
            .addReactor(new HelloReactor)
            .build();
        kalium.start();
   }
}

public class HelloReactor{
    @On("hello")
    public void printHello(Hello hello) {
        System.out.println("Hello " + hello.getValue());
    }
}
```





contact me: _*ziv@alkal.io*_
