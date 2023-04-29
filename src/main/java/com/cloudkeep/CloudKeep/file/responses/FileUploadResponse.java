package com.cloudkeep.CloudKeep.file.responses;

import com.cloudkeep.CloudKeep.file.FileDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadResponse {
    private String message;
    private FileDTO data;
}
