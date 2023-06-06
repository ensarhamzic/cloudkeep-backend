package com.cloudkeep.CloudKeep.content;

import com.cloudkeep.CloudKeep.config.JwtService;
import com.cloudkeep.CloudKeep.content.requests.ContentsRequest;
import com.cloudkeep.CloudKeep.content.requests.MoveContentsRequest;
import com.cloudkeep.CloudKeep.content.requests.RenameContentRequest;
import com.cloudkeep.CloudKeep.content.requests.ShareContentRequest;
import com.cloudkeep.CloudKeep.content.requests.helpers.ContentType;
import com.cloudkeep.CloudKeep.content.requests.helpers.OneContent;
import com.cloudkeep.CloudKeep.content.responses.RenameContentResponse;
import com.cloudkeep.CloudKeep.content.responses.SharedUsersResponse;
import com.cloudkeep.CloudKeep.directory.Directory;
import com.cloudkeep.CloudKeep.directory.DirectoryRepository;
import com.cloudkeep.CloudKeep.file.FileRepository;
import com.cloudkeep.CloudKeep.shared.SharedDirectory;
import com.cloudkeep.CloudKeep.shared.SharedDirectoryRepository;
import com.cloudkeep.CloudKeep.shared.SharedFile;
import com.cloudkeep.CloudKeep.shared.SharedFileRepository;
import com.cloudkeep.CloudKeep.user.User;
import com.cloudkeep.CloudKeep.user.UserDTOMapper;
import com.cloudkeep.CloudKeep.user.UserRepository;
import com.cloudkeep.CloudKeep.utils.responses.BasicResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Transactional
public class ContentService {
    private final JwtService jwtService;
    private final DirectoryRepository directoryRepository;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final SharedDirectoryRepository sharedDirectoryRepository;
    private final SharedFileRepository sharedFileRepository;
    private final UserDTOMapper userDTOMapper;

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
        var favoriteDirs = directoryRepository.findAllByOwner_IdAndFavoriteTrueAndDeletedFalse(user.getId());
        var favoriteFiles = fileRepository.findAllByOwner_IdAndFavoriteTrueAndDeletedFalse(user.getId());

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

    public BasicResponse moveContent(String token, MoveContentsRequest request) {
        var user = jwtService.getUserFromToken(token);
        Directory parentDir = null;
        if(request.getDestinationDirectoryId() != null) {
            parentDir = directoryRepository.findById(request.getDestinationDirectoryId()).orElseThrow();
            if(!parentDir.getOwner().getId().equals(user.getId()))
                throw new IllegalStateException(
                        "Directory does not belong to you"
                );
        }

        for (OneContent content: request.getContents()) {
            if (content.getType().equals(ContentType.DIRECTORY)) {
                var directory = directoryRepository.findById(content.getId()).orElseThrow(
                        () -> new IllegalStateException("Directory not found")
                );
                if (!directory.getOwner().getId().equals(user.getId()))
                    throw new IllegalStateException("User does not have permission to move this directory");

                directory.setParentDirectory(parentDir);
            } else {
                var file = fileRepository.findById(content.getId()).orElseThrow(
                        () -> new IllegalStateException("File not found")
                );
                if (!file.getOwner().getId().equals(user.getId()))
                    throw new IllegalStateException("User does not have permission to move this file");

                file.setDirectory(parentDir);
            }
        }
        return BasicResponse.builder().message("Successfully moved contents").build();
    }

    public BasicResponse shareContent(String token, ShareContentRequest request) {
        var user = jwtService.getUserFromToken(token);
        var content = request.getContent();
        List<User> users = new ArrayList<>();
        for (Long userId: request.getUserIds()) {
            var userToShareWith = userRepository.findById(userId).orElseThrow(
                    () -> new IllegalStateException("User not found")
            );
            if (userToShareWith.getId().equals(user.getId()))
                throw new IllegalStateException("You cannot share with yourself");
            users.add(userToShareWith);
        }

        if (content.getType().equals(ContentType.DIRECTORY)) {
            var directory = directoryRepository.findById(content.getId()).orElseThrow(
                    () -> new IllegalStateException("Directory not found")
            );
            if (!directory.getOwner().getId().equals(user.getId()))
                throw new IllegalStateException("User does not have permission to share this directory");

            List<SharedDirectory> sharedDirectoriesToAdd = new ArrayList<>();
            List<SharedDirectory> sharedDirectoriesToRemove = new ArrayList<>();
            for (User userToShareWith: users) {
                if (sharedDirectoryRepository.findByDirectory_IdAndUser_Id(directory.getId(), userToShareWith.getId()) == null) {
                    var sharedDirectory = SharedDirectory.builder()
                            .directory(directory)
                            .user(userToShareWith)
                            .build();
                    sharedDirectoriesToAdd.add(sharedDirectory);
                }
            }
            var currentSharedUsers = sharedDirectoryRepository.findAllByDirectory_Id(directory.getId());
            for (SharedDirectory sharedDirectory: currentSharedUsers) {
                if (!users.contains(sharedDirectory.getUser()))
                    sharedDirectoriesToRemove.add(sharedDirectory);
            }
            sharedDirectoryRepository.deleteAllInBatch(sharedDirectoriesToRemove);
            sharedDirectoryRepository.saveAll(sharedDirectoriesToAdd);
        } else {
            var file = fileRepository.findById(content.getId()).orElseThrow(
                    () -> new IllegalStateException("File not found")
            );
            if (!file.getOwner().getId().equals(user.getId()))
                throw new IllegalStateException("User does not have permission to share this file");

            List<SharedFile> sharedFilesToAdd = new ArrayList<>();
            List<SharedFile> sharedFilesToRemove = new ArrayList<>();
            for (User userToShareWith: users) {
                if (sharedFileRepository.findByFile_IdAndUser_Id(file.getId(), userToShareWith.getId()) == null) {
                    var sharedFile = SharedFile.builder()
                            .file(file)
                            .user(userToShareWith)
                            .build();
                    sharedFilesToAdd.add(sharedFile);
                }
            }
            var currentSharedUsers = sharedFileRepository.findAllByFile_Id(file.getId());
            for (SharedFile sharedFile: currentSharedUsers) {
                if (!users.contains(sharedFile.getUser()))
                    sharedFilesToRemove.add(sharedFile);
            }

            sharedFileRepository.deleteAllInBatch(sharedFilesToRemove);
            sharedFileRepository.saveAll(sharedFilesToAdd);
        }
        return BasicResponse.builder().message("Successfully shared content").build();
    }

    public SharedUsersResponse getSharedUsers(String token, Long contentId, ContentType contentType) {
        var user = jwtService.getUserFromToken(token);
        List<User> sharedUsers = new ArrayList<>();
        if(contentType == ContentType.DIRECTORY) {
            var directory = directoryRepository.findById(contentId).orElseThrow(
                    () -> new IllegalStateException("Directory not found")
            );
            if (!directory.getOwner().getId().equals(user.getId()))
                throw new IllegalStateException("User does not have permission to view shared users of this directory");

            var sharedUsersDirs = sharedDirectoryRepository.findAllByDirectory_Id(directory.getId());
            for (SharedDirectory sharedDirectory: sharedUsersDirs) {
                sharedUsers.add(sharedDirectory.getUser());
            }
        } else {
            var file = fileRepository.findById(contentId).orElseThrow(
                    () -> new IllegalStateException("File not found")
            );
            if (!file.getOwner().getId().equals(user.getId()))
                throw new IllegalStateException("User does not have permission to view shared users of this file");

            var sharedUsersFiles = sharedFileRepository.findAllByFile_Id(file.getId());
            for (SharedFile sharedFile: sharedUsersFiles) {
                sharedUsers.add(sharedFile.getUser());
            }
        }
        return SharedUsersResponse.builder().users(sharedUsers.stream().map(userDTOMapper).toList()).build();
    }
}
