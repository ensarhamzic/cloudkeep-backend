package com.cloudkeep.CloudKeep.content.requests.helpers;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeleteContent {
    @NotNull(message = "Id cannot be null")
    private Long id;

    @NotNull(message = "Type cannot be null")
    private ContentType type;
}
