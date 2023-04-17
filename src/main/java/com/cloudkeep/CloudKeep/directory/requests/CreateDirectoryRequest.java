package com.cloudkeep.CloudKeep.directory.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateDirectoryRequest {
    @NotNull(message = "Name is required")
    @Size(min = 3, max = 30, message = "Name must be between 3 and 30 characters")
    private String name;
    private Long parentDirectoryId;
}
