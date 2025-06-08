package org.example.temperatureapi.models;

public record Sensor(
        Long id,
        String location,
        String type,
        String description
) {
}
