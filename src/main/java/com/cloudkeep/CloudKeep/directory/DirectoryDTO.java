package com.cloudkeep.CloudKeep.directory;

import com.cloudkeep.CloudKeep.file.FileDTO;

import java.util.List;

public record DirectoryDTO(
        Long id,
        String name,
        Long parentDirectoryId,
        Long ownerId
        ) { }
