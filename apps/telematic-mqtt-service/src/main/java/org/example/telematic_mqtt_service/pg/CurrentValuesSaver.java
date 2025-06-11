package org.example.telematic_mqtt_service.pg;

import org.example.telematic_mqtt_service.mqtt.AnalogData;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrentValuesSaver {
    private final DeviceChannelRepository channelsRepository;

    public CurrentValuesSaver(DeviceChannelRepository channelsRepository) {
        this.channelsRepository = channelsRepository;
    }

    public void save(List<AnalogData> measurements) {
        channelsRepository.updateCurrentValues(measurements);
    }
}
