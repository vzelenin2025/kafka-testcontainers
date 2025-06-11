// MyEventProcessor.java
package com.example.streams;

import com.example.avro.MyEvent;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class MyEventProcessor {

    @Bean
    public Function<KStream<String, MyEvent>, KStream<String, MyEvent>> process() {
        return input -> input
                .peek((key, value) -> System.out.println("Processing: " + value))
                .mapValues(event -> MyEvent.newBuilder(event)
                        .setValue(event.getValue().toUpperCase())
                        .build());
    }
}
