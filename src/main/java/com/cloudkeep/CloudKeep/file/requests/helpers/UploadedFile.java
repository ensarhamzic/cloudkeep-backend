package com.cloudkeep.CloudKeep.file.requests.helpers;

import com.cloudkeep.CloudKeep.file.FileType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UploadedFile {
    @NotNull(message = "Filepath cannot be null")
    private String path;

    @NotNull(message = "Filename cannot be null")
    private String name;

    @NotNull(message = "Type cannot be null")
    private FileType type;
}
