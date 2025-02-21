package com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress;


import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/api")
public class RestaurantAddressController {

    private final RestaurantAddressService service;

    // 생성
    @PostMapping("/restaurantAddresses")
    public ResponseEntity<RestaurantAddressResponseDto> createAddress(
        @RequestBody RestaurantAddressCreateRequestDto dto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        RestaurantAddressResponseDto response = service.create(dto, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 단건 조회
    @GetMapping("/restaurantAddresses/{id}")
    public ResponseEntity<RestaurantAddressResponseDto> getAddressById(@PathVariable UUID id) {
        RestaurantAddressResponseDto response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    // 수정
    @PutMapping("/restaurantAddresses/{id}")
    public ResponseEntity<RestaurantAddressResponseDto> updateAddress(@PathVariable UUID id,
        @RequestBody RestaurantAddressCreateRequestDto dto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        RestaurantAddressResponseDto response = service.update(id, dto, userDetails);
        return ResponseEntity.ok(response);
    }

    // 삭제
    @DeleteMapping("/restaurantAddresses/{id}")
    public ResponseEntity<RestaurantAddressResponseDto> deleteAddress(@PathVariable UUID id,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        RestaurantAddressResponseDto response = service.delete(id, userDetails);
        return ResponseEntity.ok(response);
    }
}