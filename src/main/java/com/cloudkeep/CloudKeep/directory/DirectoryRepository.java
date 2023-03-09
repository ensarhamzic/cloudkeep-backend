package com.cloudkeep.CloudKeep.directory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DirectoryRepository extends JpaRepository<Directory, Long> {
    Optional<Directory> findByName(String name);
    Optional<Directory> findByNameContaining(String name);
}
