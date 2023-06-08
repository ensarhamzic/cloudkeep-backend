package com.cloudkeep.CloudKeep.directory;

import com.cloudkeep.CloudKeep.config.JwtService;
import com.cloudkeep.CloudKeep.directory.requests.CreateDirectoryRequest;
import com.cloudkeep.CloudKeep.directory.responses.CreateDirectoryResponse;
import com.cloudkeep.CloudKeep.directory.responses.GetDirectoriesResponse;
import com.cloudkeep.CloudKeep.file.FileDTO;
import com.cloudkeep.CloudKeep.file.FileDTOMapper;
import com.cloudkeep.CloudKeep.file.FileRepository;
import com.cloudkeep.CloudKeep.shared.SharedDirectoryRepository;
import com.cloudkeep.CloudKeep.shared.SharedFileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DirectoryService {
    private final DirectoryRepository directoryRepository;
    private final SharedDirectoryRepository sharedDirectoryRepository;
    private final SharedFileRepository sharedFileRepository;
    private final DirectoryDTOMapper directoryDTOMapper;
    private final FileRepository fileRepository;
    private final FileDTOMapper fileDTOMapper;
    private final JwtService jwtService;
    public CreateDirectoryResponse createDirectory(String token, CreateDirectoryRequest request) {
        var user = jwtService.getUserFromToken(token);
        Directory parentDir = null;
        if(request.getParentDirectoryId() != null) {
            parentDir = directoryRepository.findById(request.getParentDirectoryId()).orElseThrow();
            if(!parentDir.getOwner().getId().equals(user.getId()))
                throw new IllegalStateException(
                        "You can't create a directory in a directory that doesn't belong to you"
                );
        }

        String dirName = request.getName().toLowerCase().trim();

        // If every validation succeed, create the directory
        Directory directory = Directory.builder()
                .name(dirName)
                .dateCreated(new Date())
                .dateModified(new Date())
                .owner(user)
                .parentDirectory(parentDir)
                .favorite(false)
                .deleted(false)
                .build();
        if(parentDir != null)
            parentDir.setDateModified(new Date());
        directoryRepository.save(directory);

        return CreateDirectoryResponse.builder()
                .message("Directory created successfully")
                .data(directoryDTOMapper.apply(directory))
                .build();
    }

    public GetDirectoriesResponse getDirectories(String token, Long directoryId, Boolean favorite, Boolean shared) {
        Long userId = jwtService.extractId(token);
        Directory currentDirectory = null;
        if(directoryId != null) {
            currentDirectory = directoryRepository.findById(directoryId).orElse(null);
//            if(currentDirectory != null && !currentDirectory.getOwner().getId().equals(userId))
//                throw new IllegalStateException("You can't access this directory");
        }

        List<DirectoryDTO> directories;
        List<FileDTO> files;

        if (directoryId == null && favorite) {
            directories = directoryRepository.findAllByOwner_IdAndFavoriteTrueAndDeletedFalse(userId).stream().map(directoryDTOMapper).toList();
            files = fileRepository.findAllByOwner_IdAndFavoriteTrueAndDeletedFalse(userId).stream().map(fileDTOMapper).toList();
        }
        else if(directoryId == null && shared) {
            var sharedDirectories = sharedDirectoryRepository.findAllByUser_IdAndDirectory_DeletedFalse(userId);
            var sharedFiles = sharedFileRepository.findAllByUser_IdAndFile_DeletedFalse(userId);
            directories = sharedDirectories.stream().map(d -> directoryDTOMapper.apply(d.getDirectory())).toList();
            files = sharedFiles.stream().map(f -> fileDTOMapper.apply(f.getFile())).toList();
        }
        else {
            var ownerId = currentDirectory != null ? currentDirectory.getOwner().getId() : userId;
            directories = directoryRepository.findAllByOwner_IdAndParentDirectory_IdAndDeletedFalse(ownerId, directoryId).stream().map(directoryDTOMapper).toList();
            files = fileRepository.findAllByOwner_IdAndDirectory_IdAndDeletedFalse(ownerId, directoryId).stream().map(fileDTOMapper).toList();
        }

        return GetDirectoriesResponse.builder()
                .currentDirectory(currentDirectory != null ? directoryDTOMapper.apply(currentDirectory) : null)
                .directories(directories)
                .files(files)
                .build();
    }
}
