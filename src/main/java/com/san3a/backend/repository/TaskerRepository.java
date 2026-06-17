package com.san3a.backend.repository;

import com.san3a.backend.domain.entity.Tasker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskerRepository extends JpaRepository<Tasker, Long> {
    Optional<Tasker> findByEmail(String email);
    boolean existsByEmail(String email);
}
