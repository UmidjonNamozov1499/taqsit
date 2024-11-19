package com.example.taqsit.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    private int status;

    private String message;

    private Object data;

    public ApiResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
