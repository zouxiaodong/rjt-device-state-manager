package com.ns.device_state_manager.service;

import com.ns.device_state_manager.dto.request.DeviceEventRequest;
import com.ns.device_state_manager.exception.DeviceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
public class DeviceEventService {

    private final JdbcTemplate jdbcTemplate;

    public DeviceEventService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void processEvent(DeviceEventRequest request) {
        // 1. 校验事件类型
        Integer status = convertEvtTypeToStatus(request.getEvtType());
        if (status == null) {
            throw new IllegalArgumentException("无效事件类型: " + request.getEvtType());
        }


        // 通过device_mac查询设备信息，返回对应的id, parent_id
        Map<String, Object> deviceInfo = jdbcTemplate.queryForMap(
                "SELECT id, parent_id FROM device_info WHERE device_mac = ? and status >= 0 limit 1",
                request.getDeviceId());

        if (deviceInfo == null || !deviceInfo.containsKey("id") || !deviceInfo.containsKey("parent_id")) {
            log.warn("设备不存在: " + request.getDeviceId());
            return;
        }

        Integer deviceId = (Integer) deviceInfo.get("id");
        Integer parentId = (Integer) deviceInfo.get("parent_id");


        // 更新当前设备的状态
        // 2. 执行数据库更新
        int updatedRows = jdbcTemplate.update(
                "UPDATE device_info SET status = ?, RECENT_TIME = ? WHERE id = ? and status >= 0",
                status,
                request.getTime(),
                deviceId
        );
        log.info("本设备状态更新:{} {} ", deviceId, updatedRows);

        if (parentId == null) {
            return;
        }
        // 更新父设备的状态，如果父设备是虚拟设备（device_mac为空），则更新其状态
        updatedRows = jdbcTemplate.update(
                "UPDATE device_info SET status = ?, RECENT_TIME = ? WHERE id = ? and status >= 0 and device_mac is null",
                status,
                request.getTime(), parentId
        );
        log.info("父设备状态更新:{} {} ", parentId, updatedRows);
    }

    /**
     * 事件类型到状态码的映射规则：
     * evtType: 1-上线 → 状态0（正常）
     * 2-心跳 → 状态0（正常）
     * 3-离线 → 状态1（离线）
     */
    private Integer convertEvtTypeToStatus(Integer evtType) {
        if (evtType == null) return null;

        return switch (evtType) {
            case 1, 2 -> 0;   // 上线和心跳保持正常状态
            case 3 -> 1;      // 离线状态
            default -> null;  // 无效类型
        };
    }
}