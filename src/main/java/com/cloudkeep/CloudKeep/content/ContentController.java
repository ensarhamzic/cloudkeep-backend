package com.cloudkeep.CloudKeep.content;

import com.cloudkeep.CloudKeep.content.requests.ContentsRequest;
import com.cloudkeep.CloudKeep.content.requests.MoveContentsRequest;
import com.cloudkeep.CloudKeep.content.requests.RenameContentRequest;
import com.cloudkeep.CloudKeep.content.responses.RenameContentResponse;
import com.cloudkeep.CloudKeep.utils.responses.BasicResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contents")
@RequiredArgsConstructor
public class ContentController {
    private final ContentService contentService;

    @PostMapping()
    public ResponseEntity<BasicResponse> deleteContent(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody ContentsRequest request
    ) {
       return ResponseEntity.ok(contentService.deleteContent(token, request));
    }

    @PutMapping()
    public ResponseEntity<RenameContentResponse> renameContent(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody RenameContentRequest request
    ) {
       return ResponseEntity.ok(contentService.renameContent(token, request));
    }

    @PostMapping("/favorite")
    public ResponseEntity<BasicResponse> favoriteContent(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody ContentsRequest request
    ) {
       return ResponseEntity.ok(contentService.addRemoveFavorite(token, request));
    }

    @PostMapping("/move")
    public ResponseEntity<BasicResponse> moveContent(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody MoveContentsRequest request
    ) {
       return ResponseEntity.ok(contentService.moveContent(token, request));
    }

}
