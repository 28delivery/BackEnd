package com.sparta.spring_deep._delivery.domain.address.controller;

import com.sparta.spring_deep._delivery.domain.address.dto.AddressRequestDto;
import com.sparta.spring_deep._delivery.domain.address.dto.AddressResponseDto;
import com.sparta.spring_deep._delivery.domain.address.dto.AddressSearchDto;
import com.sparta.spring_deep._delivery.domain.address.service.AddressService;
import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api")
@Slf4j(topic = "Address Controller")
public class AddressController {

    private final AddressService addressService;

    // 배송지 추가
    @PostMapping("/addresses")
    public ResponseEntity<AddressResponseDto> createAddress(
        @RequestBody AddressRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("배송지 추가");

        AddressResponseDto responseDto = addressService.createAddress(requestDto, userDetails);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 배송지 조회
    @GetMapping("/addresses/search")
    public ResponseEntity<Page<AddressResponseDto>> searchMyAddresses(
        @ModelAttribute AddressSearchDto searchDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PageableDefault(page = 0, size = 10, sort = "addressName", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("배송지 조회");

        Page<AddressResponseDto> responseDto = addressService.searchMyAddresses(searchDto,
            userDetails, pageable);

        return ResponseEntity.ok(responseDto);
    }


    // 개별 배송지 조회
    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressResponseDto> getAddress(
        @PathVariable UUID addressId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("개별 배송지 조회");

        AddressResponseDto responseDto = addressService.getAddress(addressId, userDetails);

        return ResponseEntity.ok(responseDto);
    }

    // 배송지 수정
    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressResponseDto> updateAddress(
        @PathVariable UUID addressId,
        @RequestBody AddressRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("배송지 수정");

        AddressResponseDto responseDto = addressService.updateAddress(addressId, requestDto,
            userDetails);

        return ResponseEntity.ok(responseDto);
    }

    // 배송지 삭제
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(
        @PathVariable UUID addressId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("배송지 삭제");

        addressService.deleteAddress(addressId, userDetails);

        return ResponseEntity.ok("Address deleted successfully");
    }

}