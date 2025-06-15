package org.example.deviceinventory.pg;

import lombok.RequiredArgsConstructor;
import org.example.deviceinventory.models.Device;
import org.example.deviceinventory.models.DeviceWithChannels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private static final String DEFAULT_TYPE = "temperature";
    private final Logger log = LoggerFactory.getLogger(DeviceService.class);

    private final DeviceRepository deviceRepository;
    private final DeviceChannelRepository deviceChannelRepository;

    @Transactional
    public List<Long> insertDevicesWithDefaultChannel(List<Device> devices) {
        var deviceIds = deviceRepository.insert(devices);
        var channelIds = deviceChannelRepository.insertDefaultChannelWithType(deviceIds, DEFAULT_TYPE);

        // Логируем ТОЛЬКО после коммита
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                log.info("Inserted devices and channels successfully. device IDs: {}, device_channel IDs: {}",
                        deviceIds, channelIds);
            }
        });
        return deviceIds;
    }

    public List<DeviceWithChannels> getAllDevicesWithChannels() {
        return deviceRepository.findAllDevicesWithChannels();
    }

    public List<Long> deleteByIds(List<Long> deviceIds) {
        var deletedIds = deviceRepository.deleteByIds(deviceIds);
        log.info("Devices with their channels successfully deleted: {}", deletedIds);
        return deletedIds;
    }

}
