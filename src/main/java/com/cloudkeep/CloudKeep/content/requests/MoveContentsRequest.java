package com.cloudkeep.CloudKeep.content.requests;

import com.cloudkeep.CloudKeep.content.requests.helpers.OneContent;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
@Validated
public class MoveContentsRequest {
    @Valid
    private List<OneContent> contents;
    private Long destinationDirectoryId;
}
