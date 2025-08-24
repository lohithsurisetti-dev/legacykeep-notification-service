package com.legacykeep.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard API Response DTO for Notification Service.
 * 
 * Provides consistent response structure across all endpoints.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    /**
     * Response status (success/error)
     */
    private String status;
    
    /**
     * Response message
     */
    private String message;
    
    /**
     * Response data payload
     */
    private T data;
    
    /**
     * Error code (if applicable)
     */
    private String errorCode;
    
    /**
     * Error details (if applicable)
     */
    private String errorDetails;
    
    /**
     * Timestamp of the response
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    /**
     * Create a successful response.
     * 
     * @param data Response data
     * @param message Success message
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status("success")
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Create an error response.
     * 
     * @param message Error message
     * @param errorCode Error code
     * @param errorDetails Error details
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> error(String message, String errorCode, String errorDetails) {
        return ApiResponse.<T>builder()
                .status("error")
                .message(message)
                .errorCode(errorCode)
                .errorDetails(errorDetails)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
