package com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RestaurantAddressRequestDto {

    private String roadAddr;
    private String roadAddrPart1;
    private String roadAddrPart2;
    private String jibunAddr;
    private String detailAddr;
    private String engAddr;
    private String zipNo;
    private String siNm;
    private String sggNm;
    private String emdNm;
    private String liNm;
    private String rn;
    private String udrtYn;
    private String buldMnnm;
    private String buldSlno;

}
