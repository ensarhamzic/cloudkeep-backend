package com.cloudkeep.CloudKeep.file;

import com.cloudinary.Cloudinary;
import com.cloudinary.Singleton;
import com.cloudinary.utils.ObjectUtils;
import com.cloudkeep.CloudKeep.config.JwtService;
import com.cloudkeep.CloudKeep.directory.Directory;
import com.cloudkeep.CloudKeep.directory.DirectoryRepository;
import com.cloudkeep.CloudKeep.file.requests.FileUploadRequest;
import com.cloudkeep.CloudKeep.file.responses.FileUploadResponse;
import com.cloudkeep.CloudKeep.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.IOException;


@Service
@RequiredArgsConstructor
@Transactional
public class FileService {
    private final FileDTOMapper fileDTOMapper;
    private final JwtService jwtService;
    private final FileRepository fileRepository;
    private final DirectoryRepository directoryRepository;
    public FileUploadResponse uploadFile(String token, Long directoryId, FileUploadRequest request) throws IOException {
        User user = jwtService.getUserFromToken(token);
        Directory directory = directoryRepository.findById(directoryId).orElseThrow();

        if(!directory.getOwner().getId().equals(user.getId()))
            throw new IllegalStateException("You can't upload a file to a directory that doesn't belong to you");

        if(directory.getFiles().stream().anyMatch(f -> f.getName().equals(request.getName())))
            throw new IllegalStateException("You already have a file with this name");

        // If every validation succeed, upload file
        Cloudinary cloudinary = Singleton.getCloudinary();
        var uploadResults = cloudinary.uploader().upload(
                request.getFile().getBytes(),
                ObjectUtils.asMap("folder", "cloudkeep"));

        File file = File.builder()
                .name(request.getName())
                .url(uploadResults.get("secure_url").toString())
                .publicId(uploadResults.get("public_id").toString())
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
