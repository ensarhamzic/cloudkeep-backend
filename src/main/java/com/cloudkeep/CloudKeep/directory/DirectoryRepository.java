package com.cloudkeep.CloudKeep.directory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DirectoryRepository extends JpaRepository<Directory, Long> {
    Directory findByName(String name);
    Directory findByNameContaining(String name);
    Directory findByIdAndFavoriteTrue(Long id);
    Directory findByIdAndFavoriteFalse(Long id);
    List<Directory> findAllByOwner_Id(Long id);
    List<Directory> findAllByOwner_IdAndFavoriteTrueAndDeletedFalse(Long id);
    List<Directory> findAllByOwner_IdAndParentDirectory_IdAndDeletedFalse(Long ownerId, Long parentId);

    List<Directory> findAllByOwner_IdAndNameContainingAndDeletedFalse(Long id, String query);
    List<Directory> findAllByOwner_IdAndDeletedTrue(Long id);
}
