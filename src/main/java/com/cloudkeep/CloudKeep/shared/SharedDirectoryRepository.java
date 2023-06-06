package com.cloudkeep.CloudKeep.shared;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SharedDirectoryRepository extends JpaRepository<SharedDirectory, Long> {
    SharedDirectory findByDirectory_IdAndUser_Id(Long directoryId, Long userId);
    List<SharedDirectory> findAllByDirectory_Id(Long directoryId);
    List<SharedDirectory> findAllByUser_IdAndDirectory_DeletedFalse(Long userId);

}
