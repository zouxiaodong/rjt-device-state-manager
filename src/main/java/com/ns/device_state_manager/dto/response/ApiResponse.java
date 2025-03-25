package com.ns.device_state_manager.dto.response;

import lombok.Data;

@Data
public class ApiResponse {
    private String code;
    private String message;

    public static ApiResponse success(String message) {
        ApiResponse response = new ApiResponse();
        response.setCode("000000");
        response.setMessage(message);
        return response;
    }

    public static ApiResponse error(String code, String message) {
        ApiResponse response = new ApiResponse();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}