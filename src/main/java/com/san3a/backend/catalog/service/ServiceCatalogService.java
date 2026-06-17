package com.san3a.backend.catalog.service;

import com.san3a.backend.catalog.dto.CreateServiceRequest;
import com.san3a.backend.catalog.dto.ServiceResponse;
import com.san3a.backend.domain.entity.ServiceCategory;
import com.san3a.backend.repository.ServiceCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceCatalogService {

    private final ServiceCategoryRepository serviceCategoryRepository;

    public ServiceResponse create(CreateServiceRequest request) {
        ServiceCategory service = new ServiceCategory();
        service.setName(request.name().trim());
        service.setDescription(request.description());
        service = serviceCategoryRepository.save(service);
        return toResponse(service);
    }

    public List<ServiceResponse> getAll() {
        return serviceCategoryRepository.findAll().stream().map(this::toResponse).toList();
    }

    private ServiceResponse toResponse(ServiceCategory service) {
        return new ServiceResponse(service.getServiceId(), service.getName(), service.getDescription());
    }
}
