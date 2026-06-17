package com.san3a.backend.repository;

import com.san3a.backend.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUserUserIdOrderByCreatedAtDesc(Long userId);
    List<Review> findByTaskerTaskerIdOrderByCreatedAtDesc(Long taskerId);
    Optional<Review> findByRequestRequestId(Long requestId);
    long countByTaskerTaskerId(Long taskerId);

    @Query("select avg(r.rating) from Review r where r.tasker.taskerId = :taskerId")
    Double findAverageRatingByTaskerId(@Param("taskerId") Long taskerId);
}
