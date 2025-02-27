package com.sparta.spring_deep._delivery.admin.ai;

import com.sparta.spring_deep._delivery.domain.ai.Ai;
import com.sparta.spring_deep._delivery.domain.restaurant.Restaurant;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class AiLogResponseDto {

    private UUID aiLogId;
    private Restaurant restaurant;
    private UUID menuId;
    private String request;
    private String response;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private boolean isDeleted;
    private LocalDateTime deletedAt;
    private String deletedBy;

    public AiLogResponseDto(Ai aiLog) {
        this.aiLogId = aiLog.getId();
        this.restaurant = aiLog.getMenu().getRestaurant();
        this.menuId = aiLog.getMenu().getId();
        this.request = aiLog.getRequest();
        this.response = aiLog.getResponse();
        this.createdAt = aiLog.getCreatedAt();
        this.createdBy = aiLog.getCreatedBy();
        this.updatedAt = aiLog.getUpdatedAt();
        this.updatedBy = aiLog.getUpdatedBy();
        this.isDeleted = aiLog.getIsDeleted();
        this.deletedAt = aiLog.getDeletedAt();
        this.deletedBy = aiLog.getDeletedBy();
    }

}
