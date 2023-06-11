package com.cloudkeep.CloudKeep.auth.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotNull(message = "Email must not be empty")
    @Email(message = "Email must be valid")
    private String email;
    @NotNull(message = "Token must not be empty")
    private String code;
    @NotNull(message = "Password must not be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
}
