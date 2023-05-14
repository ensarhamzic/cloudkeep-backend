package com.cloudkeep.CloudKeep.file.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class MediaUploadRequest {
    @NotNull(message = "Files cannot be null")
    private List<MultipartFile> files;

    private Long directoryId;
}
