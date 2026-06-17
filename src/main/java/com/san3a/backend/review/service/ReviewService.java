package com.san3a.backend.review.service;

import com.san3a.backend.common.exception.BusinessException;
import com.san3a.backend.common.exception.ForbiddenOperationException;
import com.san3a.backend.common.exception.ResourceNotFoundException;
import com.san3a.backend.domain.entity.Review;
import com.san3a.backend.domain.entity.ServiceRequest;
import com.san3a.backend.domain.entity.Tasker;
import com.san3a.backend.domain.enums.AccountRole;
import com.san3a.backend.domain.enums.RequestStatus;
import com.san3a.backend.repository.ReviewRepository;
import com.san3a.backend.repository.ServiceRequestRepository;
import com.san3a.backend.repository.TaskerRepository;
import com.san3a.backend.review.dto.CreateReviewRequest;
import com.san3a.backend.review.dto.ReviewResponse;
import com.san3a.backend.review.dto.TaskerReviewSummaryResponse;
import com.san3a.backend.security.AppUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final TaskerRepository taskerRepository;

    @Transactional
    public ReviewResponse createReview(AppUserPrincipal principal, CreateReviewRequest payload) {
        enforceRole(principal, AccountRole.USER);

        ServiceRequest request = serviceRequestRepository.findById(payload.requestId())
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        if (!request.getUser().getUserId().equals(principal.getActorId())) {
            throw new ForbiddenOperationException("You can only review your own requests");
        }

        if (request.getStatus() != RequestStatus.DONE) {
            throw new BusinessException("Review is allowed only after request is marked as DONE");
        }

        if (request.getTasker() == null) {
            throw new BusinessException("Cannot review a request without an assigned tasker");
        }

        if (reviewRepository.existsById(request.getRequestId())) {
            throw new BusinessException("You already reviewed this request");
        }

        Review review = new Review();
        review.setRequest(request);
        review.setUser(request.getUser());
        review.setTasker(request.getTasker());
        review.setRating(payload.rating());
        review.setComment(payload.comment());
        review = reviewRepository.save(review);

        updateTaskerAverageRating(request.getTasker().getTaskerId());
        return toResponse(review);
    }

    @Transactional(readOnly = true)
    public ReviewResponse getByRequestId(AppUserPrincipal principal, Long requestId) {
        Review review = reviewRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        authorizeReviewAccess(principal, review.getRequest());
        return toResponse(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getMyUserReviews(AppUserPrincipal principal) {
        enforceRole(principal, AccountRole.USER);
        return reviewRepository.findByUserUserIdOrderByCreatedAtDesc(principal.getActorId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getMyTaskerReviews(AppUserPrincipal principal) {
        enforceRole(principal, AccountRole.TASKER);
        return reviewRepository.findByTaskerTaskerIdOrderByCreatedAtDesc(principal.getActorId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskerReviewSummaryResponse getTaskerSummary(Long taskerId) {
        taskerRepository.findById(taskerId)
                .orElseThrow(() -> new ResourceNotFoundException("Tasker not found"));

        List<ReviewResponse> reviews = reviewRepository.findByTaskerTaskerIdOrderByCreatedAtDesc(taskerId)
                .stream()
                .map(this::toResponse)
                .toList();

        Double avg = reviewRepository.findAverageRatingByTaskerId(taskerId);
        long total = reviewRepository.countByTaskerTaskerId(taskerId);

        return new TaskerReviewSummaryResponse(
                taskerId,
                avg == null ? 0.0 : avg,
                total,
                reviews
        );
    }

    private void updateTaskerAverageRating(Long taskerId) {
        Tasker tasker = taskerRepository.findById(taskerId)
                .orElseThrow(() -> new ResourceNotFoundException("Tasker not found"));

        Double avg = reviewRepository.findAverageRatingByTaskerId(taskerId);
        if (avg == null) {
            tasker.setRatingAvg(null);
        } else {
            tasker.setRatingAvg(BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP));
        }
        taskerRepository.save(tasker);
    }

    private void authorizeReviewAccess(AppUserPrincipal principal, ServiceRequest request) {
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

        throw new ForbiddenOperationException("You are not allowed to access this review");
    }

    private void enforceRole(AppUserPrincipal principal, AccountRole expected) {
        if (principal.getRole() != expected) {
            throw new ForbiddenOperationException("You are not allowed to access this endpoint");
        }
    }

    private ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getRequestId(),
                review.getUser().getUserId(),
                review.getTasker().getTaskerId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}
