package com.cloudkeep.CloudKeep.directory.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateDirectoryRequest {
    @NotNull(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters long")
    private String name;
    private Long parentDirectoryId;
}
