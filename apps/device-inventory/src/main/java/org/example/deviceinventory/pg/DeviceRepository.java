package org.example.deviceinventory.pg;

import lombok.RequiredArgsConstructor;
import org.example.deviceinventory.models.Device;
import org.example.deviceinventory.models.DeviceChannel;
import org.example.deviceinventory.models.DeviceWithChannels;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DeviceRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Long> insert(List<Device> devices) {
        if (devices.isEmpty()) {
            return List.of();
        }

        Map<Boolean, List<Device>> deviceByHavingId = devices.stream().
                collect(Collectors.groupingBy(device -> device.id() != null,
                                Collectors.mapping(Function.identity(), Collectors.toList()))
                );

        List<Long> deviceIds = new ArrayList<>();
        if (deviceByHavingId.get(true) != null){
            String sqlWithIds = """
                INSERT INTO device(id, name, type, location)
                SELECT unnest(:ids), unnest(:names), unnest(:types), unnest(:locations)
                RETURNING id
                """;
            var paramsWithIds = new MapSqlParameterSource()
                    .addValue("ids", deviceByHavingId.get(true).stream()
                            .map(Device::id).toArray(Long[]::new))
                    .addValue("names", deviceByHavingId.get(true).stream()
                            .map(Device::name).toArray(String[]::new))
                    .addValue("types", deviceByHavingId.get(true).stream()
                            .map(Device::type).toArray(String[]::new))
                    .addValue("locations", deviceByHavingId.get(true).stream()
                            .map(Device::location).toArray(String[]::new));
            deviceIds.addAll(namedParameterJdbcTemplate
                    .query(sqlWithIds, paramsWithIds, (rs, rowNum) -> rs.getLong("id")));
        }
        if (deviceByHavingId.get(false) != null) {
            String sqlWithoutIds = """
                INSERT INTO device(name, type, location)
                SELECT unnest(:names), unnest(:types), unnest(:locations)
                RETURNING id
                """;

            var paramsWithoutIds = new MapSqlParameterSource()
                    .addValue("names", deviceByHavingId.get(false).stream()
                            .map(Device::name).toArray(String[]::new))
                    .addValue("types", deviceByHavingId.get(false).stream()
                            .map(Device::type).toArray(String[]::new))
                    .addValue("locations", deviceByHavingId.get(false).stream()
                            .map(Device::location).toArray(String[]::new));


            deviceIds.addAll(namedParameterJdbcTemplate
                    .query(sqlWithoutIds, paramsWithoutIds, (rs, rowNum) -> rs.getLong("id")));
        }

        return deviceIds;
    }

    public List<DeviceWithChannels> findAllDevicesWithChannels() {
        String sql = """
            SELECT
                d.id AS device_id,
                d.name,
                d.type,
                d.location,
                d.ip,
                d.created_at as device_created_at,
                c.id AS channel_id,
                c.channel_index,
                c.type,
                c.created_at as channel_created_at,
                c.value,
                c.value_updated_at
            FROM device d
            LEFT JOIN device_channel c ON d.id = c.device_id
            ORDER BY d.id, c.channel_index
        """;

        Map<Long, DeviceWithChannels> deviceMap = new LinkedHashMap<>();
        return namedParameterJdbcTemplate.query(sql, rs -> {
            while (rs.next()) {
                Long deviceId = rs.getLong("device_id");

                var device = deviceMap.get(deviceId);
                if (device == null) {
                    device = new DeviceWithChannels(
                            deviceId,
                            rs.getString("name"),
                            rs.getString("type"),
                            rs.getString("location"),
                            rs.getString("ip"),
                            rs.getTimestamp("device_created_at").toInstant(),
                            new ArrayList<>()
                    );
                    deviceMap.put(deviceId, device);
                }

                var valueTs = rs.getTimestamp("value_updated_at");
                device.channels().add(new DeviceChannel(
                        rs.getLong("channel_id"),
                        deviceId,
                        rs.getInt("channel_index"),
                        rs.getString("type"),
                        rs.getTimestamp("channel_created_at").toInstant(),
                        (Float) rs.getObject("value"),
                        valueTs == null ? null : valueTs.toInstant()
                ));
            }

            return new ArrayList<>(deviceMap.values());
        });
    }

    public List<Long> deleteByIds(List<Long> deviceIds) {
        if (deviceIds.isEmpty()) {
            return List.of();
        }
        String sql = "DELETE FROM device WHERE id = ANY(:ids) RETURNING id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ids", deviceIds.toArray(new Long[0]));
        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> rs.getLong("id"));
    }
}
