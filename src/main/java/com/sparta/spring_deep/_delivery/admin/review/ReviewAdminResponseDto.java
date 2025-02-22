package com.sparta.spring_deep._delivery.admin.review;

import com.sparta.spring_deep._delivery.domain.review.Review;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewAdminResponseDto {

    private String id;
    private String orderId;
    private String userId;
    private String comment;
    private int rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private String createdBy;
    private String updatedBy;
    private String deletedBy;

    public ReviewAdminResponseDto(Review review) {
        this.id = review.getId().toString();
        this.orderId = review.getOrder().getId().toString();
        this.userId = review.getUser().getUsername();
        this.comment = review.getComment();
        this.rating = review.getRating();
        this.createdAt = review.getCreatedAt();
        this.updatedAt = review.getUpdatedAt();
        this.deletedAt = review.getDeletedAt();
        this.createdBy = review.getCreatedBy();
        this.updatedBy = review.getUpdatedBy();
        this.deletedBy = review.getDeletedBy();
    }


}
