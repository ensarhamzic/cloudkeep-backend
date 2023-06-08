package com.cloudkeep.CloudKeep.file;

import java.util.Date;

public record FileDTO (
        Long id,
        String name,
        String path,
        Date dateCreated,
        Date dateModified,
        FileType fileType,
        Boolean favorite,
        Long ownerId,
        Long directoryId,
        Boolean shared
) {}
