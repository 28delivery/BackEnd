package com.sparta.spring_deep._delivery.domain.menu;

import com.sparta.spring_deep._delivery.admin.repository.AiRepository;
import com.sparta.spring_deep._delivery.domain.ai.Ai;
import com.sparta.spring_deep._delivery.domain.ai.GoogleAiService;
import com.sparta.spring_deep._delivery.domain.restaurant.Restaurant;
import com.sparta.spring_deep._delivery.domain.restaurant.RestaurantRepository;
import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final GoogleAiService googleAiService;
    private final AiRepository aiRepository;
    private final RestaurantRepository restaurantRepository;

    // 메뉴 추가
    public MenuResponseDto addMenu(UUID restaurantId, MenuRequestDto requestDto,
        UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getUser().getUsername())
            .orElseThrow(
                () -> new EntityNotFoundException(
                    "해당 유저를 찾을 수 없습니다. username : " + userDetails.getUser().getUsername()));
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new EntityNotFoundException(
                "해당하는 레스토랑이 없습니다. restaurantId= " + restaurantId));
        if (!restaurant.getOwner().equals(userDetails.getUser())) {
            throw new AccessDeniedException(
                "메뉴를 수정할 권한이 없습니다. 현재 권한 : " + userDetails.getUser().getRole());
        }
        Menu menu = Menu.builder()
            .restaurantId(restaurantId)
            .name(requestDto.getName())
            .description(requestDto.getDescription())
            .price(requestDto.getPrice())
            .isHidden(requestDto.getIsHidden())
            .user(user)
            .build();
        menuRepository.save(menu);
        return new MenuResponseDto(menu);
    }

    // restaurant_id 기반 모든 메뉴 조회
    @Transactional
    public Page<MenuResponseDto> getAllMenus(UUID restaurantId, String name, String sortBy,
        int page, int size) {
        int pageSize = (size == 10 || size == 30 || size == 50) ? size : 10;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Direction.DESC, sortBy));
        Page<Menu> menus;
        menus = menuRepository.findAllByRestaurantId(restaurantId, pageable);
        return menus.map(MenuResponseDto::new);
    }

    // 메뉴 수정
    public MenuResponseDto updateMenu(UUID menuId, MenuRequestDto requestDto,
        UserDetailsImpl userDetails) {
        Menu menu = menuRepository.findById(menuId)
            .orElseThrow(() -> new EntityNotFoundException("메뉴 수정 : 해당 메뉴를 찾을 수 없습니다."));
        User user = userRepository.findById(userDetails.getUser().getUsername())
            .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));
        UUID restaurantId = menu.getRestaurantId();
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new EntityNotFoundException(
                "해당하는 레스토랑이 없습니다. restaurantId= " + restaurantId));
        if (!restaurant.getOwner().equals(userDetails.getUser())) {
            throw new AccessDeniedException(
                "메뉴를 수정할 권한이 없습니다. 현재 권한 : " + userDetails.getUser().getRole());
        }
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
    public void deleteMenu(UUID menuId, UserDetailsImpl userDetails) {
        Menu menu = menuRepository.findById(menuId)
            .orElseThrow(() -> new EntityNotFoundException("해당 메뉴를 찾을 수 없습니다. 메뉴 id : " + menuId));
        UUID restaurantId = menu.getRestaurantId();
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new EntityNotFoundException(
                "해당하는 레스토랑이 없습니다. restaurantId= " + restaurantId));
        User user = userRepository.findById(userDetails.getUser().getUsername())
            .orElseThrow(() -> new EntityNotFoundException(
                "해당 유저를 찾을 수 없습니다. 유저 id : " + userDetails.getUser().getUsername()));
        if (!restaurant.getOwner().equals(userDetails.getUser())) {
            throw new AccessDeniedException(
                "메뉴를 삭제할 권한이 없습니다. 현재 권한 : " + userDetails.getUser().getRole());
        } else {
            menu.delete(user);
        }
    }

    // ai 설명 생성
    public MenuAiResponseDto aiDescription(UUID menuId, UserDetailsImpl userDetails) {
        Menu menu = menuRepository.findById(menuId)
            .orElseThrow(() -> new EntityNotFoundException("AI 설명 생성 : 해당 메뉴를 찾을 수 없습니다."));
        User user = userRepository.findById(userDetails.getUser().getUsername())
            .orElseThrow(() -> new EntityNotFoundException(
                "해당 유저를 찾을 수 없습니다. 유저 id : " + userDetails.getUser().getUsername()));
        String aiDescription = googleAiService.createAiDescription(menu.getDescription());
        Ai aiLog = new Ai(menu, menu.getDescription(), aiDescription, user);
        aiRepository.save(aiLog);
        return new MenuAiResponseDto(menuId, aiDescription);
    }
}