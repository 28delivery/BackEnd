package com.sparta.spring_deep._delivery.domain.restaurantAddress;

import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RestaurantAddressResponseDto {

    private UUID id;
    private String roadAddr;
    private String jibunAddr;
    private String detailAddr;
    private String engAddr;

    public RestaurantAddressResponseDto(RestaurantAddress restaurantAddress) {
        this.id = restaurantAddress.getId();
        this.roadAddr = restaurantAddress.getRoadAddr();
        this.jibunAddr = restaurantAddress.getJibunAddr();
        this.detailAddr = restaurantAddress.getDetailAddr();
        this.engAddr = restaurantAddress.getEngAddr();
    }

}
