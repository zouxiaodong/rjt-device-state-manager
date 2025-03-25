package com.ns.device_state_manager.exception;

import com.ns.device_state_manager.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理请求参数校验失败（@Valid触发）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        String message = "参数校验失败: " + errors.toString();
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("400002", message));
    }

    // 处理类型不匹配异常（如evtType传字符串）
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("参数'%s'类型错误，需要%s类型",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "未知");
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("400003", message));
    }

    // 处理非法事件类型
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleInvalidEventType(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("400001", ex.getMessage()));
    }

    // 处理设备不存在异常
    @ExceptionHandler(DeviceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleDeviceNotFound(DeviceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("404001", ex.getMessage()));
    }

    // 处理其他所有未捕获异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleAllExceptions(Exception ex) {
        // 生产环境建议记录完整堆栈日志
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("500000", "系统繁忙，请稍后重试"));
    }
}