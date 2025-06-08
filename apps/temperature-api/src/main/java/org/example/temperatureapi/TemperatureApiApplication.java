package org.example.temperatureapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TemperatureApiApplication {
    private static final Logger log = LoggerFactory.getLogger(TemperatureApiApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TemperatureApiApplication.class, args);
        log.info("Application started");
    }

}
