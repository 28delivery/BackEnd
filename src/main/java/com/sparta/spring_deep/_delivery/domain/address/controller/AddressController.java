package com.sparta.spring_deep._delivery.domain.address.controller;

import com.sparta.spring_deep._delivery.domain.address.dto.AddressRequestDto;
import com.sparta.spring_deep._delivery.domain.address.dto.AddressResponseDto;
import com.sparta.spring_deep._delivery.domain.address.dto.AddressSearchDto;
import com.sparta.spring_deep._delivery.domain.address.service.AddressService;
import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    public ResponseEntity<AddressResponseDto> createAddress(
        @RequestBody AddressRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        AddressResponseDto responseDto = addressService.createAddress(requestDto, userDetails);

        return ResponseEntity.ok(responseDto);
    }

    // 배송지 조회
    @GetMapping("/search")
    public ResponseEntity<Page<AddressResponseDto>> searchMyAddresses(
        @ModelAttribute AddressSearchDto searchDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PageableDefault(page = 0, size = 10, sort = "addressName", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<AddressResponseDto> responseDto = addressService.searchMyAddresses(searchDto,
            userDetails, pageable);

        return ResponseEntity.ok(responseDto);
    }


    // 개별 배송지 조회
    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponseDto> getAddress(
        @PathVariable UUID addressId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        AddressResponseDto responseDto = addressService.getAddress(addressId, userDetails);

        return ResponseEntity.ok(responseDto);
    }

    // 배송지 수정
    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponseDto> updateAddress(
        @PathVariable UUID addressId,
        @RequestBody AddressRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        AddressResponseDto responseDto = addressService.updateAddress(addressId, requestDto,
            userDetails);

        return ResponseEntity.ok(responseDto);
    }

    // 배송지 삭제
    @DeleteMapping("/{addressId}")
    public ResponseEntity<String> deleteAddress(
        @PathVariable UUID addressId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        addressService.deleteAddress(addressId, userDetails);

        return ResponseEntity.ok("Address deleted successfully");
    }

}