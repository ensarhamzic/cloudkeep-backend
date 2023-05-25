package com.cloudkeep.CloudKeep.directory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DirectoryRepository extends JpaRepository<Directory, Long> {
    Optional<Directory> findByName(String name);
    Optional<Directory> findByNameContaining(String name);

    List<Directory> findAllByOwner_Id(Long id);

    List<Directory> findAllByOwner_IdAndParentDirectory_IdAndDeletedFalse(Long ownerId, Long parentId);
}
