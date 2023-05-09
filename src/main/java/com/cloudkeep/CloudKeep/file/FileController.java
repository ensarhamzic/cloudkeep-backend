package com.cloudkeep.CloudKeep.file;

import com.cloudkeep.CloudKeep.ErrorResponse;
import com.cloudkeep.CloudKeep.config.JwtService;
import com.cloudkeep.CloudKeep.config.firebase.FirebaseStorageStrategy;
import com.cloudkeep.CloudKeep.file.requests.FileUploadRequest;
import com.cloudkeep.CloudKeep.user.UserDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(path = "/directory/upload")
    public ResponseEntity<?> uploadFile(
        @Valid @ModelAttribute FileUploadRequest request,
        @RequestHeader("Authorization") String token
    ) {
        try {
            return ResponseEntity.ok(fileService.uploadFile(token, request));
        } catch (IllegalStateException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Something went wrong");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleException(BindException ex) {
        BindingResult result = ex.getBindingResult();
        List<ObjectError> fieldErrors = result.getAllErrors();

        List<String> errors = fieldErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
        // Create custom error response object
        ErrorResponse errorResponse = new ErrorResponse("Validation Failed", errors);
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
