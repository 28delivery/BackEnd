package com.sparta.spring_deep._delivery.admin.restaurant.restaurantAddress;

import static com.sparta.spring_deep._delivery.util.RestaurantAddressTools.searchAddress;
import static com.sparta.spring_deep._delivery.util.RestaurantAddressTools.validateTotalCount;

import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddress;
import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddressRepository;
import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.exception.ResourceNotFoundException;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantAddressAdminService {

    private final RestaurantAddressAdminRepository repository;
    private final RestaurantAddressRepository restaurantAddressRepository;
    private final RestaurantAddressAdminRepository restaurantAddressAdminRepository;


    // 가게 주소 생성
    @Transactional
    public RestaurantAddressAdminResponseDto create(RestaurantAddressAdminRequestDto dto,
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
        RestaurantAddress savedRestaurantAddress = repository.save(restaurantAddress);

        return new RestaurantAddressAdminResponseDto(savedRestaurantAddress);
    }

    // 가게 주소 업데이트
    @Transactional
    public RestaurantAddressAdminResponseDto update(UUID id, RestaurantAddressAdminRequestDto dto,
        UserDetailsImpl userDetails) {
        // 토큰으로부터 유저 확인
        User user = userDetails.getUser();

        // id로 주소 객체 검색
        RestaurantAddress restaurantAddress = repository.findById(id)
            .orElseThrow(
                () -> new IllegalArgumentException("해당 Id와 일치하는 가게 주소가 존재하지 않습니다. : " + id));

        // 주소 검색
        Map<String, Object> searchResultJson = searchAddress(dto.getRoadAddr());

        // 주소 검색 결과 유효성 검사 후 Juso 부분 반환
        Map<String, Object> resultsJuso = validateTotalCount(searchResultJson);

        // restaurantAddress 객체 업데이트
        restaurantAddress.update(resultsJuso, dto.getDetailAddr(), user.getUsername());

        return new RestaurantAddressAdminResponseDto(restaurantAddress);
    }

    // 특정 주소 조회
    @Transactional(readOnly = true)
    public RestaurantAddressAdminResponseDto getById(UUID id) {

        // id로 가게 주소 검색
        RestaurantAddress restaurantAddress = repository.findById(id)
            .orElseThrow(
                () -> new IllegalArgumentException("해당 Id와 일치하는 가게 주소가 존재하지 않습니다. : " + id));

        return new RestaurantAddressAdminResponseDto(restaurantAddress);
    }

    // 가게 주소 삭제 - 소프트 삭제
    @Transactional
    public RestaurantAddressAdminResponseDto delete(UUID id, UserDetailsImpl userDetails) {
        // 토큰으로부터 유저 확인
        User user = userDetails.getUser();

        // id로 가게주소 검색
        RestaurantAddress restaurantAddress = repository.findById(id)
            .orElseThrow(ResourceNotFoundException::new);

        // 소프트 삭제
        restaurantAddress.delete(user.getUsername());

        return new RestaurantAddressAdminResponseDto(restaurantAddress);
    }

    // 전체 가게 주소 목록 조회 (페이징 적용)
    @Transactional(readOnly = true)
    public Page<RestaurantAddressAdminResponseDto> getAll(Pageable pageable) {
        return repository.findAll(pageable)
            .map(RestaurantAddressAdminResponseDto::new);
    }

    // 도로명 주소로 가게 주소 검색 (페이징 적용)
    @Transactional(readOnly = true)
    public Page<RestaurantAddressAdminResponseDto> searchByRoadAddr(String roadAddr,
        Pageable pageable) {
        return repository.findByRoadAddrContaining(roadAddr, pageable)
            .map(RestaurantAddressAdminResponseDto::new);
    }


    public RestaurantAddressAdminResponseDto findByRoadAddrAndDetailAddr(String roadAddr,
        String detailAddr) {
        RestaurantAddress restaurantAddress = restaurantAddressAdminRepository.findByRoadAddrAndDetailAddr(
            roadAddr, detailAddr).orElseThrow(
            ResourceNotFoundException::new
        );

        return new RestaurantAddressAdminResponseDto(restaurantAddress);
    }
}
