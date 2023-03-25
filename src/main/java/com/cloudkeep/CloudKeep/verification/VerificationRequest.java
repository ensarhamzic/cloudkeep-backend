package com.cloudkeep.CloudKeep.verification;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VerificationRequest {

    @NotNull(message = "Email must not be empty")
    @Email(message = "Email must be valid")
    private String email;
    @NotNull(message = "Code must not be empty")
    @Size(min = 6, max = 6, message = "Code must be 6 characters long")
    private String code;
}
