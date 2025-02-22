package com.sparta.spring_deep._delivery.domain.restaurant;

import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddressCreateRequestDto;
import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddressResponseDto;
import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddressService;
import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import com.sparta.spring_deep._delivery.exception.ResourceNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Restaurant Manage Service")
public class RestaurantManageService {

    private final RestaurantService restaurantService;
    private final RestaurantAddressService restaurantAddressService;

    // 음식점 조회
    public RestaurantResponseDto getRestaurant(UUID restaurantId) {
        log.info("음식점 조회");

        return restaurantService.getRestaurant(restaurantId);
    }

    // 음식점 수정
    public RestaurantResponseDto updateRestaurant(UUID restaurantId,
        RestaurantRequestDto restaurantRequestDto, UserDetailsImpl userDetails) {
        log.info("음식점 수정");

        RestaurantAddressResponseDto restaurantAddressResponseDto;

        // Restaurant Address Service에서 도로명과 상세주소로 Restaurant Address 찾기
        try {
            restaurantAddressResponseDto = restaurantAddressService.findByRoadAddrAndDetailAddr(
                restaurantRequestDto.getRoadAddr(), restaurantRequestDto.getDetailAddr()
            );
        } catch (ResourceNotFoundException e) {
            // 만약 없다면,
            // Restaurant Address Service에서 Restaurant Address 생성 후 Restaurant Service로 접근
            RestaurantAddressCreateRequestDto restaurantAddressCreateRequestDto = new RestaurantAddressCreateRequestDto(
                restaurantRequestDto.getRoadAddr(), restaurantRequestDto.getDetailAddr()
            );
            restaurantAddressResponseDto = restaurantAddressService.create(
                restaurantAddressCreateRequestDto, userDetails);
        }

        UUID restaurantAddressId = restaurantAddressResponseDto.getId();

        // 생성된 restaurantAddress로 수정 시작
        return restaurantService.updateRestaurant(restaurantId, restaurantRequestDto,
            restaurantAddressId, userDetails);
    }

    // 음식점 삭제
    public RestaurantResponseDto deleteRestaurant(UUID restaurantId, UserDetailsImpl userDetails) {
        return restaurantService.deleteRestaurant(restaurantId, userDetails);
    }

    // 음식점 검색
    public Page<Restaurant> searchRestaurant(RestaurantSearchDto restaurantSearchDto,
        Pageable pageable) {
        return restaurantService.searchRestaurant(restaurantSearchDto, pageable);
    }
}
