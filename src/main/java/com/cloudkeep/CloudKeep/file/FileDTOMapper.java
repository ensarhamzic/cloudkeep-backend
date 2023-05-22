package com.cloudkeep.CloudKeep.file;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class FileDTOMapper implements Function<File, FileDTO> {
    @Override
    public FileDTO apply(File file) {
        return new FileDTO(
                file.getId(),
                file.getName(),
                file.getPath(),
                file.getType(),
                file.getOwner().getId(),
                file.getDirectory() != null ? file.getDirectory().getId() : null
        );
    }
}
