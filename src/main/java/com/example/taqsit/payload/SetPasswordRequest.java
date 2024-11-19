package com.example.taqsit.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetPasswordRequest {
    private Integer userId;
    private String oldPassword;
    private String newPassword;
}
