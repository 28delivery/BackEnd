package com.sparta.spring_deep._delivery.domain.menu;

import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @PostMapping("/menus/{restaurantId}")
    public ResponseEntity<MenuResponseDto> addMenu(
        @PathVariable(name = "restaurantId") UUID restaurantId,
        @RequestBody MenuRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        MenuResponseDto responseDto = menuService.addMenu(restaurantId, requestDto, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // restaurant_id 기반 모든 메뉴 조회
    @GetMapping("/menus/{restaurantId}")
    public ResponseEntity<Page<MenuResponseDto>> getRestaurantAllMenus(
        @PathVariable(name = "restaurantId") UUID restaurantId,
        @RequestParam(required = false) String menuName,
        @PageableDefault(size = 10, page = 0, direction = Direction.DESC, sort = "createdAt") Pageable pageable
    ) {
        Page<MenuResponseDto> responseDtoPage = menuService.getAllMenus(restaurantId, menuName,
            pageable);
        return ResponseEntity.status(HttpStatus.OK).body(responseDtoPage);
    }

    // 메뉴 수정
    @PutMapping("/menus/{menuId}")
    public ResponseEntity<MenuResponseDto> updateMenu(
        @PathVariable(name = "menuId") UUID menuId,
        @RequestBody MenuRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        MenuResponseDto responseDto = menuService.updateMenu(menuId, requestDto, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // 메뉴 삭제
    @DeleteMapping("/menus/{menuId}")
    public ResponseEntity<String> deleteMenu(
        @PathVariable(name = "menuId") UUID menuId,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        menuService.deleteMenu(menuId, userDetails);
        return ResponseEntity.ok("success");
    }

    // 등록된 메뉴 설명 기반 Ai 설명 생성
    @PostMapping("/menus/{menuId}/aiDescription")
    public ResponseEntity<MenuAiResponseDto> aiDescription(
        @PathVariable(name = "menuId") UUID menuId,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        MenuAiResponseDto responseDto = menuService.aiDescription(menuId, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}