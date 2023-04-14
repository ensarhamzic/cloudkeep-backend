package com.cloudkeep.CloudKeep.directory;

import com.cloudkeep.CloudKeep.ErrorResponse;
import com.cloudkeep.CloudKeep.auth.AuthenticationResponse;
import com.cloudkeep.CloudKeep.config.JwtService;
import com.cloudkeep.CloudKeep.directory.requests.CreateDirectoryRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/directories")
@RequiredArgsConstructor
public class DirectoryController {
    private final DirectoryService directoryService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<?> getDirectories(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
                                            @RequestParam(required = false) Long directoryId) {
        try {
            Long userId = jwtService.extractId(authHeader);
            return ResponseEntity.ok(directoryService.getDirectories(userId, directoryId));
        } catch (IllegalStateException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping
    public ResponseEntity<?> createDirectory(
            @Valid @RequestBody CreateDirectoryRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            Long userId = jwtService.extractId(authHeader);
            return ResponseEntity.ok(directoryService.createDirectory(request, userId));
        } catch (IllegalStateException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }

    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<ObjectError> fieldErrors = result.getAllErrors();

        List<String> errors = fieldErrors.stream().map(error -> error.getDefaultMessage()).toList();

        // Create custom error response object
        ErrorResponse errorResponse = new ErrorResponse("Validation Failed", errors);
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
