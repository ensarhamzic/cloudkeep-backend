package com.cloudkeep.CloudKeep.user;

public record UserDTO(
        Long id,
        String firstName,
        String lastName,
        String email,
        String username,
        String profilePicture,
        Boolean verified
) {
}
