package com.cloudkeep.CloudKeep.file;

import com.cloudkeep.CloudKeep.directory.Directory;
import com.cloudkeep.CloudKeep.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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

    @Column(nullable = false, length = 30)
    private String name;
    private String url;
    private String publicId;

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