package org.example.temperatureapi.models;

import java.time.Instant;

public record DeviceChannel(
        Long id,
        long deviceId,
        int channelIndex,
        String type,
        Instant createdAt,
        Float value,
        Instant valueUpdatedAt
) {
}