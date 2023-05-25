package com.cloudkeep.CloudKeep.content;

import com.cloudkeep.CloudKeep.config.JwtService;
import com.cloudkeep.CloudKeep.content.requests.ContentsRequest;
import com.cloudkeep.CloudKeep.content.requests.RenameContentRequest;
import com.cloudkeep.CloudKeep.content.requests.helpers.ContentType;
import com.cloudkeep.CloudKeep.content.requests.helpers.OneContent;
import com.cloudkeep.CloudKeep.content.responses.RenameContentResponse;
import com.cloudkeep.CloudKeep.directory.Directory;
import com.cloudkeep.CloudKeep.directory.DirectoryRepository;
import com.cloudkeep.CloudKeep.file.FileRepository;
import com.cloudkeep.CloudKeep.utils.responses.BasicResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Transactional
public class ContentService {
    private final JwtService jwtService;
    private final DirectoryRepository directoryRepository;
    private final FileRepository fileRepository;

    public BasicResponse deleteContent(String token, ContentsRequest request) {
        var user = jwtService.getUserFromToken(token);

        for (OneContent content: request.getContents()) {
            if (content.getType().equals(ContentType.DIRECTORY)) {
                var directory = directoryRepository.findById(content.getId()).orElseThrow(
                        () -> new IllegalStateException("Directory not found")
                );
                if (!directory.getOwner().getId().equals(user.getId()))
                    throw new IllegalStateException("User does not have permission to delete this directory");
                directory.setDeleted(true);
            } else {
                var file = fileRepository.findById(content.getId()).orElseThrow(
                        () -> new IllegalStateException("File not found")
                );
                if (!file.getOwner().getId().equals(user.getId()))
                    throw new IllegalStateException("User does not have permission to delete this file");
                file.setDeleted(true);
            }
        }
        return BasicResponse.builder().message("Successfully deleted contents").build();
    }

    public RenameContentResponse renameContent(String token, RenameContentRequest request) {
        var user = jwtService.getUserFromToken(token);
        String newName = request.getName().trim();

        Directory parentDir = null;
        if(request.getParentDirectoryId() != null) {
            parentDir = directoryRepository.findById(request.getParentDirectoryId()).orElseThrow();
            if(!parentDir.getOwner().getId().equals(user.getId()))
                throw new IllegalStateException(
                        "Directory does not belong to you"
                );
        }

        if (request.getType().equals(ContentType.DIRECTORY)) {
            var directory = directoryRepository.findById(request.getId()).orElseThrow(
                    () -> new IllegalStateException("Directory not found")
            );
            if (!directory.getOwner().getId().equals(user.getId()))
                throw new IllegalStateException("User does not have permission to rename this directory");

            var currentDirs = directoryRepository
                    .findAllByOwner_IdAndParentDirectory_IdAndDeletedFalse(
                            user.getId(),
                            parentDir == null ? null : parentDir.getId()
                    );
            String dirName = request.getName().toLowerCase().trim();
            if(currentDirs.stream().anyMatch(dir -> dir.getName().equals(dirName)))
                throw new IllegalStateException("You already have a directory with this name");

            directory.setName(request.getName());
        } else {
            var file = fileRepository.findById(request.getId()).orElseThrow(
                    () -> new IllegalStateException("File not found")
            );
            if (!file.getOwner().getId().equals(user.getId()))
                throw new IllegalStateException("User does not have permission to rename this file");


            var filesInDir = fileRepository
                    .findAllByOwner_IdAndDirectory_IdAndDeletedFalse(
                            user.getId(),
                            request.getParentDirectoryId() == null ? null : request.getParentDirectoryId()
                    );

            String startingFileName = newName;
            if(startingFileName.contains("."))
                startingFileName = startingFileName.substring(0, startingFileName.lastIndexOf('.'));
            AtomicReference<String> fileNameRef = new AtomicReference<>(startingFileName);
            int counter = 0;
            while (filesInDir.stream().anyMatch(f -> f.getName().equals(fileNameRef.get()))) {
                counter++;
                fileNameRef.set(startingFileName + " (" + counter + ")");
            }

            newName = fileNameRef.get();
            file.setName(fileNameRef.get());
        }
        return RenameContentResponse
                .builder()
                .message("Successfully renamed content")
                .name(newName)
                .build();
    }

    public BasicResponse addRemoveFavorite(String token, ContentsRequest request) {
        var user = jwtService.getUserFromToken(token);
        var favoriteDirs = directoryRepository.findAllByOwner_IdAndFavoriteTrue(user.getId());
        var favoriteFiles = fileRepository.findAllByOwner_IdAndFavoriteTrue(user.getId());

        for (OneContent content: request.getContents()) {
            if (content.getType().equals(ContentType.DIRECTORY)) {
                var directory = directoryRepository.findById(content.getId()).orElseThrow(
                        () -> new IllegalStateException("Directory not found")
                );
                if (!directory.getOwner().getId().equals(user.getId()))
                    throw new IllegalStateException("User does not have permission to add this directory to favorites");

                directory.setFavorite(favoriteDirs.stream().noneMatch(dir -> dir.getId().equals(directory.getId())));
            } else {
                var file = fileRepository.findById(content.getId()).orElseThrow(
                        () -> new IllegalStateException("File not found")
                );
                if (!file.getOwner().getId().equals(user.getId()))
                    throw new IllegalStateException("User does not have permission to add this file to favorites");

                file.setFavorite(favoriteFiles.stream().noneMatch(f -> f.getId().equals(file.getId())));
            }
        }
        return BasicResponse.builder().message("Successfully added or removed favorites").build();
    }
}
