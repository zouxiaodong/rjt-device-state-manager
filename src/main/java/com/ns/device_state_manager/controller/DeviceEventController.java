package com.ns.device_state_manager.controller;

import com.ns.device_state_manager.dto.request.DeviceEventRequest;
import com.ns.device_state_manager.dto.response.ApiResponse;
import com.ns.device_state_manager.service.DeviceEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeviceEventController {

    private final DeviceEventService deviceEventService;

    public DeviceEventController(DeviceEventService deviceEventService) {
        this.deviceEventService = deviceEventService;
    }

    @PostMapping("/api/device-event")
    public ResponseEntity<ApiResponse> handleDeviceEvent(@RequestBody DeviceEventRequest request) {
        deviceEventService.processEvent(request);
        return ResponseEntity.ok(ApiResponse.success("事件处理成功"));
    }
}