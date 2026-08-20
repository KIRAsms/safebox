package com.safebox.demo.service;

import com.safebox.demo.dto.SecretRequest;
import com.safebox.demo.dto.SecretResponse;
import com.safebox.demo.entity.Secret;
import com.safebox.demo.entity.User;
import com.safebox.demo.exception.AccessDeniedException;
import com.safebox.demo.exception.ResourceNotFoundException;
import com.safebox.demo.repository.SecretRepository;
import com.safebox.demo.security.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SecretService {

    private final SecretRepository secretRepository;
    private final EncryptionUtils encryptionUtils;

    public SecretResponse createSecret(SecretRequest request, User owner) {
        Secret secret = new Secret();
        secret.setLabel(request.getLabel());
        secret.setValue(encryptionUtils.encrypt(request.getValue()));
        secret.setDescription(request.getDescription());
        secret.setOwner(owner);
        Secret saved = secretRepository.save(secret);
        return toResponse(saved);
    }

    public List<SecretResponse> getAllSecrets(User owner) {
        return secretRepository.findByOwner(owner)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public SecretResponse getSecretById(UUID id, User owner) {
        Secret secret = secretRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Secret not found"));
        if (!secret.getOwner().getId().equals(owner.getId())) {
            throw new AccessDeniedException("Access denied");
        }
        return toResponse(secret);
    }

    public SecretResponse updateSecret(UUID id, SecretRequest request, User owner) {
        Secret secret = secretRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Secret not found"));
        if (!secret.getOwner().getId().equals(owner.getId())) {
            throw new AccessDeniedException("Access denied");
        }
        secret.setLabel(request.getLabel());
        secret.setValue(encryptionUtils.encrypt(request.getValue()));
        secret.setDescription(request.getDescription());
        Secret saved = secretRepository.save(secret);
        return toResponse(saved);
    }

    public void deleteSecret(UUID id, User owner) {
        Secret secret = secretRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Secret not found"));
        if (!secret.getOwner().getId().equals(owner.getId())) {
            throw new AccessDeniedException("Access denied");
        }
        secretRepository.delete(secret);
    }

    private SecretResponse toResponse(Secret secret) {
        return new SecretResponse(
                secret.getId(),
                secret.getLabel(),
                encryptionUtils.decrypt(secret.getValue()),
                secret.getDescription(),
                secret.getCreatedAt()
        );
    }
}
