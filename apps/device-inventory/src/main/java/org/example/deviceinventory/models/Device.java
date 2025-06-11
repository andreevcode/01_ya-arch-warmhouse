package org.example.deviceinventory.models;

import java.time.Instant;

public record Device(
        Long id,
        String name,
        String type,
        String location,
        String ip,
        Instant createdAt
) {
}