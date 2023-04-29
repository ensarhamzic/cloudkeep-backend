package com.cloudkeep.CloudKeep.file.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileUploadRequest {
    @NotNull(message = "Name cannot be null")
    @Size(min = 3, max = 20, message = "Name must be between 3 and 20 characters")
    private String name;
    @NotNull(message = "File cannot be null")
    private MultipartFile file;
}
