package com.sparta.spring_deep._delivery.domain.address.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDto {

    private String addressId;
    private String addressName;
    private String address;

}