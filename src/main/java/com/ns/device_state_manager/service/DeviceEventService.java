package com.ns.device_state_manager.service;

import com.ns.device_state_manager.dto.request.DeviceEventRequest;
import com.ns.device_state_manager.exception.DeviceNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        // 2. 执行数据库更新
        int updatedRows = jdbcTemplate.update(
                "UPDATE device_info SET status = ?, RECENT_TIME = ? WHERE device_mac = ? and status >= 0",
                status,
                request.getTime(),
                request.getDeviceCode()
        );

        // 3. 检查设备是否存在
        if (updatedRows == 0) {
            throw new DeviceNotFoundException("设备不存在: " + request.getDeviceCode());
        }
    }

    /**
     * 事件类型到状态码的映射规则：
     * evtType: 1-上线 → 状态0（正常）
     *          2-心跳 → 状态0（正常）
     *          3-离线 → 状态1（离线）
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