package com.cloudkeep.CloudKeep.directory;

import com.cloudkeep.CloudKeep.file.File;
import com.cloudkeep.CloudKeep.shared.SharedDirectory;
import com.cloudkeep.CloudKeep.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "directories")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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

    @Column(nullable = false, length = 256)
    private String name;

    @Column(nullable = false)
    private Date dateCreated;

    @Column(nullable = false)
    private Date dateModified;

    @Column(nullable = false)
    private Boolean favorite;

    @Column(nullable = false)
    private Boolean deleted;

    private Date dateDeleted;

    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(
            name = "parent_directory_id",
            referencedColumnName = "id"
    )
    private Directory parentDirectory;

    @OneToMany(
            mappedBy = "parentDirectory",
            cascade = CascadeType.ALL
    )
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Directory> subDirectories;

    @OneToMany(
            mappedBy = "directory",
            cascade = CascadeType.ALL
    )
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<File> files;

    @OneToMany(
            mappedBy = "directory",
            cascade = CascadeType.ALL
    )
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<SharedDirectory> sharedUsers;

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
