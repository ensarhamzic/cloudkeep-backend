package com.cloudkeep.CloudKeep.shared;

import com.cloudkeep.CloudKeep.file.File;
import com.cloudkeep.CloudKeep.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SharedFileKey implements Serializable {
    private File file;
    private User user;
}
