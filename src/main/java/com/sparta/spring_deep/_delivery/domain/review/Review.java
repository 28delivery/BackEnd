package com.sparta.spring_deep._delivery.domain.review;

import com.sparta.spring_deep._delivery.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "p_review")
public class Review extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "restaurant_id", nullable = false)
    private UUID restaurantId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private int rating;

    @Column
    private String comment;

    public Review(ReviewRequestDto requestDto, String userId) {
        this.orderId = requestDto.getOrderId();
        this.restaurantId = requestDto.getRestaurantId();
        this.userId = userId;
        this.comment = requestDto.getComment();
        this.rating = requestDto.getRating();
    }
}
