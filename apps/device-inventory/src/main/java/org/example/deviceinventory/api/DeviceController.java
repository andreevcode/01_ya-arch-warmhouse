package org.example.deviceinventory.api;

import lombok.RequiredArgsConstructor;
import org.example.deviceinventory.models.Device;
import org.example.deviceinventory.models.DeviceWithChannels;
import org.example.deviceinventory.pg.DeviceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;

    @PostMapping
    public List<Long> addDevice(@RequestBody Device device) {
        return deviceService.insertDevicesWithDefaultChannel(List.of(device));
    }

    @GetMapping()
    public List<DeviceWithChannels> getDevices() {
        return deviceService.getAllDevicesWithChannels();
    }

    @DeleteMapping("/{deviceId}")
    public List<Long> deleteDevice(@PathVariable Long deviceId) {
        return deviceService.deleteByIds(List.of(deviceId));
    }
}
