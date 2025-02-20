package com.sparta.spring_deep._delivery.admin.service;

import com.sparta.spring_deep._delivery.admin.dto.RestaurantAdminCreateRequestDto;
import com.sparta.spring_deep._delivery.admin.dto.RestaurantAdminRequestDto;
import com.sparta.spring_deep._delivery.admin.dto.RestaurantAdminResponseDto;
import com.sparta.spring_deep._delivery.domain.category.Category;
import com.sparta.spring_deep._delivery.domain.category.CategoryRepository;
import com.sparta.spring_deep._delivery.domain.restaurant.Restaurant;
import com.sparta.spring_deep._delivery.domain.restaurant.RestaurantRepository;
import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddress;
import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddressRepository;
import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Restaurant Service")
public class RestaurantAdminService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantAddressRepository restaurantAddressRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    // 음식점 상세 조회
    public RestaurantAdminResponseDto getRestaurant(UUID restaurantId,
        UserDetailsImpl userDetails) {
        // 인증 정보로 사용자 정보 가져오기
        User loggenInUser = userDetails.getUser();

        // id로 restaurant 객체 찾기
        log.info("get restaurant with id" + restaurantId);
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
            () -> new EntityNotFoundException("Restaurant with id " + restaurantId + " not found")
        );

        return new RestaurantAdminResponseDto(restaurant);
    }

    // 음식점 생성
    @Transactional
    public RestaurantAdminResponseDto createRestaurant(
        RestaurantAdminCreateRequestDto restaurantAdminCreateRequestDto,
        UUID restaurantAddressId,
        UserDetailsImpl userDetails) {
        // 인증 정보로 사용자 정보 가져오기
        User loggenInUser = userDetails.getUser();

        // Id로 사용자 정보 찾기
        String ownerId = restaurantAdminCreateRequestDto.getOwnerId();
        log.info("find User by Id " + ownerId);
        User owner = userRepository.findById(ownerId).orElseThrow(
            () -> new EntityNotFoundException("User with id " + ownerId + " not found")
        );

        // Id로 카테고리 찾아내기
        UUID uuid = restaurantAdminCreateRequestDto.getCategoryId();
        log.info("find Category by Id " + uuid);
        Category category = categoryRepository.findById(uuid).orElseThrow(
            () -> new EntityNotFoundException("Category with id " + uuid + " not found")
        );

        // Id로 음식점 주소 찾아내기
        RestaurantAddress restaurantAddress = restaurantAddressRepository.findById(
            restaurantAddressId).orElseThrow(
            () -> new EntityNotFoundException(
                "Restaurant address with id " + restaurantAddressId + " not found")
        );

        // 사용자 정보와 category로 Restaurant 객체 생성
        log.info("add restaurant " + restaurantAdminCreateRequestDto);
        Restaurant restaurant = new Restaurant(restaurantAdminCreateRequestDto, owner, category,
            restaurantAddress, loggenInUser.getUsername());

        // 생성된 Restaurant 객체 저장
        log.info("save restaurant " + restaurantAdminCreateRequestDto);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        return new RestaurantAdminResponseDto(savedRestaurant);
    }

    // 음식점 정보 수정
    @Transactional
    public RestaurantAdminResponseDto updateRestaurant(UUID restaurantId,
        RestaurantAdminRequestDto restaurantAdminRequestDto, UUID restaurantAddressId,
        UserDetailsImpl userDetails) {

        // 인증 정보로 사용자 정보 가져오기
        User loggenInUser = userDetails.getUser();

        // id로 Restaurant 객체 찾기
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
            () -> new EntityNotFoundException("Restaurant with id " + restaurantId + " not found")
        );

        // Id로 카테고리 찾아내기
        UUID uuid = restaurantAdminRequestDto.getCategoryId();
        log.info("find Category by Id " + uuid);
        Category category = categoryRepository.findById(uuid).orElseThrow(
            () -> new EntityNotFoundException("Category with id " + uuid + " not found")
        );

        // Address 값으로 address 찾아내기
        RestaurantAddress restaurantAddress = restaurantAddressRepository.findById(
            restaurantAddressId).orElseThrow(
            () -> new EntityNotFoundException(
                "Restaurant address with id " + restaurantAddressId + " not found")
        );

        restaurant.UpdateRestaurant(restaurantAdminRequestDto, category, restaurantAddress,
            loggenInUser.getUsername());

        return new RestaurantAdminResponseDto(restaurant);
    }

    @Transactional
    public RestaurantAdminResponseDto deleteRestaurant(UUID restaurantId,
        UserDetailsImpl userDetails) {
        // 사용자 정보 조회
        User loggedInUser = userDetails.getUser();

        // id로 가게 조회
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
            () -> new EntityNotFoundException("Restaurant with id " + restaurantId + " not found")
        );

        // soft delete 수행
        restaurant.delete(loggedInUser.getUsername());

        return new RestaurantAdminResponseDto(restaurant);
    }

}
