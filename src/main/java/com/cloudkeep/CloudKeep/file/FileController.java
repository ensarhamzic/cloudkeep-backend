package com.cloudkeep.CloudKeep.file;

import com.cloudkeep.CloudKeep.file.requests.FilesUploadRequest;
import com.cloudkeep.CloudKeep.file.responses.FilesUploadResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(path = "/directory/upload")
    public ResponseEntity<FilesUploadResponse> uploadFile(
        @Valid @RequestBody FilesUploadRequest request,
        @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(fileService.uploadFile(token, request));
    }
}
