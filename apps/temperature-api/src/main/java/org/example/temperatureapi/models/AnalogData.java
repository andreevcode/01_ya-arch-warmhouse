package org.example.temperatureapi.models;

import java.time.Instant;

public record AnalogData(
        long deviceId,
        int channelIndex,
        double value,
        Instant timestamp
) {
}
