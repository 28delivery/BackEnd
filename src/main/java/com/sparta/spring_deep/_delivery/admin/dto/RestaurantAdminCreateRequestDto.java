package com.sparta.spring_deep._delivery.admin.dto;

import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RestaurantAdminCreateRequestDto {

    private String ownerId;
    private String name;
    private UUID categoryId;
    private String roadAddr;
    private String detailAddr;
    private String phone;
}
