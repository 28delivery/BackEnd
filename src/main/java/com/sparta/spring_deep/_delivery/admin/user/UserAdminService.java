package com.sparta.spring_deep._delivery.admin.user;

import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.repository.UserRepository;
import com.sparta.spring_deep._delivery.exception.DuplicateResourceException;
import com.sparta.spring_deep._delivery.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "UserAdminService")
public class UserAdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 사용자 검색 및 조회
    public Page<UserAdminResponseDto> searchUsers(UserAdminSearchDto searchDto, Pageable pageable) {
        log.info("searchUsers");

        Page<UserAdminResponseDto> responseDtos = userRepository.searchByOption(searchDto,
            pageable);

        // 검색 결과가 비어있을 경우 Exception 발생
        if (responseDtos.isEmpty()) {
            throw new ResourceNotFoundException();
        }

        return responseDtos;
    }

    // 사용자 상세 조회
    public UserAdminResponseDto getUserDetails(String username) {
        log.info("getUserDetails");

        // username 체크
        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
            .orElseThrow(ResourceNotFoundException::new);

        return new UserAdminResponseDto(user);
    }

    // 사용자 생성
    @Transactional
    public UserAdminResponseDto createUser(UserCreateRequestDto requestDto,
        UserDetails userDetails) {
        log.info("createUser");

        // admin 아이디 체크
        String adminUsername = userDetails.getUsername();

        // 유저네임 중복 검사
        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }
        // 이메일 중복 검사
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
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
    public UserAdminResponseDto updateUser(String username, UserUpdateRequestDto requestDto,
        UserDetails userDetails) {
        log.info("updateUser");

        // admin 아이디 체크
        String adminUsername = userDetails.getUsername();

        // username 체크
        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
            .orElseThrow(ResourceNotFoundException::new);

        // 이메일 중복 체크 및 업데이트
        if (requestDto.getEmail() != null) {
            if (!user.getEmail().equals(requestDto.getEmail()) && userRepository
                .existsByEmail(requestDto.getEmail())) {
                throw new DuplicateResourceException("Email already exists");
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
    public void deleteUser(String username, UserDetails userDetails) {
        log.info("deleteUser");

        // admin 유저 이름
        String adminUsername = userDetails.getUsername();

        // username 체크
        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
            .orElseThrow(ResourceNotFoundException::new);

        user.setIsDeleted(true);
        user.setDeletedBy(adminUsername);
        user.setDeletedAt(LocalDateTime.now());
    }

}
