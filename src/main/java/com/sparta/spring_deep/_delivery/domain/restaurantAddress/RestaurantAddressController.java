package com.sparta.spring_deep._delivery.domain.restaurantAddress;


import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api")
public class RestaurantAddressController {

    private final RestaurantAddressService service;

    // 생성
    @PostMapping
    public ResponseEntity<RestaurantAddressResponseDto> createAddress(
        @RequestBody RestaurantAddressRequestDto dto) {
        RestaurantAddressResponseDto response = service.create(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 단건 조회
    @GetMapping("/addresses/{id}")
    public ResponseEntity<RestaurantAddressResponseDto> getAddressById(@PathVariable UUID id) {
        RestaurantAddressResponseDto response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    // 전체 목록 조회
    @GetMapping
    public ResponseEntity<List<RestaurantAddressResponseDto>> getAllAddresses() {
        List<RestaurantAddressResponseDto> responses = service.getAll();
        return ResponseEntity.ok(responses);
    }

    // 수정
    @PutMapping("/addresses/{id}")
    public ResponseEntity<RestaurantAddressResponseDto> updateAddress(@PathVariable UUID id,
        @RequestBody RestaurantAddressRequestDto dto) {
        RestaurantAddressResponseDto response = service.update(id, dto);
        return ResponseEntity.ok(response);
    }

    // 삭제
    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 검색 (예: 도로명 주소에 포함된 문자열)
    @GetMapping("/addresses/search")
    public ResponseEntity<List<RestaurantAddressResponseDto>> searchAddresses(
        @RequestParam("roadAddr") String roadAddr) {
        List<RestaurantAddressResponseDto> responses = service.searchByRoadAddr(roadAddr);
        return ResponseEntity.ok(responses);
    }
}