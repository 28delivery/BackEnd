package com.sparta.spring_deep._delivery.domain.menu;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MenuController {

    private final MenuService menuService;

    // 메뉴 추가
    // TODO : created_by, update_by, deleted_by 값 null 저장 중. User, security 기능 추가 되었을때 수정 예정.
    @PostMapping("/menus/{restaurantId}")
    public ResponseEntity<MenuResponseDto> addMenu(
        @PathVariable(name = "restaurantId") String restaurantId,
        @RequestBody MenuRequestDto requestDto
    ) {
        MenuResponseDto responseDto = menuService.addMenu(restaurantId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // restaurant_id 기반 모든 메뉴 조회
    @GetMapping("/menus/{restaurantId}")
    public ResponseEntity<List<MenuResponseDto>> getRestaurantAllMenus(
        @PathVariable(name = "restaurantId") String restaurantId
    ) {
        List<MenuResponseDto> responseDtoList = menuService.getAllMenus(restaurantId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDtoList);
    }

    // 메뉴 수정
    @PutMapping("/menus/{menuId}")
    public ResponseEntity<MenuResponseDto> updateMenu(
        @PathVariable(name = "menuId") String menuId,
        @RequestBody MenuRequestDto requestDto
    ) {
        MenuResponseDto responseDto = menuService.updateMenu(menuId, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);

    }

    // 메뉴 삭제
    // TODO : 유저와 시큐리티가 완성될 경우 검증하여 유저 id 추가하여 삭제자 필드에 채워넣기
    @DeleteMapping("/menus/{menuId}")
    public ResponseEntity<String> deleteMenu(
        @PathVariable(name = "menuId") UUID menuId
    ) {
        menuService.deleteMenu(menuId);
        return ResponseEntity.ok("success");
    }

    @PostMapping("/menus/{menuId}/aiDescription")
    public ResponseEntity<MenuAiResponseDto> aiDescription(
        @PathVariable(name = "menuId") String menuId
    ) {
        MenuAiResponseDto responseDto = menuService.aiDescription(menuId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
