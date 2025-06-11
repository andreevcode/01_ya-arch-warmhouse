package org.example.temperatureapi.cache;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.temperatureapi.models.DeviceWithChannels;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
@Data
public class DeviceCacheService {
    private static final String DEVICE_INVENTORY_URL = "http://device-inventory:8083/api/v1/devices";
    private final RestTemplate restTemplate;

    private Set<Long> devicesIds;
    private Map<Long, DeviceWithChannels> deviceById;
    private Map<String, DeviceWithChannels> deviceByLocation;

    @PostConstruct
    public void init() {
        refreshCache();
    }

    @Scheduled(fixedRate = 10000)
    public void refreshCache() {
        try {
            var response = restTemplate.getForEntity(
                    DEVICE_INVENTORY_URL,
                    DeviceWithChannels[].class
            );
            var devices = response.getBody();
            if (devices != null) {
                devicesIds = Arrays.stream(devices)
                        .map(DeviceWithChannels::id)
                        .collect(Collectors.toSet());
                deviceById = Arrays.stream(devices)
                        .collect(Collectors.toMap(DeviceWithChannels::id, Function.identity()));
                deviceByLocation = Arrays.stream(devices)
                        .collect(Collectors.toMap(DeviceWithChannels::location, Function.identity()));
                log.info("Devices list is updated {} ", devicesIds);
            }
        } catch (Exception e) {
            log.error("Error fetching devices: {}", e.getMessage());
        }
    }
}
