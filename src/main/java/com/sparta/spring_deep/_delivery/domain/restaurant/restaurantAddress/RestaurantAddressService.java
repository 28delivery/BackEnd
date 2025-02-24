package com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress;

import static com.sparta.spring_deep._delivery.util.RestaurantAddressTools.searchAddress;
import static com.sparta.spring_deep._delivery.util.RestaurantAddressTools.validateTotalCount;

import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.exception.ResourceNotFoundException;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "RestaurantAddress Service")
public class RestaurantAddressService {

    private final RestaurantAddressRepository restaurantAddressRepository;

    // 가게 주소 생성
    @Transactional
    public RestaurantAddressResponseDto create(RestaurantAddressCreateRequestDto dto,
        UserDetailsImpl userDetails) {
        // 토큰으로부터 유저 확인
        User user = userDetails.getUser();

        // 주소 검색
        Map<String, Object> searchResultJson = searchAddress(dto.getRoadAddr());

        // 주소 검색 결과 유효성 검사 후 Juso 부분 반환
        Map<String, Object> resultsJuso = validateTotalCount(searchResultJson);

        // 주소 검색 결과로 가게 주소 객체 생성
        RestaurantAddress restaurantAddress = new RestaurantAddress(resultsJuso,
            dto.getDetailAddr(), user.getUsername());

        // DB에 가게 주소 객체 저장
        RestaurantAddress savedRestaurantAddress = restaurantAddressRepository.save(
            restaurantAddress);

        return new RestaurantAddressResponseDto(savedRestaurantAddress);
    }

    // 가게 주소 업데이트
    @Transactional
    public RestaurantAddressResponseDto update(UUID id, RestaurantAddressCreateRequestDto dto,
        UserDetailsImpl userDetails) {
        // 토큰으로부터 유저 확인
        User user = userDetails.getUser();

        // id로 가게주소 검색
        RestaurantAddress restaurantAddress = restaurantAddressRepository.findByIdAndIsDeletedFalse(
            id).orElseThrow(ResourceNotFoundException::new);

        // 주소 검색
        Map<String, Object> searchResultJson = searchAddress(dto.getRoadAddr());

        // 주소 검색 결과 유효성 검사 후 Juso 부분 반환
        Map<String, Object> resultsJuso = validateTotalCount(searchResultJson);

        // restaurantAddress 객체 업데이트
        restaurantAddress.update(resultsJuso, dto.getDetailAddr(), user.getUsername());

        return new RestaurantAddressResponseDto(restaurantAddress);
    }

    // 특정 주소 조회
    @Transactional(readOnly = true)
    public RestaurantAddressResponseDto getById(UUID id) {
        // id로 가게 주소 검색
        RestaurantAddress restaurantAddress = restaurantAddressRepository.findByIdAndIsDeletedFalse(
            id).orElseThrow(ResourceNotFoundException::new);

        return new RestaurantAddressResponseDto(restaurantAddress);
    }

    // 가게 주소 삭제 - 소프트 삭제
    @Transactional
    public RestaurantAddressResponseDto delete(UUID id, UserDetailsImpl userDetails) {
        // 토큰으로부터 유저 확인
        User user = userDetails.getUser();

        // id로 가게주소 검색
        RestaurantAddress restaurantAddress = restaurantAddressRepository.findById(id)
            .orElseThrow(ResourceNotFoundException::new);

        // 소프트 삭제
        restaurantAddress.delete(user.getUsername());

        return new RestaurantAddressResponseDto(restaurantAddress);
    }

    public RestaurantAddressResponseDto findByRoadAddrAndDetailAddr(String roadAddr,
        String detailAddr) {
        RestaurantAddress restaurantAddress = restaurantAddressRepository.findByRoadAddrAndDetailAddr(
            roadAddr, detailAddr).orElseThrow(
            ResourceNotFoundException::new
        );

        return new RestaurantAddressResponseDto(restaurantAddress);
    }
}
