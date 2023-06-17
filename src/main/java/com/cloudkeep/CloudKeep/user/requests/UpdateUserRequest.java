package com.cloudkeep.CloudKeep.user.requests;

import com.cloudkeep.CloudKeep.utils.annotation.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@FieldMatch(first = "password", second = "confirmPassword", message = "Passwords must match")
public class UpdateUserRequest {
    @NotNull(message = "Username must not be empty")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    private String profilePicture;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @Size(min = 8, message = "Confirm password must be at least 8 characters long")
    private String confirmPassword;

    @NotNull(message = "First name must not be empty")
    @Size(min = 3, max = 30, message = "First name must be between 3 and 20 characters")
    private String firstName;
    @NotNull(message = "Last name must not be empty")
    @Size(min = 3, max = 30, message = "Last name must be between 3 and 20 characters")
    private String lastName;
}
