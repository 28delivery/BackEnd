package com.sparta.spring_deep._delivery.admin.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {

    private final UserAdminService userAdminService;

    // 사용자 전체 조회 + 페이징 기능
    @GetMapping("/search") // search?page=0&size=2로 요청하면 2개만 조회
    public ResponseEntity<Page<UserAdminResponseDto>> searchUsers(
        @ModelAttribute UserAdminSearchDto searchDto,
        @PageableDefault(page = 0, size = 10, sort = "username", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<UserAdminResponseDto> users = userAdminService.searchUsers(searchDto, pageable);

        return ResponseEntity.ok(users);
    }

    // 사용자 상세 조회
    @GetMapping("/{userId}")
    public ResponseEntity<UserAdminResponseDto> getUser(
        @PathVariable String userId) {

        UserAdminResponseDto responseDto = userAdminService.getUserDetails(userId);

        return ResponseEntity.ok(responseDto);
    }

    // 사용자 등록
    @PostMapping
    public ResponseEntity<UserAdminResponseDto> createUser(
        @RequestBody UserCreateRequestDto userCreateRequestDto) {

        UserAdminResponseDto responseDto = userAdminService.createUser(userCreateRequestDto);

        return ResponseEntity.ok(responseDto);
    }

    // 사용자 수정
    @PutMapping("/{userId}")
    public ResponseEntity<UserAdminResponseDto> updateUser(
        @PathVariable String userId,
        @RequestBody UserUpdateRequestDto userUpdateRequestDto) {

        UserAdminResponseDto responseDto = userAdminService.updateUser(userId,
            userUpdateRequestDto);

        return ResponseEntity.ok(responseDto);
    }

    // 사용자 삭제
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(
        @PathVariable String userId) {

        userAdminService.deleteUser(userId);

        return ResponseEntity.ok("user deleted successfully");
    }


}

