package com.sparta.spring_deep._delivery.domain.review;

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

    public ReviewResponseDto(Review review) {
        this.id = review.getId().toString();
        this.restaurantId = review.getRestaurantId().toString();
        this.orderId = review.getOrderId().toString();
        this.userId = review.getUserId();
        this.comment = review.getComment();
        this.rating = review.getRating();
    }

}
