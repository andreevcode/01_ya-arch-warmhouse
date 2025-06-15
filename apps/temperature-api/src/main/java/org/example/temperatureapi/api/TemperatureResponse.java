package org.example.temperatureapi.api;

import java.time.Instant;

public record TemperatureResponse(
        double value,
        String unit,
        Instant timestamp,
        String location,
        String status,
        String sensor_id,
        String sensor_type,
        String description
) {
}
