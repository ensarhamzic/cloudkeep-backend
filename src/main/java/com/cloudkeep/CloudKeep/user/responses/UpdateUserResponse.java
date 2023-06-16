package com.cloudkeep.CloudKeep.user.responses;

import com.cloudkeep.CloudKeep.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserResponse {
    private UserDTO user;
    private String token;
    private Boolean shouldLogout;
}
