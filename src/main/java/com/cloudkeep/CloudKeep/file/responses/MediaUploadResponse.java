package com.cloudkeep.CloudKeep.file.responses;

import com.cloudkeep.CloudKeep.file.FileDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MediaUploadResponse {
    private String message;
    private List<FileDTO> data;
}


