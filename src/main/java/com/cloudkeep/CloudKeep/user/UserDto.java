package com.cloudkeep.CloudKeep.user;

import com.cloudkeep.CloudKeep.directory.Directory;
import com.cloudkeep.CloudKeep.file.File;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserDto {
    public Long id;
    public String firstName;
    public String lastName;
    public String email;
    public String username;
    public String profilePicture;
}
