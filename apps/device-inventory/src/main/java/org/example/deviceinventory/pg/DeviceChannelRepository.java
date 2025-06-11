package org.example.deviceinventory.pg;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DeviceChannelRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Long> insertDefaultChannelWithType(List<Long> deviceIds, String type) {
        String sql = """
                    INSERT INTO device_channel(device_id, channel_index, type)
                    VALUES (:deviceId, 1, :type)
                    RETURNING id
                """;

        List<Long> channelIds = new ArrayList<>();
        for (Long deviceId : deviceIds) {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("deviceId", deviceId)
                    .addValue("type", type);
            Long channelId = namedParameterJdbcTemplate.queryForObject(sql, params, Long.class);
            channelIds.add(channelId);
        }
        return channelIds;
    }
}
