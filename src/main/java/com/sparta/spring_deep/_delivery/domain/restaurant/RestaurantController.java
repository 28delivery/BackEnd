package com.sparta.spring_deep._delivery.domain.restaurant;

import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restaurants")
@Slf4j(topic = "Restaurant Controller")
public class RestaurantController {

    private final RestaurantManageService restaurantManageService;

    // 음식점 조회
    @GetMapping(value = "/{restaurantId}", produces = "application/json")
    public ResponseEntity<RestaurantResponseDto> getRestaurant(@PathVariable UUID restaurantId) {
        log.info("getRestaurant: {}", restaurantId);
        RestaurantResponseDto response = restaurantManageService.getRestaurant(restaurantId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 음식점 수정
    @PutMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponseDto> updateRestaurant(
        @RequestBody RestaurantRequestDto restaurantRequestDto,
        @PathVariable UUID restaurantId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        log.info("updateRestaurant: {}, {}", restaurantId, restaurantRequestDto);

        RestaurantResponseDto response = restaurantManageService.updateRestaurant(
            restaurantId, restaurantRequestDto, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 음식점 삭제
    //    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponseDto> deleteRestaurant(
        @PathVariable UUID restaurantId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("deleteRestaurant: {}", restaurantId);

        RestaurantResponseDto response = restaurantManageService.deleteRestaurant(restaurantId,
            userDetails);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    // 음식점 검색
    @GetMapping("/search")
    public ResponseEntity<Page<Restaurant>> getRestaurant(
        @RequestParam(required = false) UUID id,
        @RequestParam(required = false, defaultValue = "null") String restaurantName,
        @RequestParam(required = false, defaultValue = "null") String categoryName,
        @RequestParam(required = false, defaultValue = "true") boolean isAsc,
        @RequestParam(required = false, defaultValue = "updatedAt") String sortBy) {

        log.info("searchRestaurant by values: {}, {}, {}, {}, {}", id, restaurantName, categoryName,
            isAsc, sortBy);
        Page<Restaurant> responses = restaurantManageService.searchRestaurant(
            id, restaurantName, categoryName, isAsc, sortBy);

        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

}
