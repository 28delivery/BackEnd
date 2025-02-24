package com.sparta.spring_deep._delivery.domain.address.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddressRequestDto {

    private String addressId;
    private String addressName;
    private String address;

}