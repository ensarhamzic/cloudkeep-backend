package com.cloudkeep.CloudKeep.user.responses;

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
public class SearchUsersResponse {
    private List<UserDTO> users;
}
