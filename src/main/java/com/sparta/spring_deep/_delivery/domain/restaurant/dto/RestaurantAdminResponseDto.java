package com.sparta.spring_deep._delivery.domain.restaurant.dto;

import com.sparta.spring_deep._delivery.domain.category.Category;
import com.sparta.spring_deep._delivery.domain.restaurant.entity.Restaurant;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestaurantAdminResponseDto {

    private UUID id;
    private User owner_id;
    private Category categoryId;
    private String name;
    private String address;
    private String phone;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private boolean isDeleted;
    private LocalDateTime deletedAt;
    private String deletedBy;

    public RestaurantAdminResponseDto(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.owner_id = restaurant.getOwner();
        this.categoryId = restaurant.getCategory();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.phone = restaurant.getPhone();
        this.createdAt = restaurant.getCreatedAt();
        this.createdBy = restaurant.getCreatedBy();
        this.updatedAt = restaurant.getUpdatedAt();
        this.updatedBy = restaurant.getUpdatedBy();
        this.isDeleted = restaurant.getIsDeleted();
        this.deletedAt = restaurant.getDeletedAt();
        this.deletedBy = restaurant.getDeletedBy();
    }
}
