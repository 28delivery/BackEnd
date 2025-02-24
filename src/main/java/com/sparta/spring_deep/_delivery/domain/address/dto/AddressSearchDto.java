package com.sparta.spring_deep._delivery.domain.address.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class AddressSearchDto {

    private String address;
    private String addressName;

}
