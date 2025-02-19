package com.sparta.spring_deep._delivery.domain.address.controller;

import com.sparta.spring_deep._delivery.domain.address.dto.AddressRequestDto;
import com.sparta.spring_deep._delivery.domain.address.dto.AddressResponseDto;
import com.sparta.spring_deep._delivery.domain.address.service.AddressService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    // 배송지 추가
    @PostMapping
    public AddressResponseDto createAddress(
        @RequestBody AddressRequestDto requestDto) {
        return addressService.createAddress(requestDto);
    }

    // 배송지 조회
    @GetMapping
    public List<AddressResponseDto> getMyAddresses() {
        return addressService.getAllAddresses();
    }


    // 개별 배송지 조회
    @GetMapping("/{addressId}")
    public AddressResponseDto getAddress(
        @PathVariable UUID addressId) {
        return addressService.getAddress(addressId);
    }

    // 배송지 수정
    @PutMapping("/{addressId}")
    public AddressResponseDto updateAddress(
        @PathVariable UUID addressId,
        @RequestBody AddressRequestDto requestDto) {
        return addressService.updateAddress(addressId, requestDto);
    }

    // 배송지 삭제
    @DeleteMapping("/{addressId}")
    public ResponseEntity<String> deleteAddress(
        @PathVariable UUID addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.ok("Address deleted successfully");
    }

}