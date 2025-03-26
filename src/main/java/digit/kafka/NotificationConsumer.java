package digit.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.config.ServiceConstants;
import digit.service.NotificationService;
import digit.web.models.BirthRegistrationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Slf4j
public class NotificationConsumer {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private NotificationService notificationService;

    @KafkaListener(topics = {"${btr.kafka.create.topic}"})
    /**
     * Listens to messages from the configured Kafka topic and processes them.
     *
     * @param record The received message payload in the form of a HashMap.
     * @param topic  The Kafka topic from which the message was received.
     */
    public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        try {

            BirthRegistrationRequest request = mapper.convertValue(record, BirthRegistrationRequest.class);
            //log.info(request.toString());
            notificationService.prepareEventAndSend(request);

        } catch (final Exception e) {

            log.error(ServiceConstants.KAFKA_LISTEN_ERROR + record + " on topic: " + topic + ": ", e);
        }
    }

}
