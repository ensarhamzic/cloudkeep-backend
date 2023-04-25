package com.cloudkeep.CloudKeep.auth;

import com.cloudkeep.CloudKeep.auth.requests.LoginRequest;
import com.cloudkeep.CloudKeep.auth.requests.RegisterRequest;
import com.cloudkeep.CloudKeep.auth.responses.AuthenticationResponse;
import com.cloudkeep.CloudKeep.config.JwtService;
import com.cloudkeep.CloudKeep.user.User;
import com.cloudkeep.CloudKeep.user.UserDTOMapper;
import com.cloudkeep.CloudKeep.user.UserRepository;
import com.cloudkeep.CloudKeep.verification.Verification;
import com.cloudkeep.CloudKeep.verification.VerificationRepository;
import com.cloudkeep.CloudKeep.verification.requests.VerificationRequest;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.transactional.SendContact;
import com.mailjet.client.transactional.SendEmailsRequest;
import com.mailjet.client.transactional.TrackOpens;
import com.mailjet.client.transactional.TransactionalEmail;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {
    @PersistenceContext
    private EntityManager entityManager;
    private final UserRepository userRepository;
    private final VerificationRepository verificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDTOMapper userDTOMapper;

    @Value("${mailjet.apikey}")
    private String mailjetApiKey;

    @Value("${mailjet.apisecret}")
    private String mailjetApiSecret;
    public AuthenticationResponse register(RegisterRequest request) throws MailjetException {
        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            throw new IllegalStateException("Username is already taken");
        } else if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new IllegalStateException("Email is already taken");
        }
        var user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .verified(false)
                .build();
        userRepository.save(user);

        sendVerificationEmail(user);

        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .user(userDTOMapper.apply(user))
                .build();
    }

    public AuthenticationResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            var user = userRepository.findByUsername(request.getUsername()).orElseThrow();
            var jwtToken = jwtService.generateToken(user);
            if(!user.getVerified())
                sendVerificationEmail(user);

            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .user(userDTOMapper.apply(user))
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException("Invalid username or password");
        }
    }
    public AuthenticationResponse verifyEmail(VerificationRequest request) {
        var verificationOptional = verificationRepository.findByCodeAndUserEmail(request.getCode(), request.getEmail());
        if(verificationOptional.isEmpty())
            throw new IllegalStateException("Invalid code");
        var verification = verificationOptional.get();
        var user = verification.getUser();
        var now = LocalDateTime.now();
        var oneDayAgo = now.minusDays(1);
        long minutesDifference = ChronoUnit.MINUTES.between(verification.getCreatedAt(), LocalDateTime.now());
        if(minutesDifference > 15)
            throw new IllegalStateException("Code expired");

        entityManager.createQuery("DELETE FROM Verification t WHERE t.createdAt < :dayAgo")
                .setParameter("dayAgo", oneDayAgo)
                .executeUpdate();

        user.setVerified(true);
        userRepository.save(user);
        verificationRepository.delete(verification);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .user(userDTOMapper.apply(user))
                .build();
    }

    public AuthenticationResponse verifyToken(String header) {
        var user = jwtService.getUserFromToken(header);
        return AuthenticationResponse.builder()
                .token(jwtService.extractTokenFromHeader(header))
                .user(userDTOMapper.apply(user))
                .build();
    }

    @Transactional
    private void sendVerificationEmail(User user) throws MailjetException {
        var rand = new SecureRandom();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++)
            code.append(rand.nextInt(10));
        var verification = Verification.builder()
                .user(user)
                .code(code.toString())
                .build();
        verificationRepository.save(verification);

        ClientOptions options = ClientOptions.builder()
                .apiKey(mailjetApiKey)
                .apiSecretKey(mailjetApiSecret)
                .build();

        MailjetClient client = new MailjetClient(options);

        TransactionalEmail email = TransactionalEmail
                .builder()
                .to(new SendContact(user.getEmail(), String.format("%s %s", user.getFirstName(), user.getLastName())))
                .from(new SendContact("ensarhamzic01@gmail.com", "CloudKeep Team"))
                .htmlPart("<h1>Please enter the code in your app</h1><p>Your code: " + code + "</p>")
                .subject("Email verification")
                .trackOpens(TrackOpens.ENABLED)
                .build();

        SendEmailsRequest emailRequest = SendEmailsRequest.builder().message(email).build();
        emailRequest.sendWith(client);
    }
}
