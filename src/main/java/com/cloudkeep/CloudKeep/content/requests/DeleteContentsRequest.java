package com.cloudkeep.CloudKeep.content.requests;

import com.cloudkeep.CloudKeep.content.requests.helpers.DeleteContent;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
@Validated
public class DeleteContentsRequest {
    @Valid
    private List<DeleteContent> contents;
}
