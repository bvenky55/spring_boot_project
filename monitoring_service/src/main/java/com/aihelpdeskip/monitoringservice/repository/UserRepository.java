package com.aihelpdeskip.monitoringservice.repository;

import java.util.Optional;

import com.aihelpdeskip.monitoringservice.models.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}