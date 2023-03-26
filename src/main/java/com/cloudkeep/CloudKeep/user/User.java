package com.cloudkeep.CloudKeep.user;

import com.cloudkeep.CloudKeep.directory.Directory;
import com.cloudkeep.CloudKeep.file.File;
import com.cloudkeep.CloudKeep.verification.Verification;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users",
    uniqueConstraints = {
        @UniqueConstraint(name = "email_unique", columnNames = "email"),
        @UniqueConstraint(name = "username_unique", columnNames = "username")
    }
)
public class User implements UserDetails {
    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence")
    private Long id;
    @Column(nullable = false, length = 30)
    private String firstName;
    @Column(nullable = false, length = 30)
    private String lastName;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false, length = 20)
    private String username;
    @Column(nullable = false)
    private String password;
    private String profilePicture;

    private Boolean verified;

    @OneToMany(
            mappedBy = "owner",
            cascade = CascadeType.ALL
    )
    private List<Directory> directories;

    @OneToMany(
            mappedBy = "owner",
            cascade = CascadeType.ALL
    )
    private List<File> files;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Verification> verifications;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
