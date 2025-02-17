package com.sparta.spring_deep._delivery.domain.restaurant.entity;


import com.sparta.spring_deep._delivery.common.BaseEntity;
import com.sparta.spring_deep._delivery.domain.category.Category;
import com.sparta.spring_deep._delivery.domain.restaurant.dto.RestaurantRequestDto;
import com.sparta.spring_deep._delivery.domain.user.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "p_restaurant")
public class Restaurant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @NotNull
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull
    private Category category;

    @NotNull
    @Column(unique = true)
    private String name;

    @NotNull
    private String address;

    @Nullable
    private String phone;

    public Restaurant(RestaurantRequestDto restaurantRequestDto, User ownerId,
        Category categoryId, User user) {
        super(user);
        this.owner = ownerId;
        this.category = categoryId;
        this.name = restaurantRequestDto.getName();
        this.address = restaurantRequestDto.getAddress();
        this.phone = restaurantRequestDto.getPhone();
    }

    public void UpdateRestaurant(RestaurantRequestDto restaurantRequestDto, Category categoryId,
        User user) {
        this.category = categoryId;
        this.name = restaurantRequestDto.getName();
        this.address = restaurantRequestDto.getAddress();
        this.phone = restaurantRequestDto.getPhone();
        super.update(user);
    }

}
