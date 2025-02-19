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
