package com.sparta.spring_deep._delivery.admin.controller;

import com.sparta.spring_deep._delivery.admin.dto.UserAdminResponseDto;
import com.sparta.spring_deep._delivery.admin.dto.UserCreateRequestDto;
import com.sparta.spring_deep._delivery.admin.dto.UserUpdateRequestDto;
import com.sparta.spring_deep._delivery.admin.service.UserAdminService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {

    private final UserAdminService userAdminService;

    // 사용자 전체 조회
    @GetMapping
    public List<UserAdminResponseDto> getAllUsers() {
        return userAdminService.getAllUsers();
    }

    // 사용자 상세 조회
    @GetMapping("/{userId}")
    public UserAdminResponseDto getUser(
        @PathVariable String userId) {
        return userAdminService.getUserDetails(userId);
    }

    // 사용자 등록
    @PostMapping
    public UserAdminResponseDto createUser(
        @RequestBody UserCreateRequestDto userCreateRequestDto) {
        return userAdminService.createUser(userCreateRequestDto);
    }

    // 사용자 수정
    @PutMapping("/{userId}")
    public UserAdminResponseDto updateUser(
        @PathVariable String userId,
        @RequestBody UserUpdateRequestDto userUpdateRequestDto) {
        return userAdminService.updateUser(userId, userUpdateRequestDto);
    }

    // 사용자 삭제
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(
        @PathVariable String userId) {
        userAdminService.deleteUser(userId);
        return ResponseEntity.ok("user deleted successfully");
    }


}

