package com.cloudkeep.CloudKeep.file;

import com.cloudinary.Cloudinary;
import com.cloudinary.Singleton;
import com.cloudinary.utils.ObjectUtils;
import com.cloudkeep.CloudKeep.config.JwtService;
import com.cloudkeep.CloudKeep.config.firebase.FirebaseStorageStrategy;
import com.cloudkeep.CloudKeep.directory.Directory;
import com.cloudkeep.CloudKeep.directory.DirectoryRepository;
import com.cloudkeep.CloudKeep.file.requests.FileUploadRequest;
import com.cloudkeep.CloudKeep.file.responses.FileUploadResponse;
import com.cloudkeep.CloudKeep.user.User;
import com.google.cloud.storage.Blob;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class FileService {
    private final FileDTOMapper fileDTOMapper;
    private final JwtService jwtService;
    private final FileRepository fileRepository;
    private final DirectoryRepository directoryRepository;
    private final FirebaseStorageStrategy firebaseStorageStrategy;
    public FileUploadResponse uploadFile(String token, FileUploadRequest request) throws IOException {
        User user = jwtService.getUserFromToken(token);
        Directory directory = null;
        if(request.getDirectoryId() != null)
            directory = directoryRepository.findById(request.getDirectoryId()).orElseThrow();

        if(directory != null) {
            if(!directory.getOwner().getId().equals(user.getId()))
                throw new IllegalStateException("You can't upload a file to a directory that doesn't belong to you");
        }

        var filesInDir = fileRepository
                .findAllByOwner_IdAndDirectory_Id(
                        user.getId(),
                        request.getDirectoryId() == null ? null : request.getDirectoryId()
                );
        String fileName = request.getName().toLowerCase().trim();
        if(filesInDir.stream().anyMatch(file -> file.getName().equals(fileName)))
            throw new IllegalStateException("You already have a file with this name");


        // If every validation succeed, upload file
//        Cloudinary cloudinary = Singleton.getCloudinary();
//        var uploadResults = cloudinary.uploader().upload(
//                request.getFile().getBytes(),
//                ObjectUtils.asMap("folder", "cloudkeep"));

//        File file = File.builder()
//                .name(request.getName())
//                .url(uploadResults.get("secure_url").toString())
//                .publicId(uploadResults.get("public_id").toString())
//                .owner(user)
//                .directory(directory)
//                .build();

        Blob fileInfo = firebaseStorageStrategy.uploadFile(request.getFile());

        File file = File.builder()
                .name(request.getName())
                .path(fileInfo.getBlobId().getName())
                .owner(user)
                .directory(directory)
                .build();

        fileRepository.save(file);

        return FileUploadResponse.builder()
                .message("File uploaded successfully")
                .data(fileDTOMapper.apply(file))
                .build();
    }
}
