package com.san3a.backend.domain.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
@EqualsAndHashCode
public class TaskerServiceId implements Serializable {
    private Long taskerId;
    private Long serviceId;
}
