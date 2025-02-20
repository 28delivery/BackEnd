package com.sparta.spring_deep._delivery.domain.restaurantAddress.admin;

import com.sparta.spring_deep._delivery.domain.restaurantAddress.RestaurantAddress;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RestaurantAddressAdminResponseDto {

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

    public RestaurantAddressAdminResponseDto(RestaurantAddress restaurantAddress) {
        this.roadAddr = restaurantAddress.getRoadAddr();
        this.roadAddrPart1 = restaurantAddress.getRoadAddrPart1();
        this.roadAddrPart2 = restaurantAddress.getRoadAddrPart2();
        this.jibunAddr = restaurantAddress.getJibunAddr();
        this.detailAddr = restaurantAddress.getDetailAddr();
        this.engAddr = restaurantAddress.getEngAddr();
        this.zipNo = restaurantAddress.getZipNo();
        this.siNm = restaurantAddress.getSiNm();
        this.sggNm = restaurantAddress.getSggNm();
        this.emdNm = restaurantAddress.getEmdNm();
        this.liNm = restaurantAddress.getLiNm();
        this.rn = restaurantAddress.getRn();
        this.udrtYn = restaurantAddress.getUdrtYn();
        this.buldMnnm = restaurantAddress.getBuldMnnm();
        this.buldSlno = restaurantAddress.getBuldSlno();
    }

}
