package com.sparta.spring_deep._delivery.domain.user.service;

import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import com.sparta.spring_deep._delivery.domain.user.dto.LoginRequestDto;
import com.sparta.spring_deep._delivery.domain.user.dto.LoginResponseDto;
import com.sparta.spring_deep._delivery.domain.user.dto.PasswordChangeDto;
import com.sparta.spring_deep._delivery.domain.user.dto.UserDto;
import com.sparta.spring_deep._delivery.domain.user.entity.IsPublic;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import com.sparta.spring_deep._delivery.domain.user.repository.UserRepository;
import com.sparta.spring_deep._delivery.domain.user.jwt.JwtUtil;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private final AuthenticationManager authenticationManager;

    @Autowired
    private final JwtUtil jwtUtil;

    @Transactional
    public User registerUser(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            User user = userRepository.findByUsername(userDto.getUsername()).get();
            if(!user.getIsDeleted()){
                throw new RuntimeException("Username is already taken!");
            }
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email is already in use!");
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

    public LoginResponseDto login(LoginRequestDto loginRequestDto){

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User user = userDetails.getUser();

        String username = userDetails.getUsername();
        String email = user.getEmail();
        IsPublic isPublic = user.getIsPublic();
        UserRole userRole = user.getRole();

        String jwt = jwtUtil.createJwt(username, userRole);

        return new LoginResponseDto(jwt, username, email, userRole, isPublic);
    }

    public User updateUser(String userName, UserDto userDto) {
        User user = userRepository.findByUsername(userName).orElseThrow(() -> new RuntimeException("User not found!"));

        // Check if the email was updated to a new one that already exists
        if (!user.getEmail().equals(userDto.getEmail()) && userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        user.setEmail(userDto.getEmail());
        user.setRole(userDto.getRole());
        user.setIsPublic(userDto.getIsPublic());
        user.update(userName);
        return userRepository.save(user);
    }

    public void deleteUser(String userName) {
        User user = userRepository.findByUsername(userName).orElseThrow(() -> new RuntimeException("User not found!"));
        user.setIsDeleted(true); // Assuming there's an isActive flag for soft delete
        user.delete(userName);
        userRepository.save(user);
    }

    public void changePassword(String userName, PasswordChangeDto passwordChangeDto) {
        User user = userRepository.findByUsername(userName).orElseThrow(() -> new RuntimeException("User not found!"));
        user.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
        user.update(userName);
        userRepository.save(user);
    }

    public boolean isDeletedUser(String userName) {
        // 소프트 삭제된 유저가 검색될때에는 오류처리
        return userRepository.findByUsername(userName)
            .map(User::getIsDeleted)
            .orElse(false);
    }
}

