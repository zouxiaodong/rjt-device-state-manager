package com.ns.device_state_manager.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DeviceEventRequest {
    private String deviceId;     // 对应device_mac
    private Integer evtType;      // 事件类型: 1-上线 2-心跳 3-离线
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime time;    // 事件时间
}