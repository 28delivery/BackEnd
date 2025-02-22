package com.sparta.spring_deep._delivery.admin.restaurant.restaurantAddress;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RestaurantAddressAdminRequestDto {

    private String roadAddr;
    private String detailAddr;

    public RestaurantAddressAdminRequestDto(String roadAddress, String detailAddress) {
        this.roadAddr = roadAddress;
        this.detailAddr = detailAddress;
    }
}
