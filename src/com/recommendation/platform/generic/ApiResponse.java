package com.recommendation.platform.generic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Unit 5 Requirement: Generic Class
 * Generic response container used across services and REST/Socket endpoints.
 *
 * @param <T> Payload data type
 */
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;
    private final String timestamp;

    public ApiResponse(boolean success, String message, T data) {
        this.success   = success;
        this.message   = message;
        this.data      = data;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public static <E> ApiResponse<E> success(String message, E data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <E> ApiResponse<E> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    public boolean isSuccess()   { return success; }
    public String getMessage()   { return message; }
    public T getData()           { return data; }
    public String getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("ApiResponse[success=%b, message='%s', timestamp=%s, data=%s]",
                success, message, timestamp, data);
    }
}
