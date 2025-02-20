package com.sparta.spring_deep._delivery.domain.restaurantAddress.admin;


import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class RestaurantAddressAdminController {

    private final RestaurantAddressAdminService service;

    // 생성
    @PostMapping("/addresses")
    public ResponseEntity<RestaurantAddressAdminResponseDto> createAddress(
        @RequestBody RestaurantAddressAdminRequestDto dto) {
        RestaurantAddressAdminResponseDto response = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 단건 조회
    @GetMapping("/addresses/{id}")
    public ResponseEntity<RestaurantAddressAdminResponseDto> getAddressById(@PathVariable UUID id) {
        RestaurantAddressAdminResponseDto response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    // 전체 목록 조회
    @GetMapping
    public ResponseEntity<Page<RestaurantAddressAdminResponseDto>> getAllAddresses(
        Pageable pageable) {
        Page<RestaurantAddressAdminResponseDto> responses = service.getAll(pageable);
        return ResponseEntity.ok(responses);
    }

    // 수정
    @PutMapping("/addresses/{id}")
    public ResponseEntity<RestaurantAddressAdminResponseDto> updateAddress(@PathVariable UUID id,
        @RequestBody RestaurantAddressAdminRequestDto dto) {
        RestaurantAddressAdminResponseDto response = service.update(id, dto);
        return ResponseEntity.ok(response);
    }

    // 삭제
    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<RestaurantAddressAdminResponseDto> deleteAddress(@PathVariable UUID id) {
        RestaurantAddressAdminResponseDto response = service.delete(id);
        return ResponseEntity.ok(response);
    }

    // 검색 (예: 도로명 주소에 포함된 문자열)
    @GetMapping("/addresses/search")
    public ResponseEntity<Page<RestaurantAddressAdminResponseDto>> searchAddresses(
        @RequestParam("roadAddr") String roadAddr,
        Pageable pageable) {
        Page<RestaurantAddressAdminResponseDto> responses = service.searchByRoadAddr(roadAddr,
            pageable);
        return ResponseEntity.ok(responses);
    }
}