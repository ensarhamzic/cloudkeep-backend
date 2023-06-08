package com.cloudkeep.CloudKeep.file;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class FileDTOMapper implements Function<File, FileDTO> {
    @Override
    public FileDTO apply(File file) {
        boolean shared = file.getSharedUsers() != null && file.getSharedUsers().size() > 0;
        return new FileDTO(
                file.getId(),
                file.getName(),
                file.getPath(),
                file.getDateCreated(),
                file.getDateModified(),
                file.getType(),
                file.getFavorite(),
                file.getOwner().getId(),
                file.getDirectory() != null ? file.getDirectory().getId() : null,
                shared
        );
    }
}
