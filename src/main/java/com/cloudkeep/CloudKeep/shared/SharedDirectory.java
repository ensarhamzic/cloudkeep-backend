package com.cloudkeep.CloudKeep.shared;

import com.cloudkeep.CloudKeep.directory.Directory;
import com.cloudkeep.CloudKeep.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shared_directories")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@IdClass(SharedDirectoryKey.class)
public class SharedDirectory {
    @Id
    @ManyToOne(
            optional = false,
            cascade = CascadeType.ALL
    )
    @JoinColumn(
            name = "directory_id",
            referencedColumnName = "id"
    )
    private Directory directory;

    @Id
    @ManyToOne(
            optional = false,
            cascade = CascadeType.ALL
    )
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id"
    )
    private User user;
}
