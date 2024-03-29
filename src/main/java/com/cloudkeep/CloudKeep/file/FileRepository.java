package com.cloudkeep.CloudKeep.file;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    File findByName(String name);
    List<File> findAllByOwner_IdAndFavoriteTrueAndDeletedFalse(Long id);
    List<File> findAllByOwner_IdAndDirectory_IdAndDeletedFalse(Long ownerId, Long parentId);
    List<File> findAllByOwner_IdAndNameContainingAndDeletedFalse(Long id, String query);
    List<File> findAllByOwner_Id(Long id);
    List<File> findAllByOwner_IdAndDeletedTrue(Long id);
}
