package com.cloudkeep.CloudKeep.file.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UploadedFile {
    @NotNull(message = "Filepath cannot be null")
    private String path;

    @NotNull(message = "Filename cannot be null")
    private String name;
}
