package org.example.deviceinventory.models;

import java.time.Instant;
import java.util.List;

public record DeviceWithChannels(
        Long id,
        String name,
        String type,
        String location,
        String ip,
        Instant createdAt,
        List<DeviceChannel> channels
) {
}

