package com.sparta.spring_deep._delivery.admin.user;

import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import com.sparta.spring_deep._delivery.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 관리자 권한 체크
    private String checkAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User adminUser = userDetails.getUser();

        if (!UserRole.ADMIN.equals(userDetails.getUser().getRole())) {
            throw new IllegalArgumentException("Admin privileges required");
        }

        return adminUser.getUsername();
    }

    // 사용자 전체 조회
    public Page<UserAdminResponseDto> getAllUsers(UserSearchDto searchDto, Pageable pageable) {
        // admin 권한 체크
        checkAdminRole();

        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(UserAdminResponseDto::new);
    }

    // 사용자 상세 조회
    public UserAdminResponseDto getUserDetails(String username) {
        // admin 권한 체크
        checkAdminRole();

        // username 체크
        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
            .orElseThrow(
                () -> new EntityNotFoundException("User not found with username: " + username));

        return new UserAdminResponseDto(user);
    }

    // 사용자 생성
    @Transactional
    public UserAdminResponseDto createUser(UserCreateRequestDto requestDto) {
        // admin 권한 체크
        String adminUsername = checkAdminRole();

        // 이메일 중복 검사
        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        String encodedPassword = passwordEncoder.encode("defaultPassword");

        User user = User.builder()
            .username(requestDto.getUsername())
            .password(encodedPassword)
            .email(requestDto.getEmail())
            .role(requestDto.getRole())
            .isPublic(requestDto.getIsPublic())
            .build();

        user.setCreatedBy(adminUsername);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedBy(adminUsername);
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return new UserAdminResponseDto(savedUser);
    }

    // 사용자 수정
    @Transactional
    public UserAdminResponseDto updateUser(String username, UserUpdateRequestDto requestDto) {
        // admin 권한 체크
        String adminUsername = checkAdminRole();

        // username 체크
        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
            .orElseThrow(
                () -> new EntityNotFoundException("User not found with username: " + username));

        // 이메일 중복 체크 및 업데이트
        if (requestDto.getEmail() != null) {
            if (!user.getEmail().equals(requestDto.getEmail()) && userRepository
                .existsByEmail(requestDto.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
            user.setEmail(requestDto.getEmail());
        }

        // 역할 업데이트
        if (requestDto.getRole() != null) {
            user.setRole(requestDto.getRole());
        }

        // 공개 여부 업데이트
        if (requestDto.getIsPublic() != null) {
            user.setIsPublic(requestDto.getIsPublic());
        }

        user.setUpdatedBy(adminUsername);
        user.setUpdatedAt(LocalDateTime.now());

        return new UserAdminResponseDto(user);
    }

    // 사용자 삭제
    @Transactional
    public void deleteUser(String username) {
        // admin 권한 체크
        String adminUsername = checkAdminRole();

        // username 체크
        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
            .orElseThrow(
                () -> new EntityNotFoundException("User not found with username: " + username));

        user.setIsDeleted(true);
        user.setDeletedBy(adminUsername);
        user.setDeletedAt(LocalDateTime.now());
    }

}
