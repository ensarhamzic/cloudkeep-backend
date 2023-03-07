package com.cloudkeep.CloudKeep.auth;

import com.cloudkeep.CloudKeep.annotation.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldMatch(first = "password", second = "confirmPassword", message = "Passwords must match")
public class RegisterRequest {
    @NotNull(message = "Email must not be empty")
    @Email(message = "Email must be valid")
    private String email;
    @NotNull(message = "Username must not be empty")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;
    @NotNull(message = "Password must not be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotNull(message = "Confirm password must not be empty")
    @Size(min = 8, message = "Confirm password must be at least 8 characters long")
    private String confirmPassword;
    @NotNull(message = "First name must not be empty")
    @Size(min = 3, max = 30, message = "First name must be between 3 and 20 characters")
    private String firstName;
    @NotNull(message = "Last name must not be empty")
    @Size(min = 3, max = 30, message = "Last name must be between 3 and 20 characters")
    private String lastName;
}