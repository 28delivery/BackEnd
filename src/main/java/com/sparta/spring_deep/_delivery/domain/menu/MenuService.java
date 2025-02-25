package com.sparta.spring_deep._delivery.domain.menu;

import static com.sparta.spring_deep._delivery.util.AuthTools.ownerCheck;

import com.sparta.spring_deep._delivery.admin.ai.AiRepository;
import com.sparta.spring_deep._delivery.domain.ai.Ai;
import com.sparta.spring_deep._delivery.domain.ai.GoogleAiService;
import com.sparta.spring_deep._delivery.domain.restaurant.Restaurant;
import com.sparta.spring_deep._delivery.domain.restaurant.RestaurantRepository;
import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.exception.ResourceNotFoundException;
import com.sparta.spring_deep._delivery.util.AuthTools;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "MenuService")
public class MenuService {

    private final MenuRepository menuRepository;
    private final GoogleAiService googleAiService;
    private final AiRepository aiRepository;
    private final RestaurantRepository restaurantRepository;

    // 메뉴 추가
    @Transactional
    public MenuResponseDto addMenu(
        UUID restaurantId,
        MenuRequestDto requestDto,
        UserDetailsImpl userDetails
    ) {
        log.info("메뉴 추가");

        User user = userDetails.getUser();

        Restaurant restaurant = restaurantRepository.findByIdAndIsDeletedFalse(restaurantId)
            .orElseThrow(ResourceNotFoundException::new);

        // 음식점 오너로 로그인 되어 있는지 확인
        ownerCheck(user, restaurant.getOwner());

        AuthTools.roleCheck(userDetails, restaurant, "메뉴 추가");

        Menu menu = Menu.builder()
            .restaurant(restaurant)
            .name(requestDto.getName())
            .description(requestDto.getDescription())
            .price(requestDto.getPrice())
            .isHidden(requestDto.getIsHidden())
            .user(user)
            .build();

        menuRepository.save(menu);
        return new MenuResponseDto(menu);
    }

    public Page<MenuResponseDto> searchMenus(UUID restaurantId, MenuSearchDto searchDto,
        Pageable pageable) {
        log.info("restaurant_id 기반 모든 메뉴 검색 및 조회");

        Page<MenuResponseDto> responseDto = menuRepository.searchByOptionAndIsDeletedFalse(
            restaurantId, searchDto, pageable);

        // 검색 및 조회 결과가 비어있다면 Exception 발생
        if (responseDto.isEmpty()) {
            throw new ResourceNotFoundException();
        }

        return responseDto;
    }

    // 메뉴 수정
    @Transactional
    public MenuResponseDto updateMenu(
        UUID menuId,
        MenuRequestDto requestDto,
        UserDetailsImpl userDetails
    ) {
        log.info("메뉴 수정");

        Menu menu = menuRepository.findByIdAndIsDeletedFalse(menuId)
            .orElseThrow(ResourceNotFoundException::new);

        User user = userDetails.getUser();

        Restaurant restaurant = restaurantRepository.findByIdAndIsDeletedFalse(
                menu.getRestaurant().getId())
            .orElseThrow(ResourceNotFoundException::new);

        // 사장님으로 로그인 했는지 확인
        ownerCheck(user, restaurant.getOwner());

        AuthTools.roleCheck(userDetails, restaurant, "메뉴 수정");

        menu.update(
            requestDto.getName(),
            requestDto.getDescription(),
            requestDto.getPrice(),
            requestDto.getIsHidden(),
            user
        );
        menuRepository.save(menu);
        return new MenuResponseDto(menu);
    }

    // 메뉴 삭제
    @Transactional
    public void deleteMenu(
        UUID menuId,
        UserDetailsImpl userDetails
    ) {
        log.info("메뉴 삭제");

        Menu menu = menuRepository.findByIdAndIsDeletedFalse(menuId)
            .orElseThrow(ResourceNotFoundException::new);

        Restaurant restaurant = restaurantRepository.findByIdAndIsDeletedFalse(
                menu.getRestaurant().getId())
            .orElseThrow(ResourceNotFoundException::new);

        User user = userDetails.getUser();

        // 사장님으로 로그인 했는지 확인
        ownerCheck(user, restaurant.getOwner());

        AuthTools.roleCheck(userDetails, restaurant, "메뉴 삭제");

        log.info("메뉴 삭제 됨 : 현재 유저 권한 : " + user.getRole());
        menu.delete(user);

    }

    // ai 설명 생성
    @Transactional
    public MenuAiResponseDto aiDescription(
        UUID menuId,
        UserDetailsImpl userDetails
    ) {
        log.info("ai 설명 생성");

        Menu menu = menuRepository.findByIdAndIsDeletedFalse(menuId)
            .orElseThrow(ResourceNotFoundException::new);

        User user = userDetails.getUser();

        Restaurant restaurant = restaurantRepository.findByIdAndIsDeletedFalse(
                menu.getRestaurant().getId())
            .orElseThrow(ResourceNotFoundException::new);

        // 사장님으로 로그인 했는지 확인
        ownerCheck(user, restaurant.getOwner());

        AuthTools.roleCheck(userDetails, restaurant, "Ai 설명 생성");

        String aiDescription = googleAiService.createAiDescription(menu.getDescription());

        Ai aiLog = new Ai(menu, menu.getDescription(), aiDescription, user);

        aiRepository.save(aiLog);
        return new MenuAiResponseDto(menuId, aiDescription);
    }

}