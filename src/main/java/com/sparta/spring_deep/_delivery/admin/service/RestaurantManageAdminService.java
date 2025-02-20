package com.sparta.spring_deep._delivery.admin.service;

import com.sparta.spring_deep._delivery.admin.dto.RestaurantAddressAdminRequestDto;
import com.sparta.spring_deep._delivery.admin.dto.RestaurantAddressAdminResponseDto;
import com.sparta.spring_deep._delivery.admin.dto.RestaurantAdminCreateRequestDto;
import com.sparta.spring_deep._delivery.admin.dto.RestaurantAdminRequestDto;
import com.sparta.spring_deep._delivery.admin.dto.RestaurantAdminResponseDto;
import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Restaurant Manage Admin Service")
public class RestaurantManageAdminService {

    private final RestaurantAdminService restaurantAdminService;
    private final RestaurantAddressAdminService restaurantAddressAdminService;

    // 음식점 조회
    public RestaurantAdminResponseDto getRestaurant(UUID restaurantId,
        UserDetailsImpl userDetails) {
        return restaurantAdminService.getRestaurant(restaurantId, userDetails);
    }

    // 음식점 생성
    public RestaurantAdminResponseDto createRestaurant(
        RestaurantAdminCreateRequestDto restaurantAdminCreateRequestDto,
        UserDetailsImpl userDetails) {
        // Restaurant Address Service에서 Restaurant Address 생성 후 Restaurant Service로 접근
        RestaurantAddressAdminRequestDto restaurantAddressAdminRequestDto = new RestaurantAddressAdminRequestDto(
            restaurantAdminCreateRequestDto.getRoadAddr(),
            restaurantAdminCreateRequestDto.getDetailAddr()
        );
        RestaurantAddressAdminResponseDto restaurantAddressAdminResponseDto = restaurantAddressAdminService.create(
            restaurantAddressAdminRequestDto);

        UUID restaurantAddressId = restaurantAddressAdminResponseDto.getId();

        // 생성된 restaurantAddress로 가게 생성 시작
        return restaurantAdminService.createRestaurant(restaurantAdminCreateRequestDto,
            restaurantAddressId, userDetails);
    }

    // 음식점 수정
    public RestaurantAdminResponseDto updateRestaurant(UUID restaurantId,
        RestaurantAdminRequestDto restaurantAdminRequestDto, UserDetailsImpl userDetails) {

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
                restaurantAddressAdminRequestDto);

        }
        UUID restaurantAddressId = restaurantAddressAdminResponseDto.getId();

        // 생성된 restaurantAddress로 가게 생성 시작
        return restaurantAdminService.updateRestaurant(restaurantId, restaurantAdminRequestDto,
            restaurantAddressId, userDetails);

    }

    // 음식점 삭제
    public RestaurantAdminResponseDto deleteRestaurant(UUID restaurantId,
        UserDetailsImpl userDetails) {
        return restaurantAdminService.deleteRestaurant(restaurantId, userDetails);
    }
}
