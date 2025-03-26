package digit.kafka;

import lombok.extern.slf4j.Slf4j;
import org.egov.tracer.kafka.CustomKafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// NOTE: If tracer is disabled change CustomKafkaTemplate to KafkaTemplate in autowiring

@Service
@Slf4j
public class Producer {

    @Autowired
    private CustomKafkaTemplate<String, Object> kafkaTemplate;
    /**
     * Pushes a message to the specified Kafka topic.
     *
     * @param topic The name of the Kafka topic.
     * @param value The message payload to be sent.
     */
    public void push(String topic, Object value) {
        kafkaTemplate.send(topic, value);
    }
}
