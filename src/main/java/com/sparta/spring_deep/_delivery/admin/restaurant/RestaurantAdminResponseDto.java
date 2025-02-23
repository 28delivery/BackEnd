package com.sparta.spring_deep._delivery.admin.restaurant;

import com.sparta.spring_deep._delivery.domain.restaurant.Restaurant;
import com.sparta.spring_deep._delivery.domain.restaurant.Restaurant.CategoryEnum;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RestaurantAdminResponseDto {

    private UUID id;
    private String ownerId;
    private CategoryEnum category;
    private String name;
    private String roadAddr;
    private String jibunAddr;
    private String detailAddr;
    private String engAddr;
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
        this.ownerId = restaurant.getOwner().getUsername();
        this.category = restaurant.getCategory();
        this.name = restaurant.getName();
        this.roadAddr = restaurant.getRestaurantAddress().getRoadAddr();
        this.detailAddr = restaurant.getRestaurantAddress().getDetailAddr();
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
