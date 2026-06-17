package com.san3a.backend.payment.service;

import com.san3a.backend.common.exception.BusinessException;
import com.san3a.backend.common.exception.ForbiddenOperationException;
import com.san3a.backend.common.exception.ResourceNotFoundException;
import com.san3a.backend.domain.entity.Payment;
import com.san3a.backend.domain.entity.ServiceRequest;
import com.san3a.backend.domain.enums.AccountRole;
import com.san3a.backend.domain.enums.RequestStatus;
import com.san3a.backend.payment.dto.CreatePaymentRequest;
import com.san3a.backend.payment.dto.PaymentResponse;
import com.san3a.backend.repository.PaymentRepository;
import com.san3a.backend.repository.ServiceRequestRepository;
import com.san3a.backend.security.AppUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ServiceRequestRepository serviceRequestRepository;

    @Transactional
    public PaymentResponse createPayment(AppUserPrincipal principal, CreatePaymentRequest payload) {
        enforceRole(principal, AccountRole.USER);

        ServiceRequest request = serviceRequestRepository.findById(payload.requestId())
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        if (!request.getUser().getUserId().equals(principal.getActorId())) {
            throw new ForbiddenOperationException("You can only pay your own requests");
        }

        if (request.getStatus() != RequestStatus.DONE) {
            throw new BusinessException("Payment is allowed only after request is marked as DONE");
        }

        if (paymentRepository.existsById(request.getRequestId())) {
            throw new BusinessException("This request is already paid");
        }

        Payment payment = new Payment();
        payment.setRequest(request);
        payment.setAmount(payload.amount());
        payment.setMethod(payload.method());
        payment.setTransactionRef(payload.transactionRef());
        payment.setPaidAt(LocalDateTime.now());

        return toResponse(paymentRepository.save(payment));
    }

    @Transactional(readOnly = true)
    public PaymentResponse getByRequestId(AppUserPrincipal principal, Long requestId) {
        Payment payment = paymentRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        authorizePaymentAccess(principal, payment.getRequest());
        return toResponse(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getMyUserPayments(AppUserPrincipal principal) {
        enforceRole(principal, AccountRole.USER);
        return paymentRepository.findByRequestUserUserIdOrderByPaidAtDesc(principal.getActorId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getMyTaskerPayments(AppUserPrincipal principal) {
        enforceRole(principal, AccountRole.TASKER);
        return paymentRepository.findByRequestTaskerTaskerIdOrderByPaidAtDesc(principal.getActorId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void authorizePaymentAccess(AppUserPrincipal principal, ServiceRequest request) {
        if (principal.getRole() == AccountRole.ADMIN) {
            return;
        }

        if (principal.getRole() == AccountRole.USER && request.getUser().getUserId().equals(principal.getActorId())) {
            return;
        }

        if (principal.getRole() == AccountRole.TASKER
                && request.getTasker() != null
                && request.getTasker().getTaskerId().equals(principal.getActorId())) {
            return;
        }

        throw new ForbiddenOperationException("You are not allowed to access this payment");
    }

    private void enforceRole(AppUserPrincipal principal, AccountRole expected) {
        if (principal.getRole() != expected) {
            throw new ForbiddenOperationException("You are not allowed to access this endpoint");
        }
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getRequestId(),
                payment.getAmount(),
                payment.getMethod(),
                payment.getTransactionRef(),
                payment.getPaidAt()
        );
    }
}
