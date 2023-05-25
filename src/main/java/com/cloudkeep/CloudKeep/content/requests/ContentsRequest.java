package com.cloudkeep.CloudKeep.content.requests;

import com.cloudkeep.CloudKeep.content.requests.helpers.OneContent;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
@Validated
public class ContentsRequest {
    @Valid
    private List<OneContent> contents;
}
