package com.cloudkeep.CloudKeep.directory.responses;

import com.cloudkeep.CloudKeep.directory.Directory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateDirectoryResponse {
    private String message;
    private Directory data;
}
