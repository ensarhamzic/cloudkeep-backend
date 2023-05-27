package com.cloudkeep.CloudKeep.directory;

import com.cloudkeep.CloudKeep.directory.requests.CreateDirectoryRequest;
import com.cloudkeep.CloudKeep.directory.responses.CreateDirectoryResponse;
import com.cloudkeep.CloudKeep.directory.responses.GetDirectoriesResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/directories")
@RequiredArgsConstructor
public class DirectoryController {
    private final DirectoryService directoryService;

    @GetMapping
    public ResponseEntity<GetDirectoriesResponse> getDirectories(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestParam(required = false) Long directoryId,
            @RequestParam(required = false) Boolean favorite
    ) {
        return ResponseEntity.ok(directoryService.getDirectories(token, directoryId, favorite));
    }

    @PostMapping
    public ResponseEntity<CreateDirectoryResponse> createDirectory(
            @Valid @RequestBody CreateDirectoryRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return ResponseEntity.ok(directoryService.createDirectory(token, request));
    }
}
