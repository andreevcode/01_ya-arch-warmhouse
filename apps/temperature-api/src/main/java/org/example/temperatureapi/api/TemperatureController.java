package org.example.temperatureapi.api;

import lombok.RequiredArgsConstructor;
import org.example.temperatureapi.cache.DeviceCacheService;
import org.example.temperatureapi.models.DeviceWithChannels;
import org.example.temperatureapi.utils.TemperatureRandomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@RestController
@RequestMapping("/temperature")
@RequiredArgsConstructor
public class TemperatureController {
    private static final Logger log = LoggerFactory.getLogger(TemperatureController.class);
    private static final String THERM = "thermometer";
    private static final String CELSIUS = "°C";

    private final DeviceCacheService devicesCache;


    @GetMapping("/{sensorId}")
    public TemperatureResponse getBySensorId(@PathVariable Long sensorId) {
        log.info("Getting temperature for sensor id: {}", sensorId);
        var deviceById = devicesCache.getDeviceById();
        if (!deviceById.containsKey(sensorId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return createTemperatureResponse(deviceById.get(sensorId));
    }

    @GetMapping
    public TemperatureResponse getByLocation(@RequestParam String location) {
        log.info("Getting temperature for location: {}", location);
        var deviceByLocation = devicesCache.getDeviceByLocation();
        if (!deviceByLocation.containsKey(location)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return createTemperatureResponse(deviceByLocation.get(location));
    }

    private TemperatureResponse createTemperatureResponse(DeviceWithChannels device) {
        return new TemperatureResponse(
                TemperatureRandomizer.getRandomTemperature(),
                CELSIUS,
                Instant.now(),
                device.location(),
                "OK",
                device.id().toString(),
                device.type(),
                device.name()
        );
    }
}
