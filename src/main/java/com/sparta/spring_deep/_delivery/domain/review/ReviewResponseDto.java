package com.sparta.spring_deep._delivery.domain.review;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewResponseDto {

    private UUID id;
    private UUID orderId;
    private String userId;
    private String comment;
    private Integer rating;
    private LocalDateTime updatedAt;
    private String updatedBy;

    public ReviewResponseDto(Review review) {
        this.id = review.getId();
        this.orderId = review.getOrder().getId();
        this.userId = review.getUser().getUsername();
        this.comment = review.getComment();
        this.rating = review.getRating();
        this.updatedAt = review.getUpdatedAt();
        this.updatedBy = review.getUpdatedBy();
    }

}
