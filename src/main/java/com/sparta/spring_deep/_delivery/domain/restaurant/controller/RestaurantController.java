package com.sparta.spring_deep._delivery.domain.restaurant.controller;

import com.sparta.spring_deep._delivery.domain.restaurant.dto.RestaurantRequestDto;
import com.sparta.spring_deep._delivery.domain.restaurant.dto.RestaurantResponseDto;
import com.sparta.spring_deep._delivery.domain.restaurant.service.RestaurantService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/restaurants")
@Slf4j(topic = "Restaurant Controller")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping(value = "/{restaurantId}", produces = "application/json")
    public RestaurantResponseDto getRestaurant(@PathVariable UUID restaurantId) {
        log.info("getRestaurant: {}", restaurantId);
        return restaurantService.getRestaurant(restaurantId);
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

    // ADMIN만 가게를 생성할 수 있다고 했는데, 그럼 가게 주인의 정보를 받아와야 함
    // 유저의 id를 request param으로 받고, 가게 정보를 body로 받는 것이 좋아보임
    //    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{ownerId}")
    public RestaurantResponseDto addRestaurant(
        @PathVariable String ownerId,
        @RequestBody RestaurantRequestDto restaurantRequestDto) {
        log.info("addRestaurant with owner id {} : {} ", ownerId, restaurantRequestDto);
        return restaurantService.addRestaurant(ownerId, restaurantRequestDto);
    }

    //    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{restaurantId}")
    public RestaurantResponseDto updateRestaurant(
        @RequestBody RestaurantRequestDto restaurantRequestDto,
        @PathVariable UUID restaurantId) {
        log.info("updateRestaurant: {}, {}", restaurantId, restaurantRequestDto);
        return restaurantService.updateRestaurant(restaurantId, restaurantRequestDto);
    }

    //    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<String> deleteRestaurant(@PathVariable UUID restaurantId) {
        log.info("deleteRestaurant: {}", restaurantId);

        if (restaurantService.deleteRestaurant(restaurantId)) {
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body("Restaurant deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Restaurant not found");
        }
    }

}
