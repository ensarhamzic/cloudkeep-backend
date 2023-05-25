package com.cloudkeep.CloudKeep.content.requests;

import com.cloudkeep.CloudKeep.content.requests.helpers.ContentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RenameContentRequest {
    @NotNull(message = "Id cannot be null")
    private Long id;

    @NotNull(message = "Type cannot be null")
    private ContentType type;

    @NotNull(message = "Name cannot be null")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters long")
    private String name;

    private Long parentDirectoryId;
}
