package com.cloudkeep.CloudKeep.file;

import com.cloudinary.Cloudinary;
import com.cloudinary.Singleton;
import com.cloudinary.utils.ObjectUtils;
import com.cloudkeep.CloudKeep.config.JwtService;
import com.cloudkeep.CloudKeep.file.requests.FileUploadRequest;
import com.cloudkeep.CloudKeep.user.User;
import com.cloudkeep.CloudKeep.user.UserDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Transactional
public class FileService {
    private final JwtService jwtService;
    private final FileRepository fileRepository;
    public String uploadFile(String token, Long id, FileUploadRequest request) throws IOException {
        User user = jwtService.getUserFromToken(token);
        Cloudinary cloudinary = Singleton.getCloudinary();
        var uploadResults = cloudinary.uploader().upload(
                request.getFile().getBytes(),
                ObjectUtils.asMap("folder", "cloudkeep"));
        System.out.println(uploadResults.get("secure_url"));
        return "File uploaded";
    }
}
