package com.safebox.demo.controller;

import com.safebox.demo.dto.SecretRequest;
import com.safebox.demo.dto.SecretResponse;
import com.safebox.demo.entity.User;
import com.safebox.demo.service.SecretService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/secrets")
@RequiredArgsConstructor
public class SecretController {

    private final SecretService secretService;

    @Operation(summary = "save a new secret")
    @PostMapping
    public ResponseEntity<SecretResponse> createSecret(@AuthenticationPrincipal User user,
                                                       @Valid @RequestBody SecretRequest request) {
        SecretResponse response = secretService.createSecret(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "show all secrets")
    @GetMapping
    public ResponseEntity<List<SecretResponse>> getAllSecrets(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(secretService.getAllSecrets(user));
    }

    @Operation(summary = "Get a secret by Id")
    @GetMapping("/{id}")
    public ResponseEntity<SecretResponse> getSecretById(@AuthenticationPrincipal User user,
                                                        @PathVariable UUID id) {
        return ResponseEntity.ok(secretService.getSecretById(id, user));
    }

    @Operation(summary = "Update a secret")
    @PutMapping("/{id}")
    public ResponseEntity<SecretResponse> updateSecret(@AuthenticationPrincipal User user,
                                                       @PathVariable UUID id,
                                                       @Valid @RequestBody SecretRequest request) {
        return ResponseEntity.ok(secretService.updateSecret(id, request, user));
    }

    @Operation(summary = "Delete a secret")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSecret(@AuthenticationPrincipal User user,
                                             @PathVariable UUID id) {
        secretService.deleteSecret(id, user);
        return ResponseEntity.noContent().build();
    }
}
