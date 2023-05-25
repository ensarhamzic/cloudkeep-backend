package com.cloudkeep.CloudKeep.file.requests;

import com.cloudkeep.CloudKeep.file.requests.helpers.UploadedFile;
import jakarta.validation.Valid;
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
