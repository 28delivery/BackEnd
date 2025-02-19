package com.sparta.spring_deep._delivery.domain.menu;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MenuController {

    private final MenuService menuService;

    // 메뉴 추가
    // TODO : security 기능 추가 되었을때 수정 예정.
    @PostMapping("/menus/{restaurantId}")
    public ResponseEntity<MenuResponseDto> addMenu(
        @PathVariable(name = "restaurantId") String restaurantId,
        @RequestBody MenuRequestDto requestDto,
        @RequestParam(name = "userId") String userId
    ) {
        MenuResponseDto responseDto = menuService.addMenu(restaurantId, requestDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // restaurant_id 기반 모든 메뉴 조회
    @GetMapping("/menus/{restaurantId}")
    public ResponseEntity<Page<MenuResponseDto>> getRestaurantAllMenus(
        @PathVariable(name = "restaurantId") UUID restaurantId,
        @RequestParam(required = false) String name,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Page<MenuResponseDto> responseDtoPage = menuService.getAllMenus(restaurantId, name, sortBy,
            page, size);
        return ResponseEntity.status(HttpStatus.OK).body(responseDtoPage);
    }

    // 메뉴 수정
    @PutMapping("/menus/{menuId}")
    public ResponseEntity<MenuResponseDto> updateMenu(
        @PathVariable(name = "menuId") UUID menuId,
        @RequestBody MenuRequestDto requestDto,
        @RequestParam(name = "userId") String userId
    ) {
        MenuResponseDto responseDto = menuService.updateMenu(menuId, requestDto, userId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);

    }

    // 메뉴 삭제
    // TODO : 유저와 시큐리티가 완성될 경우 검증하여 유저 객체 받기
    @DeleteMapping("/menus/{menuId}")
    public ResponseEntity<String> deleteMenu(
        @PathVariable(name = "menuId") UUID menuId,
        @RequestParam(name = "userId") String userId
    ) {
        menuService.deleteMenu(menuId, userId);
        return ResponseEntity.ok("success");
    }

    // 등록된 메뉴 설명 기반 Ai 설명 생성
    @PostMapping("/menus/{menuId}/aiDescription")
    public ResponseEntity<MenuAiResponseDto> aiDescription(
        @PathVariable(name = "menuId") UUID menuId,
        @RequestParam(name = "userId") String userId
    ) {
        MenuAiResponseDto responseDto = menuService.aiDescription(menuId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
