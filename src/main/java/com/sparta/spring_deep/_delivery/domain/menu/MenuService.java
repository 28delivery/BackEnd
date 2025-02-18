package com.sparta.spring_deep._delivery.domain.menu;

import com.sparta.spring_deep._delivery.domain.ai.GoogleAiService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final GoogleAiService googleAiService;

    // 메뉴 추가
    public MenuResponseDto addMenu(String restaurantId, MenuRequestDto requestDto) {
        UUID uuid = UUID.fromString(restaurantId);

        Menu menu = Menu.builder()
            .restaurantId(uuid)
            .name(requestDto.getName())
            .description(requestDto.getDescription())
            .price(requestDto.getPrice())
            .isHidden(requestDto.getIsHidden())
            .build();

        menuRepository.save(menu);

        return new MenuResponseDto(menu);
    }

    // restaurant_id 기반 모든 메뉴 조회
    @Transactional
    public List<MenuResponseDto> getAllMenus(String restaurantId) {
        UUID uuid = UUID.fromString(restaurantId);

        List<Menu> menus = menuRepository.findAllByRestaurantId(uuid);

        return menus.stream()
            .map(MenuResponseDto::new)
            .collect(Collectors.toList());
    }

    // 메뉴 수정
    public MenuResponseDto updateMenu(String menuId, MenuRequestDto requestDto) {
        UUID uuid = UUID.fromString(menuId);

        Menu menu = menuRepository.findById(uuid)
            .orElseThrow(() -> new EntityNotFoundException("메뉴 수정 : 해당 메뉴를 찾을 수 없습니다."));

        menu.update(
            requestDto.getName(),
            requestDto.getDescription(),
            requestDto.getPrice(),
            requestDto.getIsHidden()
        );

        menuRepository.save(menu);
        return new MenuResponseDto(menu);
    }

    public void deleteMenu(UUID menuId) {
        menuRepository.softDeleteMenu(menuId);
    }


    public MenuAiResponseDto aiDescription(String menuId) {
        UUID uuid = UUID.fromString(menuId);

        Menu menu = menuRepository.findById(uuid)
            .orElseThrow(() -> new EntityNotFoundException("AI 설명 생성 : 해당 메뉴를 찾을 수 없습니다."));

        String aiDescription = googleAiService.createAiDescription(menu.getDescription());

        return new MenuAiResponseDto(menuId, aiDescription);
    }
}
