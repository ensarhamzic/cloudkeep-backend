package com.cloudkeep.CloudKeep.directory;

import com.cloudkeep.CloudKeep.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "directories")
@Data
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
    private String name;

    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(
            name = "parent_directory_id",
            referencedColumnName = "id"
    )
    private Directory parentDirectory;

    @OneToMany(
            mappedBy = "parentDirectory"
    )
    private List<Directory> subDirectories;
    @ManyToOne(
            optional = false,
            cascade = CascadeType.ALL
    )
    @JoinColumn(
            name = "owner_id",
            referencedColumnName = "id"
    )
    private User owner;

}
