package com.cloudkeep.CloudKeep.shared;

import com.cloudkeep.CloudKeep.file.File;
import com.cloudkeep.CloudKeep.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shared_files")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@IdClass(SharedFile.class)
public class SharedFile {
    @Id
    @ManyToOne(
            optional = false
    )
    @JoinColumn(
            name = "file_id",
            referencedColumnName = "id"
    )
    private File file;

    @Id
    @ManyToOne(
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id"
    )
    private User user;
}
