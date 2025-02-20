package com.sparta.spring_deep._delivery.domain.restaurant;

import com.sparta.spring_deep._delivery.domain.category.Category;
import com.sparta.spring_deep._delivery.domain.category.CategoryRepository;
import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddress;
import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddressRepository;
import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Restaurant Service")
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantAddressRepository restaurantAddressRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    // 음식점 조회
    public RestaurantResponseDto getRestaurant(UUID restaurantId) {

        // id로 restaurant 객체 찾기
        log.info("get restaurant with id" + restaurantId);
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
            () -> new EntityNotFoundException("Restaurant with id " + restaurantId + " not found")
        );

        return new RestaurantResponseDto(restaurant);
    }

    // 음식점 수정
    @Transactional
    public RestaurantResponseDto updateRestaurant(UUID restaurantId,
        RestaurantRequestDto restaurantRequestDto,
        UUID restaurantAddressId,
        UserDetailsImpl userDetails) {
        // 토큰으로 사용자 정보 찾기
        User loggedInUser = userDetails.getUser();

        // id로 Restaurant 객체 찾기
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
            () -> new EntityNotFoundException("Restaurant with id " + restaurantId + " not found")
        );

        // 사용자가 수정을 요청한 가게가 본인 소유의 가게가 맞는지 검증
        log.info("update restaurant " + restaurantRequestDto);
        if (!loggedInUser.getUsername().equals(restaurant.getOwner().getUsername())) {
            throw new IllegalArgumentException(
                "Restaurant owner id is not matched with user's id" + loggedInUser.getUsername());
        }

        // Id로 카테고리 찾아내기
        UUID uuid = restaurantRequestDto.getCategoryId();
        log.info("find Category by Id " + uuid);
        Category category = categoryRepository.findById(uuid).orElseThrow(
            () -> new EntityNotFoundException("Category with id " + uuid + " not found")
        );

        // 음식점 주소 Id로 찾아내기
        RestaurantAddress restaurantAddress = restaurantAddressRepository.findById(
            restaurantAddressId).orElseThrow(
            () -> new EntityNotFoundException(
                "Restaurant address with id " + restaurantAddressId + " not found")
        );

        restaurant.UpdateRestaurant(restaurantRequestDto, category, restaurantAddress,
            loggedInUser.getUsername());

        return new RestaurantResponseDto(restaurant);
    }

    // 음식점 삭제
    @Transactional
    public RestaurantResponseDto deleteRestaurant(UUID restaurantId, UserDetailsImpl userDetails) {
        // 토큰으로 사용자 정보 찾기
        User loggedInUser = userDetails.getUser();

        // id로 가게 조회
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
            () -> new EntityNotFoundException("Restaurant with id " + restaurantId + " not found")
        );

        // 사용자가 수정을 요청한 가게가 본인 소유의 가게가 맞는지 검증
        log.info("delete restaurant " + restaurantId);
        if (!loggedInUser.getUsername().equals(restaurant.getOwner().getUsername())) {
            throw new IllegalArgumentException(
                "Restaurant owner id is not matched with user's id" + loggedInUser.getUsername());
        }

        // soft delete 수행
        restaurant.delete(loggedInUser.getUsername());

        return new RestaurantResponseDto(restaurant);
    }

    // 음식점 검색
    public Page<Restaurant> searchRestaurant(UUID id, String restaurantName,
        String categoryName, boolean isAsc, String sortBy) {

        // Category를 이름으로 찾기
        log.info("getCategory by name: {}", categoryName);
        Category category = categoryRepository.findByName(categoryName).orElse(null);

        // Pageable 객체와 함께 검색
        log.info("search restaurant by values: {}, {}, {}, {}, {}", id, restaurantName, category,
            isAsc, sortBy);
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(0, 10, sort);

        return restaurantRepository.searchByOption(pageable, id, restaurantName, category);
    }
}
