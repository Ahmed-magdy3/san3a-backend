package com.san3a.backend.request.service;

import com.san3a.backend.common.exception.BusinessException;
import com.san3a.backend.common.exception.ForbiddenOperationException;
import com.san3a.backend.common.exception.ResourceNotFoundException;
import com.san3a.backend.domain.entity.ServiceCategory;
import com.san3a.backend.domain.entity.ServiceRequest;
import com.san3a.backend.domain.entity.Tasker;
import com.san3a.backend.domain.entity.User;
import com.san3a.backend.domain.enums.AccountRole;
import com.san3a.backend.domain.enums.RequestStatus;
import com.san3a.backend.repository.ServiceCategoryRepository;
import com.san3a.backend.repository.ServiceRequestRepository;
import com.san3a.backend.repository.TaskerRepository;
import com.san3a.backend.repository.UserRepository;
import com.san3a.backend.request.dto.CreateRequestRequest;
import com.san3a.backend.request.dto.RequestResponse;
import com.san3a.backend.request.dto.UpdateRequestStatusRequest;
import com.san3a.backend.security.AppUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final UserRepository userRepository;
    private final TaskerRepository taskerRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;

    @Transactional
    public RequestResponse create(AppUserPrincipal principal, CreateRequestRequest request) {
        enforceRole(principal, AccountRole.USER);

        User user = userRepository.findById(principal.getActorId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ServiceCategory serviceCategory = serviceCategoryRepository.findById(request.serviceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        ServiceRequest entity = new ServiceRequest();
        entity.setUser(user);
        entity.setService(serviceCategory);
        entity.setCity(request.city());
        entity.setStreet(request.street());
        entity.setBlock(request.block());
        entity.setApartment(request.apartment());
        entity.setScheduledAt(request.scheduledAt());
        entity.setStatus(RequestStatus.PENDING);
        entity.setNotes(request.notes());

        return toResponse(serviceRequestRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<RequestResponse> getMyUserRequests(AppUserPrincipal principal) {
        enforceRole(principal, AccountRole.USER);
        return serviceRequestRepository.findByUserUserIdOrderByCreatedAtDesc(principal.getActorId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RequestResponse> getMyTaskerRequests(AppUserPrincipal principal) {
        enforceRole(principal, AccountRole.TASKER);
        return serviceRequestRepository.findByTaskerTaskerIdOrderByCreatedAtDesc(principal.getActorId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public RequestResponse acceptRequest(AppUserPrincipal principal, Long requestId) {
        enforceRole(principal, AccountRole.TASKER);

        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        if (request.getTasker() != null) {
            throw new BusinessException("Request is already assigned to another tasker");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BusinessException("Only pending requests can be accepted");
        }

        Tasker tasker = taskerRepository.findById(principal.getActorId())
                .orElseThrow(() -> new ResourceNotFoundException("Tasker not found"));

        request.setTasker(tasker);
        request.setStatus(RequestStatus.ACCEPTED);

        return toResponse(serviceRequestRepository.save(request));
    }

    @Transactional
    public RequestResponse updateStatus(AppUserPrincipal principal, Long requestId, UpdateRequestStatusRequest payload) {
        enforceRole(principal, AccountRole.TASKER);

        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        if (request.getTasker() == null || !request.getTasker().getTaskerId().equals(principal.getActorId())) {
            throw new ForbiddenOperationException("You are not allowed to update this request");
        }

        RequestStatus next = payload.status();
        if (!isTransitionAllowed(request.getStatus(), next)) {
            throw new BusinessException("Invalid request status transition");
        }

        request.setStatus(next);
        return toResponse(serviceRequestRepository.save(request));
    }

    private boolean isTransitionAllowed(RequestStatus current, RequestStatus next) {
        if (current == RequestStatus.DONE || current == RequestStatus.CANCELED) {
            return false;
        }

        return switch (current) {
            case PENDING -> next == RequestStatus.ACCEPTED || next == RequestStatus.CANCELED;
            case ACCEPTED -> next == RequestStatus.IN_PROGRESS || next == RequestStatus.CANCELED;
            case IN_PROGRESS -> next == RequestStatus.DONE || next == RequestStatus.CANCELED;
            default -> false;
        };
    }

    private void enforceRole(AppUserPrincipal principal, AccountRole expected) {
        if (principal.getRole() != expected) {
            throw new ForbiddenOperationException("You are not allowed to access this endpoint");
        }
    }

    private RequestResponse toResponse(ServiceRequest request) {
        return new RequestResponse(
                request.getRequestId(),
                request.getUser().getUserId(),
                request.getTasker() != null ? request.getTasker().getTaskerId() : null,
                request.getService().getServiceId(),
                request.getService().getName(),
                request.getCity(),
                request.getStreet(),
                request.getBlock(),
                request.getApartment(),
                request.getScheduledAt(),
                request.getStatus(),
                request.getNotes(),
                request.getCreatedAt()
        );
    }
}
