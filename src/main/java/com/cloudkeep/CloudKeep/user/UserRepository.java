package com.cloudkeep.CloudKeep.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    User findByEmailAndGoogleIsNullOrGoogleFalse(String email);
    User findByEmailAndGoogleTrue(String email);

    List<User> findFirst3ByUsernameContaining(String query);

}
