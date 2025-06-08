package org.example.temperatureapi.api;

import org.example.temperatureapi.models.Sensor;
import org.example.temperatureapi.utils.TemperatureRandomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/temperature")
public class TemperatureController {
    private static final Logger log = LoggerFactory.getLogger(TemperatureController.class);
    private static final String THERM = "thermometer";
    private static final String CELSIUS = "°C";

    private final List<Sensor> sensors = List.of(
            new Sensor(1L, "kitchen", THERM, "kitchen sensor"),
            new Sensor(2L, "living_room", THERM, "living room sensor"),
            new Sensor(3L, "bedroom", THERM, "bedroom sensor")
    );

    private final Map<Long, Sensor> sensorsById = sensors.stream()
            .collect(Collectors.toMap(Sensor::id, Function.identity()));

    private final Map<String, Sensor> sensorsByLocation = sensors.stream()
            .collect(Collectors.toMap(Sensor::location, Function.identity()));

    @GetMapping("/{sensorId}")
    public TemperatureResponse getBySensorId(@PathVariable Long sensorId) {
        log.info("Getting temperature for sensor id: {}", sensorId);
        if (!sensorsById.containsKey(sensorId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return createTemperatureResponse(sensorsById.get(sensorId));
    }

    @GetMapping
    public TemperatureResponse getByLocation(@RequestParam String location) {
        log.info("Getting temperature for location: {}", location);
        if (!sensorsByLocation.containsKey(location)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return createTemperatureResponse(sensorsByLocation.get(location));
    }

    private TemperatureResponse createTemperatureResponse(Sensor sensor) {
        return new TemperatureResponse(
                TemperatureRandomizer.getRandomTemperature(),
                CELSIUS,
                Instant.now(),
                sensor.location(),
                "OK",
                sensor.id().toString(),
                sensor.type(),
                sensor.description()
        );
    }
}
