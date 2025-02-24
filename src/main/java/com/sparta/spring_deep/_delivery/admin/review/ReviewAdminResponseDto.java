package com.sparta.spring_deep._delivery.admin.review;

import com.sparta.spring_deep._delivery.domain.review.Review;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewAdminResponseDto {

    private UUID id;
    private UUID orderId;
    private UUID restaurantId;
    private String userId;
    private String comment;
    private Integer rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private String createdBy;
    private String updatedBy;
    private String deletedBy;
    private Boolean isDeleted;

    public ReviewAdminResponseDto(Review review) {
        this.id = review.getId();
        this.orderId = review.getOrder().getId();
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
