package com.sparta.spring_deep._delivery.admin.restaurant;

import com.sparta.spring_deep._delivery.domain.restaurant.Restaurant;
import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddress;
import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddressRepository;
import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.repository.UserRepository;
import com.sparta.spring_deep._delivery.exception.ResourceNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "RestaurantAdminService")
public class RestaurantAdminService {

    private final RestaurantAdminRepository restaurantAdminRepository;
    private final RestaurantAddressRepository restaurantAddressRepository;
    private final UserRepository userRepository;

    // 음식점 상세 조회
    public RestaurantAdminResponseDto getRestaurant(UUID restaurantId) {
        log.info("음식점 상세 조회");

        // id로 restaurant 객체 찾기
        Restaurant restaurant = restaurantAdminRepository.findById(restaurantId).orElseThrow(
            ResourceNotFoundException::new
        );

        return new RestaurantAdminResponseDto(restaurant);
    }

    // 음식점 생성
    @Transactional
    public RestaurantAdminResponseDto createRestaurant(
        RestaurantAdminCreateRequestDto restaurantAdminCreateRequestDto,
        UUID restaurantAddressId,
        UserDetailsImpl userDetails) {
        log.info("음식점 생성");

        // 인증 정보로 사용자 정보 가져오기
        User loggenInUser = userDetails.getUser();

        // Id로 사용자 정보 찾기
        log.info("사용자 정보 찾기");
        String ownerId = restaurantAdminCreateRequestDto.getOwnerId();
        User owner = userRepository.findByUsernameAndIsDeletedFalse(ownerId).orElseThrow(
            ResourceNotFoundException::new
        );

        // Id로 음식점 주소 찾아내기
        log.info("음식점 주소 찾기");
        RestaurantAddress restaurantAddress = restaurantAddressRepository.findByIdAndIsDeletedFalse(
            restaurantAddressId).orElseThrow(
            ResourceNotFoundException::new
        );

        // 사용자 정보와 category로 Restaurant 객체 생성
        Restaurant restaurant = new Restaurant(restaurantAdminCreateRequestDto, owner,
            restaurantAddress, loggenInUser.getUsername());

        // 생성된 Restaurant 객체 저장
        Restaurant savedRestaurant = restaurantAdminRepository.save(restaurant);

        return new RestaurantAdminResponseDto(savedRestaurant);
    }

    // 음식점 정보 수정
    @Transactional
    public RestaurantAdminResponseDto updateRestaurant(UUID restaurantId,
        RestaurantAdminRequestDto restaurantAdminRequestDto, UUID restaurantAddressId,
        UserDetailsImpl userDetails) {
        log.info("음식점 정보 수정");

        // 인증 정보로 사용자 정보 가져오기
        User loggenInUser = userDetails.getUser();

        // id로 Restaurant 객체 찾기
        log.info("음식점 객체 찾기");
        Restaurant restaurant = restaurantAdminRepository.findByIdAndIsDeletedFalse(restaurantId)
            .orElseThrow(
                ResourceNotFoundException::new
            );

        // Address 값으로 address 찾아내기
        log.info("음식점 가게 찾기");
        RestaurantAddress restaurantAddress = restaurantAddressRepository.findByIdAndIsDeletedFalse(
            restaurantAddressId).orElseThrow(
            ResourceNotFoundException::new
        );

        restaurant.UpdateRestaurant(restaurantAdminRequestDto, restaurantAddress,
            loggenInUser.getUsername());

        return new RestaurantAdminResponseDto(restaurant);
    }

    // 음식점 삭제
    @Transactional
    public void deleteRestaurant(UUID restaurantId,
        UserDetailsImpl userDetails) {
        log.info("음식점 삭제");

        // 사용자 정보 조회
        User loggedInUser = userDetails.getUser();

        // id로 가게 조회
        log.info("음식점 조회");
        Restaurant restaurant = restaurantAdminRepository.findById(restaurantId).orElseThrow(
            ResourceNotFoundException::new
        );

        // soft delete 수행
        restaurant.delete(loggedInUser.getUsername());

    }

    public Page<RestaurantAdminResponseDto> searchRestaurant(
        RestaurantAdminSearchDto restaurantAdminSearchDto, Pageable pageable) {
        log.info("음식점 검색");

        Page<RestaurantAdminResponseDto> responseDtos = restaurantAdminRepository.searchByOption(
            restaurantAdminSearchDto, pageable);

        // 검색 결과가 비어있으면 Exception 출력
        if (responseDtos.isEmpty()) {
            throw new ResourceNotFoundException();
        }

        return null;
    }
}
