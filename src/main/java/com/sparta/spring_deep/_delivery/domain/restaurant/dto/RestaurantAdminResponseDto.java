package com.sparta.spring_deep._delivery.domain.restaurant.dto;

import com.sparta.spring_deep._delivery.domain.category.Category;
import com.sparta.spring_deep._delivery.domain.user.User;
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
    private User createdBy;
    private LocalDateTime updatedAt;
    private User updatedBy;
    private Boolean isDeleted;
    private LocalDateTime deletedAt;
    private User deletedBy;
}
