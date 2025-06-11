package org.example.temperatureapi.pub;

import lombok.RequiredArgsConstructor;
import org.example.temperatureapi.cache.DeviceCacheService;
import org.example.temperatureapi.models.AnalogData;
import org.example.temperatureapi.utils.TemperatureRandomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class TemperaturePublisher {
    private static final Logger log = LoggerFactory.getLogger(TemperaturePublisher.class);
    private static final int TEMPERATURE_CHANNEL_INDEX = 1;

    private final RabbitTemplate rabbitTemplate;
    private final DeviceCacheService devicesCache;

    @Scheduled(fixedRate = 1000)
    public void publishTemperatures() {
        var timestamp = Instant.now();
        for (Long deviceId : devicesCache.getDevicesIds()) {
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
