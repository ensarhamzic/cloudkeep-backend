package com.cloudkeep.CloudKeep.auth.requests;

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
public class RegisterGoogleRequest {
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String profilePicture;
}
