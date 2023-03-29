package com.cloudkeep.CloudKeep.directory;

import com.cloudkeep.CloudKeep.file.File;
import com.cloudkeep.CloudKeep.user.User;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "directories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Directory {
    @Id
    @SequenceGenerator(
            name = "directory_sequence",
            sequenceName = "directory_sequence",
            allocationSize = 1)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "directory_sequence")
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(
            name = "parent_directory_id",
            referencedColumnName = "id"
    )
    @JsonIgnore
    private Directory parentDirectory;

    @OneToMany(
            mappedBy = "parentDirectory",
            cascade = CascadeType.ALL
    )
    @JsonIgnore
    private List<Directory> subDirectories;

    @OneToMany(
            mappedBy = "directory",
            cascade = CascadeType.ALL
    )
    @JsonIgnore
    private List<File> files;

    @ManyToOne(
            optional = false,
            cascade = CascadeType.ALL
    )
    @JoinColumn(
            name = "owner_id",
            referencedColumnName = "id"
    )
    @JsonIgnore
    private User owner;

    @JsonProperty("parentDirectory")
    public Long getParentDirectoryId() {
        if (parentDirectory == null) {
            return null;
        }
        return parentDirectory.getId();
    }
}
