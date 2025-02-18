package com.sparta.spring_deep._delivery.domain.user;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(UserDto userDto) {
        if(userRepository == null){
            System.out.println("userRepository is null");
        }
        if(passwordEncoder == null){
            System.out.println("passwordEncoder is null");
        }
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("Username is already taken!");
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

        user.setCreatedBy(user);
        user.setUpdatedBy(user);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setIsDeleted(false);

        User saved = userRepository.save(user);
        System.out.println("created user in service: " + saved);
        return saved;
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
        return userRepository.save(user);
    }

    public void deleteUser(String userName) {
        User user = userRepository.findByUsername(userName).orElseThrow(() -> new RuntimeException("User not found!"));
        user.setIsDeleted(false); // Assuming there's an isActive flag for soft delete
        userRepository.save(user);
    }

    public void changePassword(String userName, PasswordChangeDto passwordChangeDto) {
        User user = userRepository.findByUsername(userName).orElseThrow(() -> new RuntimeException("User not found!"));
        user.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
        userRepository.save(user);
    }

    public User findById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found!"));
    }
}

