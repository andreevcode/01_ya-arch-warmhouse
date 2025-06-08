package org.example.telematic_mqtt_service.pg;

import org.example.telematic_mqtt_service.mqtt.AnalogData;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Repository
public class DeviceChannelRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public DeviceChannelRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public int[] updateCurrentValues(List<AnalogData> measurements) {
        var batchValues = measurements.stream()
                .map(m -> Map.of(
                        "value", m.value(),
                        "valueUpdatedAt",  Timestamp.from(m.timestamp()) ,
                        "deviceId", m.deviceId(),
                        "channelIndex", m.channelIndex()
                ))
                .toArray(Map[]::new);
        return namedParameterJdbcTemplate.batchUpdate(
                "UPDATE public.device_channel SET value = :value, value_updated_at = :valueUpdatedAt " +
                        "WHERE device_id = :deviceId AND channel_index = :channelIndex",
                batchValues
        );
    }
}
