package com.safebox.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SecretRequest {

    @NotBlank(message = "Label is required")
    private String label;

    @NotBlank(message = "Value is required")
    private String value;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;
}
