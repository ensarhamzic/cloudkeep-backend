package com.cloudkeep.CloudKeep.directory;

import com.cloudkeep.CloudKeep.file.FileDTOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class DirectoryDTOMapper implements Function<Directory, DirectoryDTO> {
    @Override
    public DirectoryDTO apply(Directory directory) {
        boolean shared = directory.getSharedUsers() != null && directory.getSharedUsers().size() > 0;

        return new DirectoryDTO(
                directory.getId(),
                directory.getName(),
                directory.getDateCreated(),
                directory.getDateModified(),
                directory.getParentDirectory() != null ? directory.getParentDirectory().getId() : null,
                directory.getFavorite(),
                directory.getOwner().getId(),
                shared
        );
    }
}
