package com.san3a.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "tasker_services")
public class TaskerService {

    @EmbeddedId
    private TaskerServiceId id = new TaskerServiceId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("taskerId")
    @JoinColumn(name = "tasker_id")
    private Tasker tasker;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("serviceId")
    @JoinColumn(name = "service_id")
    private ServiceCategory service;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}
