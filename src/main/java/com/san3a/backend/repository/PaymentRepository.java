package com.san3a.backend.repository;

import com.san3a.backend.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByRequestUserUserIdOrderByPaidAtDesc(Long userId);
    List<Payment> findByRequestTaskerTaskerIdOrderByPaidAtDesc(Long taskerId);
}
