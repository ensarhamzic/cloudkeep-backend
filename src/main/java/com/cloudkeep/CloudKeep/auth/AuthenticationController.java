package com.cloudkeep.CloudKeep.auth;

import com.cloudkeep.CloudKeep.ErrorResponse;
import com.cloudkeep.CloudKeep.auth.requests.LoginRequest;
import com.cloudkeep.CloudKeep.auth.requests.RegisterRequest;
import com.cloudkeep.CloudKeep.verification.requests.VerificationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    // JUST CLOUDINARY TEST
//    @PostMapping("/hello")
//    public ResponseEntity<?> hello() throws IOException {
//        Cloudinary cloudinary = Singleton.getCloudinary();
//        Map uploadResults = cloudinary.uploader().upload(
//                "https://img.freepik.com/free-photo/wide-angle-shot-single-tree-growing-clouded-sky-during-sunset-surrounded-by-grass_181624-22807.jpg",
//                ObjectUtils.asMap("folder", "cloudkeep"));
//        System.out.println(uploadResults.get("secure_url"));
//        return ResponseEntity.ok("Hello");
//    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthenticationResponse response = service.register(request);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Something went wrong");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthenticationResponse response = service.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody VerificationRequest request) {
        try {
            AuthenticationResponse response = service.verifyEmail(request);
            return ResponseEntity.ok(response);
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
