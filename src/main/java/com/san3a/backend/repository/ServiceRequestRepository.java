package com.san3a.backend.repository;

import com.san3a.backend.domain.entity.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    List<ServiceRequest> findByUserUserIdOrderByCreatedAtDesc(Long userId);
    List<ServiceRequest> findByTaskerTaskerIdOrderByCreatedAtDesc(Long taskerId);
}
