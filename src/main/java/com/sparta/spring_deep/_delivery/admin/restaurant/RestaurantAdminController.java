package com.sparta.spring_deep._delivery.admin.restaurant;

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
@RequestMapping("/admin/restaurants")
@Slf4j(topic = "Admin Restaurant Controller")
public class RestaurantAdminController {

    private final RestaurantManageAdminService restaurantManageAdminService;
    private final RestaurantAdminService restaurantAdminService;

    // 음식점 상세 조회
    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantAdminResponseDto> getRestaurant(
        @PathVariable UUID restaurantId) {
        log.info("음식점 상세 조회");

        RestaurantAdminResponseDto restaurantAdminResponseDto = restaurantManageAdminService.getRestaurant(
            restaurantId);

        return ResponseEntity.status(HttpStatus.OK).body(restaurantAdminResponseDto);
    }

    // 음식점 생성
    @PostMapping
    public ResponseEntity<RestaurantAdminResponseDto> addRestaurant(
        @RequestBody RestaurantAdminCreateRequestDto restaurantAdminCreateRequestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("음식점 생성");

        RestaurantAdminResponseDto restaurantAdminResponseDto = restaurantManageAdminService.createRestaurant(
            restaurantAdminCreateRequestDto, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantAdminResponseDto);
    }

    // 음식점 정보 수정
    @PutMapping("/{restaurantId}")
    public ResponseEntity<RestaurantAdminResponseDto> updateRestaurant(
        @RequestBody RestaurantAdminRequestDto restaurantAdminRequestDto,
        @PathVariable UUID restaurantId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("음식점 정보 수정");

        RestaurantAdminResponseDto restaurantAdminResponseDto = restaurantManageAdminService.updateRestaurant(
            restaurantId, restaurantAdminRequestDto, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(restaurantAdminResponseDto);
    }

    // 음식점 삭제
    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<?> deleteRestaurant(
        @PathVariable UUID restaurantId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("음식점 삭제");

        restaurantManageAdminService.deleteRestaurant(restaurantId, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<RestaurantAdminResponseDto>> searchRestaurant(
        @ModelAttribute RestaurantAdminSearchDto restaurantAdminSearchDto,
        @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<RestaurantAdminResponseDto> responseDtos = restaurantAdminService.searchRestaurant(
            restaurantAdminSearchDto, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
    }

}
