package com.cloudkeep.CloudKeep.auth;

import com.cloudkeep.CloudKeep.user.User;
import com.cloudkeep.CloudKeep.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private UserDto user;
    private String token;

}
