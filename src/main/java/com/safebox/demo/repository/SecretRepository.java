package com.safebox.demo.repository;

import com.safebox.demo.entity.Secret;
import com.safebox.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SecretRepository extends JpaRepository<Secret, UUID> {
    List<Secret> findByOwner(User owner);
}
