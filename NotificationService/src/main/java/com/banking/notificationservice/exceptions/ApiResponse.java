package com.banking.notificationservice.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> { // Use generics
    private int status;
    private String message;
    private OffsetDateTime timestamp;
    private T body; // Typed body

    public static <T> ApiResponse<T> success(int status, String message, T body) {
        return new ApiResponse<>(status, message, OffsetDateTime.now(), body);
    }

    public static <T> ApiResponse<T> error(int status, String message, T body) {
        return new ApiResponse<>(status, message, OffsetDateTime.now(), body);
    }
}
