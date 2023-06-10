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
import com.cloudkeep.CloudKeep.directory.DirectoryDTOMapper;
import com.cloudkeep.CloudKeep.directory.DirectoryRepository;
import com.cloudkeep.CloudKeep.directory.responses.GetDirectoriesResponse;
import com.cloudkeep.CloudKeep.file.File;
import com.cloudkeep.CloudKeep.file.FileDTOMapper;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
    private final DirectoryDTOMapper directoryDTOMapper;
    private final FileDTOMapper fileDTOMapper;

    public BasicResponse deleteContent(String token, ContentsRequest request, Boolean permanent) {
        var user = jwtService.getUserFromToken(token);

        List<Directory> directoriesToDelete = new ArrayList<>();
        List<File> filesToDelete = new ArrayList<>();
        for (OneContent content: request.getContents()) {
            if (content.getType().equals(ContentType.DIRECTORY)) {
                var directory = directoryRepository.findById(content.getId()).orElseThrow(
                        () -> new IllegalStateException("Directory not found")
                );
                if (!directory.getOwner().getId().equals(user.getId()))
                    throw new IllegalStateException("User does not have permission to delete this directory");
                directoriesToDelete.add(directory);
                directory.setDeleted(true);
                directory.setDateDeleted(new Date());
            } else {
                var file = fileRepository.findById(content.getId()).orElseThrow(
                        () -> new IllegalStateException("File not found")
                );
                if (!file.getOwner().getId().equals(user.getId()))
                    throw new IllegalStateException("User does not have permission to delete this file");
                filesToDelete.add(file);
                file.setDeleted(true);
                file.setDateDeleted(new Date());
            }
        }
        if(permanent) {
            directoryRepository.deleteAllInBatch(directoriesToDelete);
            fileRepository.deleteAllInBatch(filesToDelete);
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
            directory.setDateModified(new Date());
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
            file.setDateModified(new Date());
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

    public GetDirectoriesResponse searchContents(String token, String query) {
        var user = jwtService.getUserFromToken(token);
        var directories = directoryRepository.findAllByOwner_IdAndNameContainingAndDeletedFalse(user.getId(), query);
        var files = fileRepository.findAllByOwner_IdAndNameContainingAndDeletedFalse(user.getId(), query);
        return GetDirectoriesResponse.builder()
                .directories(directories.stream().map(directoryDTOMapper).toList())
                .files(files.stream().map(fileDTOMapper).toList())
                .build();
    }

    public GetDirectoriesResponse getDeletedContents(String token) {
        var user = jwtService.getUserFromToken(token);
        var directories = directoryRepository.findAllByOwner_IdAndDeletedTrue(user.getId());
        var files = fileRepository.findAllByOwner_IdAndDeletedTrue(user.getId());
        // get all directories whose deletedDate is more than 30 days ago
        var directoriesToDelete = directories.stream().filter(directory -> {
            var now = new Date();
            var deletedDate = directory.getDateDeleted();
            var diff = now.getTime() - deletedDate.getTime();
            var days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            return days > 30;
        }).toList();
        // get all files whose deletedDate is more than 30 days ago
        var filesToDelete = files.stream().filter(file -> {
            var now = new Date();
            var deletedDate = file.getDateDeleted();
            var diff = now.getTime() - deletedDate.getTime();
            var days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            return days > 30;
        }).toList();
        // delete all directories and files whose deletedDate is more than 30 days ago
        directoryRepository.deleteAllInBatch(directoriesToDelete);
        fileRepository.deleteAllInBatch(filesToDelete);
        // get all directories and files that are deleted
        directories = directoryRepository.findAllByOwner_IdAndDeletedTrue(user.getId());
        files = fileRepository.findAllByOwner_IdAndDeletedTrue(user.getId());
        return GetDirectoriesResponse.builder()
                .directories(directories.stream().map(directoryDTOMapper).toList())
                .files(files.stream().map(fileDTOMapper).toList())
                .build();
    }

    public BasicResponse restoreContent(String token, ContentsRequest request) {
        var user = jwtService.getUserFromToken(token);
        for (OneContent content : request.getContents()) {
            if (content.getType().equals(ContentType.DIRECTORY)) {
                var directory = directoryRepository.findById(content.getId()).orElseThrow(
                        () -> new IllegalStateException("Directory not found")
                );
                if (!directory.getOwner().getId().equals(user.getId()))
                    throw new IllegalStateException("User does not have permission to restore this directory");
                directory.setDeleted(false);
                directory.setDateDeleted(null);
                directoryRepository.save(directory);
            } else {
                var file = fileRepository.findById(content.getId()).orElseThrow(
                        () -> new IllegalStateException("File not found")
                );
                if (!file.getOwner().getId().equals(user.getId()))
                    throw new IllegalStateException("User does not have permission to restore this file");
                file.setDeleted(false);
                file.setDateDeleted(null);
                fileRepository.save(file);
            }
        }
        return BasicResponse.builder().message("Successfully restored content").build();
    }
}
