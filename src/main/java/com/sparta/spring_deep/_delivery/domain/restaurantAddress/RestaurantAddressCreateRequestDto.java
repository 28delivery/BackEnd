package com.sparta.spring_deep._delivery.domain.restaurantAddress;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RestaurantAddressCreateRequestDto {

    private String roadAddr;
    private String detailAddr;

}
