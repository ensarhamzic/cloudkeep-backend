package com.cloudkeep.CloudKeep.content.requests;

import com.cloudkeep.CloudKeep.content.requests.helpers.OneContent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ShareContentRequest {
    @NotNull(message = "Content must be provided")
    private OneContent content;
    private List<Long> userIds;
}
