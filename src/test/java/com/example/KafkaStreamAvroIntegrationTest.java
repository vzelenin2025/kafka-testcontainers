// KafkaStreamAvroIntegrationTest.java
package com.example;

import com.example.avro.MyEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class KafkaStreamAvroIntegrationTest {

    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));
    static GenericContainer<?> schemaRegistry = new GenericContainer<>(DockerImageName.parse("confluentinc/cp-schema-registry:7.5.0"))
            .withExposedPorts(8081)
            .withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry")
            .withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", kafka.getBootstrapServers());

    @BeforeAll
    static void beforeAll() {
        kafka.start();
        schemaRegistry.start();
    }

    @AfterAll
    static void afterAll() {
        kafka.stop();
        schemaRegistry.stop();
    }

    @DynamicPropertySource
    static void dynamicProps(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.kafka.properties.schema.registry.url", () ->
                "http://" + schemaRegistry.getHost() + ":" + schemaRegistry.getMappedPort(8081));
    }

    @Autowired
    private KafkaTemplate<String, MyEvent> kafkaTemplate;

    @Test
    public void shouldProcessEvent() {
        String topic = "my-events";
        MyEvent event = MyEvent.newBuilder()
                .setId("1")
                .setValue("hello")
                .build();

        kafkaTemplate.send(topic, event);

        // Await or assert output depending on stream setup (e.g., state store or output topic)
        Awaitility.await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            // TODO: add output topic consumer or state store query
        });
    }
}
