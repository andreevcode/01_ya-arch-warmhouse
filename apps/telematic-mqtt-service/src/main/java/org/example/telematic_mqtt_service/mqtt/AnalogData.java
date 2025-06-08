package org.example.telematic_mqtt_service.mqtt;

import java.time.Instant;

public record AnalogData(
        long deviceId,
        int channelIndex,
        double value,
        Instant timestamp
) {
}
