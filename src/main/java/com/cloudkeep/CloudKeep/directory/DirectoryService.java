package com.cloudkeep.CloudKeep.directory;

import com.cloudkeep.CloudKeep.config.JwtService;
import com.cloudkeep.CloudKeep.directory.requests.CreateDirectoryRequest;
import com.cloudkeep.CloudKeep.directory.responses.CreateDirectoryResponse;
import com.cloudkeep.CloudKeep.directory.responses.GetDirectoriesResponse;
import com.cloudkeep.CloudKeep.file.FileDTOMapper;
import com.cloudkeep.CloudKeep.file.FileRepository;
import com.cloudkeep.CloudKeep.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DirectoryService {
    private final DirectoryRepository directoryRepository;
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

        // Check if the user already has a directory in the same directory with the same name
        var currentDirs = directoryRepository
                .findAllByOwner_IdAndParentDirectory_Id(
                        user.getId(),
                        parentDir == null ? null : parentDir.getId()
                );
        String dirName = request.getName().toLowerCase().trim();
        if(currentDirs.stream().anyMatch(directory -> directory.getName().equals(dirName)))
            throw new IllegalStateException("You already have a directory with this name");


        // If every validation succeed, create the directory
        Directory directory = Directory.builder()
                .name(dirName)
                .owner(user)
                .parentDirectory(parentDir)
                .build();
        directoryRepository.save(directory);

        return CreateDirectoryResponse.builder()
                .message("Directory created successfully")
                .data(directoryDTOMapper.apply(directory))
                .build();
    }

    public GetDirectoriesResponse getDirectories(String token, Long directoryId) {
        Long userId = jwtService.extractId(token);
        Directory currentDirectory = null;
        if(directoryId != null) {
            currentDirectory = directoryRepository.findById(directoryId).orElse(null);
            if(currentDirectory != null && !currentDirectory.getOwner().getId().equals(userId))
                throw new IllegalStateException("You can't access a directory that doesn't belong to you");
        }
        var directories = directoryRepository.findAllByOwner_IdAndParentDirectory_Id(userId, directoryId).stream().map(directoryDTOMapper).toList();
        var files = fileRepository.findAllByOwner_IdAndDirectory_Id(userId, directoryId).stream().map(fileDTOMapper).toList();

        GetDirectoriesResponse.GetDirectoriesResponseBuilder response = GetDirectoriesResponse.builder()
                .currentDirectory(currentDirectory != null ? directoryDTOMapper.apply(currentDirectory) : null)
                .directories(directories)
                .files(files);
        return response.build();
    }
}
