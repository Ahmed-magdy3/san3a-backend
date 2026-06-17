package com.san3a.backend.catalog.controller;

import com.san3a.backend.catalog.dto.CreateServiceRequest;
import com.san3a.backend.catalog.dto.ServiceResponse;
import com.san3a.backend.catalog.service.ServiceCatalogService;
import com.san3a.backend.common.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class ServiceCatalogController {

    private final ServiceCatalogService serviceCatalogService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ServiceResponse>>> getAllServices() {
        return ResponseEntity.ok(ApiResponse.ok("Services fetched successfully", serviceCatalogService.getAll()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ServiceResponse>> createService(@Valid @RequestBody CreateServiceRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Service created successfully", serviceCatalogService.create(request)));
    }
}
