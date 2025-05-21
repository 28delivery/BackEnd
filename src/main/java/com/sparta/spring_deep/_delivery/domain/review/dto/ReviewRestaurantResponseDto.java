package com.sparta.spring_deep._delivery.domain.review.dto;

import com.sparta.spring_deep._delivery.domain.review.model.Review;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
public class ReviewRestaurantResponseDto {

    private UUID restaurantId;
    private List<ReviewResponseDto> reviews  = new ArrayList<>();

    private Double AvgRating;

    public ReviewRestaurantResponseDto(List<ReviewResponseDto> reviews, Double AvgRating, UUID restaurantId) {
        this.restaurantId = restaurantId;
        this.AvgRating = AvgRating;
        this.reviews =reviews;
    }

}
