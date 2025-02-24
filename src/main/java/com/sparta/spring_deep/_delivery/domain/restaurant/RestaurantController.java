package com.sparta.spring_deep._delivery.domain.restaurant;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j(topic = "Restaurant Controller")
public class RestaurantController {

    private final RestaurantManageService restaurantManageService;

    // 음식점 수정
    @PutMapping("/restaurants/{restaurantId}")
    public ResponseEntity<RestaurantResponseDto> updateRestaurant(
        @RequestBody RestaurantRequestDto restaurantRequestDto,
        @PathVariable UUID restaurantId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("음식점 수정");

        RestaurantResponseDto response = restaurantManageService.updateRestaurant(
            restaurantId, restaurantRequestDto, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 음식점 삭제
    @DeleteMapping("/restaurants/{restaurantId}")
    public ResponseEntity<RestaurantResponseDto> deleteRestaurant(
        @PathVariable UUID restaurantId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("음식점 삭제");

        RestaurantResponseDto response = restaurantManageService.deleteRestaurant(restaurantId,
            userDetails);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    // 음식점 조회
    @GetMapping(value = "/restaurants/{restaurantId}")
    public ResponseEntity<RestaurantResponseDto> getRestaurant(@PathVariable UUID restaurantId) {
        log.info("음식점 조회");

        RestaurantResponseDto response = restaurantManageService.getRestaurant(restaurantId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 음식점 검색
    @GetMapping("/restaurants/search")
    public ResponseEntity<Page<RestaurantResponseDto>> searchRestaurant(
        @ModelAttribute RestaurantSearchDto searchDto,
        @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("음식점 검색");

        Page<RestaurantResponseDto> responses = restaurantManageService.searchRestaurant(
            searchDto, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

}
