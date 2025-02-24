package com.sparta.spring_deep._delivery.admin.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "UserAdminService")
@RequestMapping("/admin")
public class UserAdminController {

    private final UserAdminService userAdminService;

    // 사용자 전체 조회 + 페이징 기능
    @GetMapping("/users/search") // search?page=0&size=2로 요청하면 2개만 조회
    public ResponseEntity<Page<UserAdminResponseDto>> searchUsers(
        @ModelAttribute UserAdminSearchDto searchDto,
        @PageableDefault(sort = "username", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("searchUsers");

        Page<UserAdminResponseDto> users = userAdminService.searchUsers(searchDto, pageable);

        return ResponseEntity.ok(users);
    }

    // 사용자 상세 조회
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserAdminResponseDto> getUser(
        @PathVariable String userId) {
        log.info("getUser");

        UserAdminResponseDto responseDto = userAdminService.getUserDetails(userId);

        return ResponseEntity.ok(responseDto);
    }

    // 사용자 등록
    @PostMapping("/users")
    public ResponseEntity<UserAdminResponseDto> createUser(
        @RequestBody UserCreateRequestDto userCreateRequestDto,
        @AuthenticationPrincipal UserDetails userDetails) {
        log.info("createUser");

        UserAdminResponseDto responseDto = userAdminService.createUser(userCreateRequestDto,
            userDetails);

        return ResponseEntity.ok(responseDto);
    }

    // 사용자 수정
    @PutMapping("/users/{userId}")
    public ResponseEntity<UserAdminResponseDto> updateUser(
        @PathVariable String userId,
        @RequestBody UserUpdateRequestDto userUpdateRequestDto,
        @AuthenticationPrincipal UserDetails userDetails) {
        log.info("updateUser");

        UserAdminResponseDto responseDto = userAdminService.updateUser(userId,
            userUpdateRequestDto, userDetails);

        return ResponseEntity.ok(responseDto);
    }

    // 사용자 삭제
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(
        @PathVariable String userId,
        @AuthenticationPrincipal UserDetails userDetails) {
        log.info("deleteUser");

        userAdminService.deleteUser(userId, userDetails);

        return ResponseEntity.ok("user deleted successfully");
    }


}

