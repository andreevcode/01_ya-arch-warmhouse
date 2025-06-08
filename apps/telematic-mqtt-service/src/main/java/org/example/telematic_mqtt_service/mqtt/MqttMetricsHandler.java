package org.example.telematic_mqtt_service.mqtt;

import org.example.telematic_mqtt_service.pg.CurrentValuesSaver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MqttMetricsHandler {
    private final Logger log = LoggerFactory.getLogger(MqttMetricsHandler.class);
    private final CurrentValuesSaver currentValuesSaver;

    public MqttMetricsHandler(CurrentValuesSaver currentValuesSaver) {
        this.currentValuesSaver = currentValuesSaver;
    }

    @RabbitListener(queues = "temperature-queue", containerFactory = "batchFactory")
    public void receiveTemperatures(List<AnalogData> temperatures) {
        log.info("Received temperatures from queue: {}", temperatures);
        var lastValues = new ArrayList<>(temperatures.stream()
                .collect(Collectors.toMap(
                        data -> Map.entry(data.deviceId(), data.channelIndex()),
                        data -> data,
                        (a, b) -> a.timestamp().isAfter(b.timestamp()) ? a : b
                ))
                .values());

        log.info("Saving only last values: {}", lastValues);
        currentValuesSaver.save(lastValues);
    }
}
