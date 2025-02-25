package com.sparta.spring_deep._delivery.domain.user.service;

import com.sparta.spring_deep._delivery.domain.user.dto.PasswordChangeDto;
import com.sparta.spring_deep._delivery.domain.user.dto.UserDto;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.jwt.JwtUtil;
import com.sparta.spring_deep._delivery.domain.user.repository.UserRepository;
import com.sparta.spring_deep._delivery.exception.DuplicateResourceException;
import com.sparta.spring_deep._delivery.exception.GlobalExceptionHandler;
import com.sparta.spring_deep._delivery.exception.OwnershipMismatchException;
import com.sparta.spring_deep._delivery.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "UserService")
public class UserService {

    @Autowired
    private final AuthenticationManager authenticationManager;
    @Autowired
    private final JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(UserDto userDto) {

        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new DuplicateResourceException();
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new DuplicateResourceException("이미 사용중인 Email 입니다.");
        }

        // Encrypting the password
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // Creating user's account
        User user = User.builder()
            .username(userDto.getUsername())
            .password(userDto.getPassword())
            .email(userDto.getEmail())
            .role(userDto.getRole())
            .isPublic(userDto.getIsPublic())
            .build();

        user.setCreatedBy(userDto.getUsername());
        user.setUpdatedBy(userDto.getUsername());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setIsDeleted(false);

        user = userRepository.save(user);// 첫 번째 저장 (createdBy = null)

        return user;
    }

    public User updateUser(String userName, UserDto userDto) {

        User user = userRepository.findByUsernameAndIsDeletedFalse(userName)
            .orElseThrow(ResourceNotFoundException::new);

        // Check if the email was updated to a new one that already exists
        if (!user.getEmail().equals(userDto.getEmail()) && userRepository.existsByEmail(
            userDto.getEmail())) {
            log.info("Check if the email was updated to a new one that already exists");
            throw new OwnershipMismatchException();
        }

        user.setEmail(userDto.getEmail());
        user.setRole(userDto.getRole());
        user.setIsPublic(userDto.getIsPublic());
        user.update(userName);
        return userRepository.save(user);
    }

    public void deleteUser(String userName) {
        log.info("delete user " + userName);

        User user = userRepository.findByUsernameAndIsDeletedFalse(userName)
            .orElseThrow(ResourceNotFoundException::new);
        user.setIsDeleted(true); // Assuming there's an isActive flag for soft delete
        user.delete(userName);
        userRepository.save(user);
    }

    public User changePassword(String userName, PasswordChangeDto passwordChangeDto) {
        if (passwordChangeDto.getOldPassword().equals(passwordChangeDto.getNewPassword())) {
            throw new DuplicateResourceException("새 비밀번호는 이전 비밀번호와 달라야 합니다.");
        }

        log.info("change password " + userName);

        User user = userRepository.findByUsernameAndIsDeletedFalse(userName)
            .orElseThrow(ResourceNotFoundException::new);
        user.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
        user.update(userName);
        return userRepository.save(user);
    }

}

