package com.cloudkeep.CloudKeep.file;

import com.cloudkeep.CloudKeep.ErrorResponse;
import com.cloudkeep.CloudKeep.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final JwtService jwtService;

//    @PostMapping("/directory/{id}/upload")
//    public ResponseEntity<?> uploadFile(
//        @PathVariable Long id,
//        @RequestBody FileUploadRequest request,
//        @RequestHeader("Authorization") String token
//    ) {
//        try {
//            String username = jwtService.getUsernameFromToken(token);
//            FileDTO file = fileService.uploadFile(id, request, username);
//            return ResponseEntity.ok(file);
//        } catch (IllegalStateException e) {
//            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
//            return ResponseEntity.badRequest().body(errorResponse);
//        } catch (Exception e) {
//            ErrorResponse errorResponse = new ErrorResponse("Something went wrong");
//            return ResponseEntity.badRequest().body(errorResponse);
//        }
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<ObjectError> fieldErrors = result.getAllErrors();

        List<String> errors = fieldErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();

        // Create custom error response object
        ErrorResponse errorResponse = new ErrorResponse("Validation Failed", errors);
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
