package com.cloudkeep.CloudKeep.file;

import com.cloudkeep.CloudKeep.directory.Directory;
import com.cloudkeep.CloudKeep.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "files",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "file_name_unique",
                        columnNames = {"name", "owner_id", "directory_id"}
                )
        }
    )
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class File {
    @Id
    @SequenceGenerator(
            name = "file_sequence",
            sequenceName = "file_sequence",
            allocationSize = 1)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "file_sequence")
    private Long id;

    @Column(nullable = false, length = 256)
    private String name;

    @Column(nullable = false, length = 256)
    private String path;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileType type;

    @Column(nullable = false)
    private Boolean favorite;

    @Column(nullable = false)
    private Boolean deleted;

    @ManyToOne(
            optional = false,
            cascade = CascadeType.ALL
    )
    @JoinColumn(
            name = "owner_id",
            referencedColumnName = "id"
    )
    private User owner;

    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(
            name = "directory_id",
            referencedColumnName = "id"
    )
    private Directory directory;
}
