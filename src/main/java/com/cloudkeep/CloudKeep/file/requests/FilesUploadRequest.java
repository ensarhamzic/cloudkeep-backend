package com.cloudkeep.CloudKeep.file.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.List;


@Data
@Validated
public class FilesUploadRequest {
    @Valid
    private List<UploadedFile> files;
    private Long directoryId;
}
