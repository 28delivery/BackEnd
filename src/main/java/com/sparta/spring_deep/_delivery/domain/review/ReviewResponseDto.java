package com.sparta.spring_deep._delivery.domain.review;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewResponseDto {

    private String id;
    private String restaurantId;
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

    public ReviewResponseDto(Review review) {
        this.id = review.getId().toString();
        this.restaurantId = review.getRestaurantId().toString();
        this.orderId = review.getOrderId().toString();
        this.userId = review.getUserId();
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
