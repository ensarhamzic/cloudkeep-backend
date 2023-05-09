package com.cloudkeep.CloudKeep.file;

import com.cloudkeep.CloudKeep.directory.Directory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findByName(String name);
    List<File> findAllByOwner_IdAndDirectory_Id(Long ownerId, Long parentId);

}
