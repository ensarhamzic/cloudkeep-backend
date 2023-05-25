package com.cloudkeep.CloudKeep.file;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    File findByName(String name);
    List<File> findAllByOwner_IdAndFavoriteTrue(Long id);
    List<File> findAllByOwner_IdAndDirectory_IdAndDeletedFalse(Long ownerId, Long parentId);

}
