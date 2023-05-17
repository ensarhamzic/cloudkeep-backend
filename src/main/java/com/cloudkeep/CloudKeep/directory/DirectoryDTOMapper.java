package com.cloudkeep.CloudKeep.directory;

import com.cloudkeep.CloudKeep.file.FileDTOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class DirectoryDTOMapper implements Function<Directory, DirectoryDTO> {

    private final FileDTOMapper fileDTOMapper;

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
