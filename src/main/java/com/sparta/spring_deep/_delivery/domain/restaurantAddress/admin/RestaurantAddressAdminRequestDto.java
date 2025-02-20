package com.sparta.spring_deep._delivery.domain.restaurantAddress.admin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RestaurantAddressAdminRequestDto {

    private String roadAddr;
    private String detailAddr;

}
