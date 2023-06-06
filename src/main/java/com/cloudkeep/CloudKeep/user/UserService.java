package com.cloudkeep.CloudKeep.user;

import com.cloudkeep.CloudKeep.config.JwtService;
import com.cloudkeep.CloudKeep.user.responses.SearchUsersResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserDTOMapper userDTOMapper;
    private final JwtService jwtService;

    public SearchUsersResponse searchUsers(String token, String query) {
        User user = jwtService.getUserFromToken(token);
        var foundUsers = userRepository.findFirst3ByUsernameContaining(query);
        foundUsers.removeIf(u -> u.getId().equals(user.getId()));
        return SearchUsersResponse.builder()
                .users(foundUsers.stream().map(userDTOMapper).toList())
                .build();
    }
}
