package com.cloudkeep.CloudKeep.directory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DirectoryRepository extends JpaRepository<Directory, Long> {
    Optional<Directory> findByName(String name);
    Optional<Directory> findByNameContaining(String name);

//    @Query("SELECT d FROM Directory d WHERE d.owner.id = ?1")
    List<Directory> findAllByOwner_Id(Long id);

    List<Directory> findAllByOwner_IdAndParentDirectory_Id(Long ownerId, Long parentId);
}
