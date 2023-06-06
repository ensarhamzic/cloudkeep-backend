package com.cloudkeep.CloudKeep.shared;

import com.cloudkeep.CloudKeep.directory.Directory;
import com.cloudkeep.CloudKeep.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SharedDirectoryKey implements Serializable {
    private Directory directory;
    private User user;
}
