package com.cloudkeep.CloudKeep.directory;

import com.cloudkeep.CloudKeep.directory.requests.CreateDirectoryRequest;
import com.cloudkeep.CloudKeep.directory.responses.CreateDirectoryResponse;
import com.cloudkeep.CloudKeep.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DirectoryService {
    private final DirectoryRepository directoryRepository;
    private final UserRepository userRepository;
    public CreateDirectoryResponse createDirectory(CreateDirectoryRequest request, Long userId) {
        var user = userRepository.findById(userId).orElseThrow();
        Directory parentDir = null;
        if(request.getParentId() != null) {
            parentDir = directoryRepository.findById(request.getParentId()).orElseThrow();
            if(parentDir.getOwner().getId() != userId) {
                throw new IllegalStateException("You can't create a directory in a directory that doesn't belong to you");
            }
        }
        Directory directory = new Directory();
        directory.setName(request.getName());
        directory.setOwner(user);
        directory.setParentDirectory(parentDir);
        directoryRepository.save(directory);

        return CreateDirectoryResponse.builder()
                .message("Directory created successfully")
                .data(directory)
                .build();
    }

    public List<Directory> getDirectories(Long userId, Long directoryId) {
        var dirs = directoryRepository.findAllByOwner_IdAndParentDirectory_Id(userId, directoryId);
        return dirs;
    }
}
