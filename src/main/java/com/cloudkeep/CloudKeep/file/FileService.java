package com.cloudkeep.CloudKeep.file;

import com.cloudkeep.CloudKeep.config.JwtService;
import com.cloudkeep.CloudKeep.directory.Directory;
import com.cloudkeep.CloudKeep.directory.DirectoryRepository;
import com.cloudkeep.CloudKeep.file.requests.FilesUploadRequest;
import com.cloudkeep.CloudKeep.file.requests.helpers.UploadedFile;
import com.cloudkeep.CloudKeep.file.responses.FilesSizeResponse;
import com.cloudkeep.CloudKeep.file.responses.FilesUploadResponse;
import com.cloudkeep.CloudKeep.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Transactional
public class FileService {
    private final FileDTOMapper fileDTOMapper;
    private final JwtService jwtService;
    private final FileRepository fileRepository;
    private final DirectoryRepository directoryRepository;

    @Value("${storage.limit}")
    private Long storageLimit;
    public FilesUploadResponse uploadFile(String token, FilesUploadRequest request) {
        User user = jwtService.getUserFromToken(token);
        Directory directory = null;
        if(request.getDirectoryId() != null)
            directory = directoryRepository.findById(request.getDirectoryId()).orElseThrow(
                    () -> new IllegalStateException("Directory with id " + request.getDirectoryId() + " not found")
            );

        if(directory != null)
            if(!directory.getOwner().getId().equals(user.getId()))
                throw new IllegalStateException("You can't upload a file to a directory that doesn't belong to you");

        Long filesSize = getFilesSizeForUser(user);
        filesSize = filesSize + request.getFiles().stream().mapToLong(UploadedFile::getSize).sum();
        if(filesSize > storageLimit)
            throw new IllegalStateException("You don't have enough storage space");

        var filesInDir = fileRepository
                .findAllByOwner_IdAndDirectory_IdAndDeletedFalse(
                        user.getId(),
                        request.getDirectoryId() == null ? null : request.getDirectoryId()
                );

        List<File> files = new ArrayList<>();
        for(UploadedFile file : request.getFiles()) {
            String startingFileName = file.getName().trim();
            if(startingFileName.contains("."))
                startingFileName = startingFileName.substring(0, startingFileName.lastIndexOf('.'));
            AtomicReference<String> fileNameRef = new AtomicReference<>(startingFileName);
            int counter = 0;
            while (filesInDir.stream().anyMatch(f -> f.getName().equals(fileNameRef.get()))) {
                counter++;
                fileNameRef.set(startingFileName + " (" + counter + ")");
            }

            File newFile = File.builder()
                    .name(fileNameRef.get())
                    .path(file.getPath())
                    .type(file.getType())
                    .size(file.getSize())
                    .dateCreated(new Date())
                    .dateModified(new Date())
                    .owner(user)
                    .directory(directory)
                    .favorite(false)
                    .deleted(false)
                    .build();
            files.add(newFile);

            fileRepository.save(newFile);
        }
        if(directory != null)
            directory.setDateModified(new Date());

        return FilesUploadResponse.builder()
                .message("Files uploaded successfully")
                .data(files.stream().map(fileDTOMapper).toList())
                .build();
    }

    public FilesSizeResponse getFilesSize(String token) {
        User user = jwtService.getUserFromToken(token);
        Long size = getFilesSizeForUser(user);
        return FilesSizeResponse.builder()
                .size(size)
                .storageLimit(storageLimit)
                .build();
    }

    private Long getFilesSizeForUser(User user) {
        var files = fileRepository.findAllByOwner_Id(user.getId());
        Long size = 0L;
        for(File file : files)
            size += file.getSize();
        return size;
    }
}
