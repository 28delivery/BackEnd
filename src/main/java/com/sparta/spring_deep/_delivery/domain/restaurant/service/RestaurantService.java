package com.sparta.spring_deep._delivery.domain.restaurant.service;

import com.sparta.spring_deep._delivery.domain.category.Category;
import com.sparta.spring_deep._delivery.domain.category.CategoryRepository;
import com.sparta.spring_deep._delivery.domain.restaurant.dto.RestaurantRequestDto;
import com.sparta.spring_deep._delivery.domain.restaurant.dto.RestaurantResponseDto;
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
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public RestaurantResponseDto getRestaurant(UUID restaurantId) {
        // id로 restaurant 객체 찾기
        log.info("get restaurant with id" + restaurantId);
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
            () -> new EntityNotFoundException("Restaurant with id " + restaurantId + " not found")
        );

        return new RestaurantResponseDto(restaurant);
    }


    @Transactional
    public RestaurantResponseDto addRestaurant(String ownerId,
        RestaurantRequestDto restaurantRequestDto) {
        // 토큰으로 현재 접속자 정보 찾기
        // 임시방편
        User user = userRepository.findById("admin").orElseThrow(
            () -> new EntityNotFoundException("User with id " + "test" + " not found")
        );

        // Id로 사용자 정보 찾기
        log.info("find User by Id " + ownerId);
        User owner = userRepository.findById(ownerId).orElseThrow(
            () -> new EntityNotFoundException("User with id " + ownerId + " not found")
        );

        // Id로 카테고리 찾아내기
        UUID uuid = restaurantRequestDto.getCategoryId();
        log.info("find Category by Id " + uuid);
        Category category = categoryRepository.findById(uuid).orElseThrow(
            () -> new EntityNotFoundException("Category with id " + uuid + " not found")
        );

        // 사용자 정보와 category로 Restaurant 객체 생성
        log.info("add restaurant " + restaurantRequestDto);
        Restaurant restaurant = new Restaurant(restaurantRequestDto, owner, category, user);

        // 생성된 Restaurant 객체 저장
        log.info("save restaurant " + restaurantRequestDto);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        return new RestaurantResponseDto(savedRestaurant);
    }

    @Transactional
    public RestaurantResponseDto updateRestaurant(UUID restaurantId,
        RestaurantRequestDto restaurantRequestDto) {
        // id로 Restaurant 객체 찾기
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
            () -> new EntityNotFoundException("Restaurant with id " + restaurantId + " not found")
        );

        // 사용자 정보 조회
        // 임시방편
        User user = userRepository.findById("admin").orElseThrow(
            () -> new EntityNotFoundException("User with id " + restaurantId + " not found")
        );
        // 임시방편

        // 토큰으로 사용자 정보 찾기
        // ----추가하기----

        // 사용자가 수정을 요청한 가게가 본인 소유의 가게가 맞는지 검증
        log.info("update restaurant " + restaurantRequestDto);
        if (!user.getUsername().equals(restaurant.getOwner().getUsername())) {
            throw new IllegalArgumentException(
                "Restaurant owner id is not matched with user's id" + user.getUsername());
        } else {
            // Id로 카테고리 찾아내기
            UUID uuid = restaurantRequestDto.getCategoryId();
            log.info("find Category by Id " + uuid);
            Category category = categoryRepository.findById(uuid).orElseThrow(
                () -> new EntityNotFoundException("Category with id " + uuid + " not found")
            );

            restaurant.UpdateRestaurant(restaurantRequestDto, category, user);
        }

        return new RestaurantResponseDto(restaurant);
    }

    @Transactional
    public boolean deleteRestaurant(UUID restaurantId) {

        // id로 가게 조회
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
            () -> new EntityNotFoundException("Restaurant with id " + restaurantId + " not found")
        );
        // 사용자 정보 조회
        // 임시방편
        User owner = userRepository.findById("admin").orElseThrow(
            () -> new EntityNotFoundException("User with id " + restaurantId + " not found")
        );

        // 토큰으로 사용자 정보 찾기
        // ----추가하기----

        // 사용자가 수정을 요청한 가게가 본인 소유의 가게가 맞는지 검증
        log.info("delete restaurant " + restaurantId);
        if (!owner.getUsername().equals(restaurant.getOwner().getUsername())) {
            throw new IllegalArgumentException(
                "Restaurant owner id is not matched with user's id" + owner.getUsername());
        } else {
            // soft delete 수행
            restaurant.delete(owner);
        }

        return true;
    }
}
