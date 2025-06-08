package org.example.temperatureapi.pub;

import lombok.RequiredArgsConstructor;
import org.example.temperatureapi.models.AnalogData;
import org.example.temperatureapi.models.DeviceWithChannels;
import org.example.temperatureapi.utils.TemperatureRandomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TemperatureScheduler {
    private static final Logger log = LoggerFactory.getLogger(TemperatureScheduler.class);
    private static final int TEMPERATURE_CHANNEL_INDEX = 1;
    private static final String DEVICE_INVENTORY_URL = "http://device-inventory:8083/api/v1/devices";

    private List<Long> deviceIds = List.of(1L, 2L, 3L);

    private final RabbitTemplate rabbitTemplate;
    private final RestTemplate restTemplate;

    @Scheduled(fixedRate = 1000)
    public void publishTemperatures() {
        var timestamp = Instant.now();
        for (Long deviceId : deviceIds) {
            var message = new AnalogData(
                    deviceId,
                    TEMPERATURE_CHANNEL_INDEX,
                    TemperatureRandomizer.getRandomTemperature(),
                    timestamp
            );
            log.info("Publishing temperature: {}", message);
            rabbitTemplate.convertAndSend(
                    "temperature-exchange",
                    "temperature." + deviceId + TEMPERATURE_CHANNEL_INDEX,
                    message
            );
        }
    }

    @Scheduled(fixedRate = 10000)
    public void updateSensors() {
        try {
            var response = restTemplate.getForEntity(
                    DEVICE_INVENTORY_URL,
                    DeviceWithChannels[].class
            );
            var devices = response.getBody();
            if (devices != null) {
                deviceIds = Arrays.stream(devices)
                        .map(DeviceWithChannels::id)
                        .toList();
                log.info("Devices list is updated {} ", deviceIds);
            }
        } catch (Exception e) {
            log.error("Error fetching devices: {}", e.getMessage());
        }
    }

    @Bean
    public TopicExchange temperatureExchange() {
        return new TopicExchange("temperature-exchange");
    }

    @Bean
    public Queue temperatureQueue() {
        return new Queue("temperature-queue");
    }

    @Bean
    public Binding binding(Queue temperatureQueue, TopicExchange temperatureExchange) {
        return BindingBuilder.bind(temperatureQueue).to(temperatureExchange).with("temperature.*");
    }
}
