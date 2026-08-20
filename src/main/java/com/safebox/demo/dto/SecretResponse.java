package com.safebox.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
public class SecretResponse {

    private UUID id;
    private String label;
    private String value;
    private String description;
    private Instant createdAt;
}
