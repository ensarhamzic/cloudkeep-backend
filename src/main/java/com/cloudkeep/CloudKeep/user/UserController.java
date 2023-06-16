package com.cloudkeep.CloudKeep.user;

import com.cloudkeep.CloudKeep.user.requests.UpdateUserRequest;
import com.cloudkeep.CloudKeep.user.responses.SearchUsersResponse;
import com.cloudkeep.CloudKeep.utils.responses.BasicResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping(path = "/search")
    public ResponseEntity<SearchUsersResponse> searchUsers(
            @RequestParam String query,
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(userService.searchUsers(token, query));
    }

    @PutMapping()
    public ResponseEntity<BasicResponse> updateUser(
            @Valid @RequestBody UpdateUserRequest request,
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(userService.updateUser(token, request));
    }
}
