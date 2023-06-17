package com.cloudkeep.CloudKeep.user;

import com.cloudkeep.CloudKeep.auth.responses.AuthenticationResponse;
import com.cloudkeep.CloudKeep.config.JwtService;
import com.cloudkeep.CloudKeep.user.requests.UpdateUserRequest;
import com.cloudkeep.CloudKeep.user.responses.SearchUsersResponse;
import com.cloudkeep.CloudKeep.user.responses.UpdateUserResponse;
import com.cloudkeep.CloudKeep.utils.responses.BasicResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserDTOMapper userDTOMapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public SearchUsersResponse searchUsers(String token, String query) {
        User user = jwtService.getUserFromToken(token);
        query = query.toLowerCase();
        var foundUsers = userRepository.findFirst3ByUsernameContaining(query);
        foundUsers.removeIf(u -> u.getId().equals(user.getId()));
        return SearchUsersResponse.builder()
                .users(foundUsers.stream().map(userDTOMapper).toList())
                .build();
    }

    public UpdateUserResponse updateUser(String token, UpdateUserRequest request) {
        boolean shouldLogout = false;
        User user = jwtService.getUserFromToken(token);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        if(request.getProfilePicture() != null)
            user.setProfilePicture(request.getProfilePicture());

        if(!request.getUsername().equals(user.getUsername())) {
            if(userRepository.findByUsername(request.getUsername()).isPresent())
                throw new IllegalStateException("Username already taken");
            user.setUsername(request.getUsername());
            shouldLogout = true;
        }

        if(request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            shouldLogout = true;
        }


        userRepository.save(user);
        return UpdateUserResponse.builder()
                .user(userDTOMapper.apply(user))
                .token(jwtService.generateToken(user))
                .shouldLogout(shouldLogout)
                .build();
    }
}
