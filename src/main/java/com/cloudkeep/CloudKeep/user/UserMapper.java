package com.cloudkeep.CloudKeep.user;

public class UserMapper {
    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUsername())
                .profilePicture(user.getProfilePicture())
                .verified(user.getVerified())
                .build();
    }
}
