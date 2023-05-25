package com.cloudkeep.CloudKeep.content;

import com.cloudkeep.CloudKeep.config.JwtService;
import com.cloudkeep.CloudKeep.content.requests.DeleteContentsRequest;
import com.cloudkeep.CloudKeep.content.requests.helpers.ContentType;
import com.cloudkeep.CloudKeep.content.requests.helpers.DeleteContent;
import com.cloudkeep.CloudKeep.directory.DirectoryRepository;
import com.cloudkeep.CloudKeep.directory.requests.CreateDirectoryRequest;
import com.cloudkeep.CloudKeep.file.FileRepository;
import com.cloudkeep.CloudKeep.utils.responses.BasicResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ContentService {
    private final JwtService jwtService;
    private final DirectoryRepository directoryRepository;
    private final FileRepository fileRepository;

    public BasicResponse deleteContents(String token,  DeleteContentsRequest request) {
        var user = jwtService.getUserFromToken(token);

        for (DeleteContent content: request.getContents()) {
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
}
