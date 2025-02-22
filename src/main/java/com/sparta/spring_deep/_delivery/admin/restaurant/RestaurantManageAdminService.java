package com.sparta.spring_deep._delivery.admin.restaurant;

import com.sparta.spring_deep._delivery.admin.restaurant.restaurantAddress.RestaurantAddressAdminRequestDto;
import com.sparta.spring_deep._delivery.admin.restaurant.restaurantAddress.RestaurantAddressAdminResponseDto;
import com.sparta.spring_deep._delivery.admin.restaurant.restaurantAddress.RestaurantAddressAdminService;
import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Restaurant Manage Admin Service")
public class RestaurantManageAdminService {

    private final RestaurantAdminService restaurantAdminService;
    private final RestaurantAddressAdminService restaurantAddressAdminService;

    // 음식점 조회
    public RestaurantAdminResponseDto getRestaurant(UUID restaurantId) {
        return restaurantAdminService.getRestaurant(restaurantId);
    }

    // 음식점 생성
    @Transactional
    public RestaurantAdminResponseDto createRestaurant(
        RestaurantAdminCreateRequestDto restaurantAdminCreateRequestDto,
        UserDetailsImpl userDetails) {
        // Restaurant Address Service에서 Restaurant Address 생성 후 Restaurant Service로 접근
        RestaurantAddressAdminRequestDto restaurantAddressAdminRequestDto = new RestaurantAddressAdminRequestDto(
            restaurantAdminCreateRequestDto.getRoadAddr(),
            restaurantAdminCreateRequestDto.getDetailAddr()
        );
        RestaurantAddressAdminResponseDto restaurantAddressAdminResponseDto = restaurantAddressAdminService.create(
            restaurantAddressAdminRequestDto, userDetails);

        UUID restaurantAddressId = restaurantAddressAdminResponseDto.getId();

        // 생성된 restaurantAddress로 가게 생성 시작
        return restaurantAdminService.createRestaurant(restaurantAdminCreateRequestDto,
            restaurantAddressId, userDetails);
    }

    // 음식점 수정
    @Transactional
    public RestaurantAdminResponseDto updateRestaurant(UUID restaurantId,
        RestaurantAdminRequestDto restaurantAdminRequestDto, UserDetailsImpl userDetails) {
        log.info("음식점 수정");

        RestaurantAddressAdminResponseDto restaurantAddressAdminResponseDto;

        // Restaurant Address Service에서 도로명과 상세주소로 Restaurant Address 찾기
        try {
            restaurantAddressAdminResponseDto = restaurantAddressAdminService.findByRoadAddrAndDetailAddr(
                restaurantAdminRequestDto.getRoadAddr(), restaurantAdminRequestDto.getDetailAddr()
            );
        } catch (Exception e) {
            // Restaurant Address Service에서 Restaurant Address 생성 후 Restaurant Service로 접근
            RestaurantAddressAdminRequestDto restaurantAddressAdminRequestDto = new RestaurantAddressAdminRequestDto(
                restaurantAdminRequestDto.getRoadAddr(), restaurantAdminRequestDto.getDetailAddr()
            );
            restaurantAddressAdminResponseDto = restaurantAddressAdminService.create(
                restaurantAddressAdminRequestDto, userDetails);

        }
        UUID restaurantAddressId = restaurantAddressAdminResponseDto.getId();

        // 생성된 restaurantAddress로 가게 생성 시작
        return restaurantAdminService.updateRestaurant(restaurantId, restaurantAdminRequestDto,
            restaurantAddressId, userDetails);

    }

    // 음식점 삭제
    @Transactional
    public void deleteRestaurant(UUID restaurantId,
        UserDetailsImpl userDetails) {
        restaurantAdminService.deleteRestaurant(restaurantId, userDetails);
    }
}
