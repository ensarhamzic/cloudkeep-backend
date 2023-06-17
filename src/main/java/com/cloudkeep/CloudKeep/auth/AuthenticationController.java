package com.cloudkeep.CloudKeep.auth;

import com.cloudkeep.CloudKeep.auth.requests.*;
import com.cloudkeep.CloudKeep.auth.responses.AuthenticationResponse;
import com.cloudkeep.CloudKeep.utils.responses.BasicResponse;
import com.cloudkeep.CloudKeep.verification.requests.VerificationRequest;
import com.mailjet.client.errors.MailjetException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request) throws MailjetException {
        AuthenticationResponse response = service.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/google")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterGoogleRequest request) {
        AuthenticationResponse response = service.authGoogle(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthenticationResponse response = service.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<AuthenticationResponse> verifyEmail(@Valid @RequestBody VerificationRequest request) {
        AuthenticationResponse response = service.verifyEmail(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-token")
    public ResponseEntity<AuthenticationResponse> verifyToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        AuthenticationResponse response = service.verifyToken(authHeader);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<BasicResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) throws MailjetException {
        return ResponseEntity.ok(service.forgotPassword(request.getEmail()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<BasicResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) throws MailjetException {
        return ResponseEntity.ok(service.resetPassword(request));
    }
}
