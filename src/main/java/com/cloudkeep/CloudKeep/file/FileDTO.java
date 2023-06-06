package com.cloudkeep.CloudKeep.file;

public record FileDTO (Long id, String name, String path, FileType fileType, Boolean favorite, Long ownerId, Long directoryId, Boolean shared) {}
