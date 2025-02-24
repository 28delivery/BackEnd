package com.sparta.spring_deep._delivery.admin.restaurant;

import com.sparta.spring_deep._delivery.common.AdminSearchDto;
import com.sparta.spring_deep._delivery.domain.restaurant.Restaurant.CategoryEnum;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class RestaurantAdminSearchDto extends AdminSearchDto {

    private UUID id;
    private String ownerId;
    private String name;
    private CategoryEnum category;
    private String roadAddr;
    private String jibunAddr;
    private String detailAddr;
    private String engAddr;
    private String phone;

}
