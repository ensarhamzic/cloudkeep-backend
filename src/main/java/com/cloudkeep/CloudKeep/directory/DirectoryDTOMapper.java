package com.cloudkeep.CloudKeep.directory;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class DirectoryDTOMapper implements Function<Directory, DirectoryDTO> {
    @Override
    public DirectoryDTO apply(Directory directory) {
        return new DirectoryDTO(
                directory.getId(),
                directory.getName(),
                directory.getParentDirectory() != null ? directory.getParentDirectory().getId() : null,
                directory.getOwner().getId()
        );
    }
}
