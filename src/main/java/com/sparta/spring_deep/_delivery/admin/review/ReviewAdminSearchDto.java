package com.sparta.spring_deep._delivery.admin.review;

import com.sparta.spring_deep._delivery.common.AdminSearchDto;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class ReviewAdminSearchDto extends AdminSearchDto {

    private UUID id;
    private UUID restaurantId;
    private UUID orderId;
    private String username;
    private Integer rating;
    private String comment;

}
