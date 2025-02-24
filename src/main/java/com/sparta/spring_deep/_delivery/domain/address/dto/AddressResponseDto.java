package com.sparta.spring_deep._delivery.domain.address.dto;

import com.sparta.spring_deep._delivery.domain.address.entity.Address;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AddressResponseDto {

    private UUID id;
    private String username;
    private String addressName;
    private String address;

    public AddressResponseDto(Address address) {
        this.id = address.getId();
        this.username = address.getUser().getUsername();
        this.addressName = address.getAddressName();
        this.address = address.getAddress();
    }

}