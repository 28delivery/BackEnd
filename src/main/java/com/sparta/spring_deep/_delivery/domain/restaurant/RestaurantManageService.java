package com.sparta.spring_deep._delivery.domain.restaurant;

import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddressCreateRequestDto;
import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddressResponseDto;
import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddressService;
import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Restaurant Manage Service")
public class RestaurantManageService {

    private final RestaurantService restaurantService;
    private final RestaurantAddressService restaurantAddressService;

    public RestaurantResponseDto getRestaurant(UUID restaurantId) {
        return restaurantService.getRestaurant(restaurantId);
    }

    public RestaurantResponseDto updateRestaurant(UUID restaurantId,
        RestaurantRequestDto restaurantRequestDto, UserDetailsImpl userDetails) {
        RestaurantAddressResponseDto restaurantAddressResponseDto;
        // Restaurant Address Service에서 도로명과 상세주소로 Restaurant Address 찾기
        try {
            restaurantAddressResponseDto = restaurantAddressService.findByRoadAddrAndDetailAddr(
                restaurantRequestDto.getRoadAddress(), restaurantRequestDto.getDetailAddress()
            );
        } catch (Exception e) {
            // 만약 없다면,
            // Restaurant Address Service에서 Restaurant Address 생성 후 Restaurant Service로 접근
            RestaurantAddressCreateRequestDto restaurantAddressCreateRequestDto = new RestaurantAddressCreateRequestDto(
                restaurantRequestDto.getRoadAddress(), restaurantRequestDto.getDetailAddress()
            );
            restaurantAddressResponseDto = restaurantAddressService.create(
                restaurantAddressCreateRequestDto);
        }

        UUID restaurantAddressId = restaurantAddressResponseDto.getId();

        // 생성된 restaurantAddress로 수정 시작
        return restaurantService.updateRestaurant(restaurantId, restaurantRequestDto,
            restaurantAddressId, userDetails);
    }

    public RestaurantResponseDto deleteRestaurant(UUID restaurantId, UserDetailsImpl userDetails) {
        return restaurantService.deleteRestaurant(restaurantId, userDetails);
    }

    public Page<Restaurant> searchRestaurant(UUID id, String restaurantName, String categoryName,
        boolean isAsc, String sortBy) {
        return restaurantService.searchRestaurant(id, restaurantName, categoryName, isAsc, sortBy);
    }
}
