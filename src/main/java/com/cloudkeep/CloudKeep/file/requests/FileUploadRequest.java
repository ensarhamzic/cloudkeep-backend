package com.cloudkeep.CloudKeep.file.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileUploadRequest {
    @NotNull(message = "File cannot be null")
    private MultipartFile file;

    private Long directoryId;
}
