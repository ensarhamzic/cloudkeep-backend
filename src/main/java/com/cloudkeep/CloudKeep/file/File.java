package com.cloudkeep.CloudKeep.file;

import com.cloudkeep.CloudKeep.directory.Directory;
import com.cloudkeep.CloudKeep.shared.SharedFile;
import com.cloudkeep.CloudKeep.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Table(name = "files")
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

    @OneToMany(
            mappedBy = "file",
            cascade = CascadeType.ALL
    )
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<SharedFile> sharedUsers;
}
