package com.sparta.spring_deep._delivery.domain.review;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDto {

    private String orderId;
    private String comment;
    private int rating;

    public UUID getOrderId() {
        return UUID.fromString(orderId);
    }

}
