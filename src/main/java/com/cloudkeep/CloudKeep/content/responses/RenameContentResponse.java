package com.cloudkeep.CloudKeep.content.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RenameContentResponse {
    private String message;
    private String name;
}
