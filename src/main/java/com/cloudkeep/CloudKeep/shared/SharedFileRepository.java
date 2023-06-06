package com.cloudkeep.CloudKeep.shared;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SharedFileRepository extends JpaRepository<SharedFile, Long> {
    SharedFile findByFile_IdAndUser_Id(Long fileId, Long userId);
    List<SharedFile> findAllByFile_Id(Long fileId);
    List<SharedFile> findAllByUser_IdAndFile_DeletedFalse(Long userId);

}
