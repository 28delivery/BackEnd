package com.sparta.spring_deep._delivery.domain.review;

import com.sparta.spring_deep._delivery.common.BaseEntity;
import com.sparta.spring_deep._delivery.domain.order.Order;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "p_review")
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Order order;

    ///  restaurantId 삭제
//    @Column(name = "restaurant_id", nullable = false)
//    private UUID restaurantId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private int rating;

    @Column
    private String comment;

    public Review(ReviewRequestDto requestDto, Order order, User user) {
        super(user.getUsername());
        this.order = order;
        this.userId = user.getUsername();
        this.comment = requestDto.getComment();
        this.rating = requestDto.getRating();
    }

    public void updateReview(String comment, int rating, User user) {
        this.comment = comment;
        this.rating = rating;
        super.update(user.getUsername());
    }
}
