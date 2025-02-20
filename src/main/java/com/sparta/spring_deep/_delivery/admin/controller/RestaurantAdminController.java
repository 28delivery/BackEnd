package com.sparta.spring_deep._delivery.admin.controller;

import com.sparta.spring_deep._delivery.admin.dto.RestaurantAdminCreateRequestDto;
import com.sparta.spring_deep._delivery.admin.dto.RestaurantAdminRequestDto;
import com.sparta.spring_deep._delivery.admin.dto.RestaurantAdminResponseDto;
import com.sparta.spring_deep._delivery.admin.service.RestaurantManageAdminService;
import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/admin/restaurants")
@Slf4j(topic = "Admin Restaurant Controller")
public class RestaurantAdminController {

    private final RestaurantManageAdminService restaurantManageAdminService;

    // 음식점 상세 조회
    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantAdminResponseDto> getRestaurant(
        @PathVariable UUID restaurantId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        log.info("getRestaurant: {}", restaurantId);
        RestaurantAdminResponseDto restaurantAdminResponseDto = restaurantManageAdminService.getRestaurant(
            restaurantId, userDetails);

        return ResponseEntity.status(HttpStatus.OK).body(restaurantAdminResponseDto);
    }

    // 음식점 생성
    @PostMapping("/")
    public ResponseEntity<RestaurantAdminResponseDto> addRestaurant(
        @RequestBody RestaurantAdminCreateRequestDto restaurantAdminCreateRequestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        log.info("addRestaurant : {} ", restaurantAdminCreateRequestDto);
        RestaurantAdminResponseDto restaurantAdminResponseDto = restaurantManageAdminService.createRestaurant(
            restaurantAdminCreateRequestDto, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantAdminResponseDto);
    }

    // 음식점 정보 수정
    //    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{restaurantId}")
    public ResponseEntity<RestaurantAdminResponseDto> updateRestaurant(
        @RequestBody RestaurantAdminRequestDto restaurantAdminRequestDto,
        @PathVariable UUID restaurantId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        log.info("updateRestaurant: {}, {}", restaurantId, restaurantAdminRequestDto);
        RestaurantAdminResponseDto restaurantAdminResponseDto = restaurantManageAdminService.updateRestaurant(
            restaurantId, restaurantAdminRequestDto, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(restaurantAdminResponseDto);
    }

    // 음식점 삭제
    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<String> deleteRestaurant(
        @PathVariable UUID restaurantId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        log.info("deleteRestaurant: {}", restaurantId);
        if (restaurantManageAdminService.deleteRestaurant(restaurantId, userDetails)) {
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body("Restaurant deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Restaurant not found");
        }
    }

    //    @PreAuthorize("hasRole('OWNER')")
//    @GetMapping("/search")
//    public RestaurantResponseDto getRestaurant(
//        @RequestParam(required = false) UUID id,
//        @RequestParam(required = false) String restaurantName,
//        @RequestParam(required = false) String category,
//        @RequestParam(required = false) String address,
//        @RequestParam(required = false) String phone) {
//
//    }

}
