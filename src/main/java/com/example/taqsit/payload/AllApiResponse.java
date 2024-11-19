package com.example.taqsit.payload;

import org.springframework.http.ResponseEntity;

public class AllApiResponse {
    public static ResponseEntity<?> response(int dataStatus, String message) {
        return ResponseEntity.ok(new ApiResponse(dataStatus, message));
    }

    public static ResponseEntity<?> response(int dataStatus, String message, Object data) {
        return ResponseEntity.ok(new ApiResponse(dataStatus, message, data));
    }

    public static ResponseEntity<?> response(Integer status, int dataStatus, String message) {
        return ResponseEntity.status(status).body(new ApiResponse(dataStatus, message));
    }

    public static ResponseEntity<?> response(Integer status, int dataStatus, String message, Object data) {
        return ResponseEntity.status(status).body(new ApiResponse(dataStatus, message, data));
    }
}
