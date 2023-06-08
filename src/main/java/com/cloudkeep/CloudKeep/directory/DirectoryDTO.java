package com.cloudkeep.CloudKeep.directory;

import com.cloudkeep.CloudKeep.file.FileDTO;

import java.util.Date;
import java.util.List;

public record DirectoryDTO(
        Long id,
        String name,
        Date dateCreated,
        Date dateModified,
        Long parentDirectoryId,
        Boolean favorite,
        Long ownerId,
        Boolean shared
        ) { }
