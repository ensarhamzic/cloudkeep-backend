package com.cloudkeep.CloudKeep.content.responses;

import com.cloudkeep.CloudKeep.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SharedUsersResponse {
    private List<UserDTO> users;
}
