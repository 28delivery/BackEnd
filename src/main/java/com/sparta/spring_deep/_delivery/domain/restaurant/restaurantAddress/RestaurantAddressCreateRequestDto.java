package com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RestaurantAddressCreateRequestDto {

    private String roadAddr;
    private String detailAddr;

    public RestaurantAddressCreateRequestDto(String roadAddr, String detailAddr) {
        this.roadAddr = roadAddr;
        this.detailAddr = detailAddr;
    }
}
