package com.cloudkeep.CloudKeep.auth.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
    @NotNull(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
}
