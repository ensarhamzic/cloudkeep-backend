package com.cloudkeep.CloudKeep.file.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilesSizeResponse {
    private Long size;
    private Long storageLimit;
}
