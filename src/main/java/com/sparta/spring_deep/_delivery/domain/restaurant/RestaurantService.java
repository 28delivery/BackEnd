package com.sparta.spring_deep._delivery.domain.restaurant;

import static com.sparta.spring_deep._delivery.util.AuthTools.ownerCheck;

import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddress;
import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddressRepository;
import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.repository.UserRepository;
import com.sparta.spring_deep._delivery.exception.ResourceNotFoundException;
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
    private final RestaurantAddressRepository restaurantAddressRepository;
    private final UserRepository userRepository;

    // 음식점 조회
    public RestaurantResponseDto getRestaurant(UUID restaurantId) {
        log.info("음식점 조회");

        // id로 restaurant 객체 찾기
        Restaurant restaurant = restaurantRepository.findByIdAndIsDeletedFalse(restaurantId)
            .orElseThrow(ResourceNotFoundException::new);

        return new RestaurantResponseDto(restaurant);
    }

    // 음식점 수정
    @Transactional
    public RestaurantResponseDto updateRestaurant(UUID restaurantId,
        RestaurantRequestDto restaurantRequestDto,
        UUID restaurantAddressId,
        UserDetailsImpl userDetails) {
        log.info("음식점 수정");

        // 토큰으로 사용자 정보 찾기
        User loggedInUser = userDetails.getUser();

        // id로 Restaurant 객체 찾기
        Restaurant restaurant = restaurantRepository.findByIdAndIsDeletedFalse(restaurantId)
            .orElseThrow(ResourceNotFoundException::new);

        // 사용자가 수정을 요청한 가게가 본인 소유의 가게가 맞는지 검증
        ownerCheck(loggedInUser, restaurant.getOwner());

        // 음식점 주소 Id로 찾아내기
        RestaurantAddress restaurantAddress = restaurantAddressRepository.findByIdAndIsDeletedFalse(
            restaurantAddressId).orElseThrow(ResourceNotFoundException::new);

        restaurant.UpdateRestaurant(restaurantRequestDto, restaurantAddress,
            loggedInUser.getUsername());

        return new RestaurantResponseDto(restaurant);
    }

    // 음식점 삭제
    @Transactional
    public RestaurantResponseDto deleteRestaurant(UUID restaurantId, UserDetailsImpl userDetails) {
        log.info("음식점 삭제");

        // 토큰으로 사용자 정보 찾기
        User loggedInUser = userDetails.getUser();

        // id로 가게 조회
        Restaurant restaurant = restaurantRepository.findByIdAndIsDeletedFalse(restaurantId)
            .orElseThrow(ResourceNotFoundException::new);

        // 사용자가 수정을 요청한 가게가 본인 소유의 가게가 맞는지 검증
        log.info("delete restaurant : " + restaurantId);
        ownerCheck(loggedInUser, restaurant.getOwner());

        // soft delete 수행
        restaurant.delete(loggedInUser.getUsername());

        return new RestaurantResponseDto(restaurant);
    }

//    // 음식점 검색
//    public Page<Restaurant> searchRestaurant(UUID id, String restaurantName,
//        String categoryName, boolean isAsc, String sortBy) {
//        log.info("음식점 검색");
//
//        // Category를 이름으로 찾기
//        log.info("getCategory by name: {}", categoryName);
//        Category category = categoryRepository.findByNameAndIsDeletedFalse(categoryName)
//            .orElseThrow(
//                () -> new ResourceNotFoundException("존재하지 않는 카테고리 입니다.")
//            );
//
//        // Pageable 객체와 함께 검색
//        log.info("search restaurant by values: {}, {}, {}, {}, {}", id, restaurantName, category,
//            isAsc, sortBy);
//        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
//        Sort sort = Sort.by(direction, sortBy);
//        Pageable pageable = PageRequest.of(0, 10, sort);
//
//        return restaurantRepository.searchByOptionAndIsDeletedFalse(pageable, id, restaurantName,
//            category);
//    }
}
