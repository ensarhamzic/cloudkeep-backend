package com.cloudkeep.CloudKeep.auth;

import com.cloudkeep.CloudKeep.config.JwtService;
import com.cloudkeep.CloudKeep.user.User;
import com.cloudkeep.CloudKeep.user.UserRepository;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.transactional.SendContact;
import com.mailjet.client.transactional.SendEmailsRequest;
import com.mailjet.client.transactional.TrackOpens;
import com.mailjet.client.transactional.TransactionalEmail;
import com.mailjet.client.transactional.response.SendEmailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

//    @Value("${mailjet.apikey}")
//    private String mailjetApiKey;
//
//    @Value("${mailjet.apisecret}")
//    private String mailjetApiSecret;
    public AuthenticationResponse register(RegisterRequest request) throws MailjetException {
        var user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .verified(false)
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);


//        ClientOptions options = ClientOptions.builder()
//                .apiKey(mailjetApiKey)
//                .apiSecretKey(mailjetApiSecret)
//                .build();
//
//        MailjetClient client = new MailjetClient(options);
//
//        TransactionalEmail email = TransactionalEmail
//                .builder()
//                .to(new SendContact("ensarhamzic2001@gmail.com", "stanislav"))
//                .from(new SendContact("ensarhamzic01@gmail.com", "Mailjet integration test"))
//                .htmlPart("<h1>This is the HTML content of the mail</h1>")
//                .subject("This is the subject")
//                .trackOpens(TrackOpens.ENABLED)
//                .header("test-header-key", "test-value")
//                .customID("custom-id-value")
//                .build();
//
//
//        SendEmailsRequest emailRequest = SendEmailsRequest.builder().message(email).build();
//
//        SendEmailsResponse emailResponse = emailRequest.sendWith(client);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
