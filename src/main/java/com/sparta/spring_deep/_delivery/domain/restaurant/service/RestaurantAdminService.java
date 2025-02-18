package com.sparta.spring_deep._delivery.domain.restaurant.service;

import com.sparta.spring_deep._delivery.domain.category.Category;
import com.sparta.spring_deep._delivery.domain.category.CategoryRepository;
import com.sparta.spring_deep._delivery.domain.restaurant.dto.RestaurantAdminCreateRequestDto;
import com.sparta.spring_deep._delivery.domain.restaurant.dto.RestaurantAdminRequestDto;
import com.sparta.spring_deep._delivery.domain.restaurant.dto.RestaurantAdminResponseDto;
import com.sparta.spring_deep._delivery.domain.restaurant.entity.Restaurant;
import com.sparta.spring_deep._delivery.domain.restaurant.repository.RestaurantRepository;
import com.sparta.spring_deep._delivery.domain.user.User;
import com.sparta.spring_deep._delivery.domain.user.UserRepository;
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
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    // 음식점 상세 조회
    public RestaurantAdminResponseDto getRestaurant(UUID restaurantId) {
        // id로 restaurant 객체 찾기
        log.info("get restaurant with id" + restaurantId);
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
            () -> new EntityNotFoundException("Restaurant with id " + restaurantId + " not found")
        );

        return new RestaurantAdminResponseDto(restaurant);
    }

    // TODO: 인증 정보로 사용자 정보를 가져와야 함
    // 음식점 생성
    @Transactional
    public RestaurantAdminResponseDto createRestaurant(
        RestaurantAdminCreateRequestDto restaurantAdminCreateRequestDto) {

        // 인증 정보로 사용자 정보 가져오기
        // 임시방편
        String userId = "admin";
        User createUser = userRepository.findById(userId).orElseThrow(
            () -> new EntityNotFoundException("User with id " + userId + " not found")
        );
        // 임시방편

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

        // 사용자 정보와 category로 Restaurant 객체 생성
        log.info("add restaurant " + restaurantAdminCreateRequestDto);
        Restaurant restaurant = new Restaurant(restaurantAdminCreateRequestDto, owner, category,
            createUser);

        // 생성된 Restaurant 객체 저장
        log.info("save restaurant " + restaurantAdminCreateRequestDto);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        return new RestaurantAdminResponseDto(savedRestaurant);
    }

    // TODO: 인증 정보로 사용자 정보를 가져와야 함
    // 음식점 정보 수정
    @Transactional
    public RestaurantAdminResponseDto updateRestaurant(UUID restaurantId,
        RestaurantAdminRequestDto restaurantAdminRequestDto) {

        // id로 Restaurant 객체 찾기
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
            () -> new EntityNotFoundException("Restaurant with id " + restaurantId + " not found")
        );

        // 인증 정보로 사용자 정보 가져오기
        // 임시방편
        String userId = "admin";
        User createUser = userRepository.findById(userId).orElseThrow(
            () -> new EntityNotFoundException("User with id " + userId + " not found")
        );
        // 임시방편

        // Id로 카테고리 찾아내기
        UUID uuid = restaurantAdminRequestDto.getCategoryId();
        log.info("find Category by Id " + uuid);
        Category category = categoryRepository.findById(uuid).orElseThrow(
            () -> new EntityNotFoundException("Category with id " + uuid + " not found")
        );

        restaurant.UpdateRestaurant(restaurantAdminRequestDto, category, createUser);

        return new RestaurantAdminResponseDto(restaurant);
    }

    // TODO: 인증 정보로 사용자 정보를 가져와야 함
    @Transactional
    public boolean deleteRestaurant(UUID restaurantId) {

        // id로 가게 조회
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
            () -> new EntityNotFoundException("Restaurant with id " + restaurantId + " not found")
        );

        // 사용자 정보 조회
        // 임시방편
        User deleteUser = userRepository.findById("admin").orElseThrow(
            () -> new EntityNotFoundException("User with id " + restaurantId + " not found")
        );

        // soft delete 수행
        restaurant.delete(deleteUser);

        return true;
    }
}
