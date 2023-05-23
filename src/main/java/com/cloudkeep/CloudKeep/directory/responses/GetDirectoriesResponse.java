package com.cloudkeep.CloudKeep.directory.responses;

import com.cloudkeep.CloudKeep.directory.DirectoryDTO;
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
public class GetDirectoriesResponse {
    private DirectoryDTO currentDirectory;
    private List<DirectoryDTO> directories;
    private List<FileDTO> files;
}
