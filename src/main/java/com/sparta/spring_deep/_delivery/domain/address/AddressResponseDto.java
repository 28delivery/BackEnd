package com.sparta.spring_deep._delivery.domain.address;

import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AddressResponseDto {

    private UUID id;
    private String username;
    private String address_name;
    private String address;

    public AddressResponseDto(Address address) {
        this.id = address.getId();
        this.username = address.getUser().getUsername();
        this.address_name = address.getAddress_name();
        this.address = address.getAddress();
    }

}
