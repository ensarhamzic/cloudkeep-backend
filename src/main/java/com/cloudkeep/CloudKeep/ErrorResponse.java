package com.cloudkeep.CloudKeep;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorResponse {
    private String message;
    private List<String> errors;

    public ErrorResponse(String message) {
        this.message = message;
    }
}
