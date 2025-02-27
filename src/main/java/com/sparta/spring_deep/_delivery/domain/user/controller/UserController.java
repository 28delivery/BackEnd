package com.sparta.spring_deep._delivery.domain.user.controller;

import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import com.sparta.spring_deep._delivery.domain.user.dto.PasswordChangeDto;
import com.sparta.spring_deep._delivery.domain.user.dto.UserDto;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.jwt.JwtUtil;
import com.sparta.spring_deep._delivery.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j(topic = "UserController")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/users/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserDto userDto,
        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            String errorMsg = fieldError != null ? fieldError.getDefaultMessage() : "Invalid input";
            logger.error("Sign up error: {}", errorMsg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMsg);
        }

        User newUser = userService.registerUser(userDto);
        logger.info("User Sign up: {}", newUser.getUsername());
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PostMapping("/users/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        // 클라이언트쪽에서 JWT 토큰 무효화해야 함!
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            jwtUtil.blacklist(jwtToken);
            logger.info("User logout with token: {}", jwtToken);
            return ResponseEntity.ok().body("You've been logged out successfully.");
        }
        logger.error("Invalid token for logout attempt: {}", token);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
    }

    @GetMapping("/users/me")
    public ResponseEntity<User> getCurrentUser(
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        logger.info("Looking user info for: {}", user.getUsername());
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("authentication.name == #username")
    @PutMapping("/users/{username}")
    public ResponseEntity<?> updateUser(@PathVariable String username,
        @Valid @RequestBody UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldError("email") != null ?
                bindingResult.getFieldError("email").getDefaultMessage() : "Invalid input";
            logger.error("Update user validation failed for {}: {}", username, errorMsg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMsg);
        }
        User updatedUser = userService.updateUser(username, userDto);
        logger.info("User updated successfully: {}", username);
        return ResponseEntity.ok(updatedUser);
    }

    @PreAuthorize("authentication.name == #username")
    @PutMapping("/users/{username}/password")
    public ResponseEntity<?> changePassword(@PathVariable String username,
        @Valid @RequestBody PasswordChangeDto passwordChangeDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            String errorMsg = fieldError != null ? fieldError.getDefaultMessage() : "Invalid input";
            logger.error("Sign up error: {}", errorMsg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMsg);
        }

        userService.changePassword(username, passwordChangeDto);
        return ResponseEntity.ok("Password updated successfully");
    }

    @PreAuthorize("authentication.name == #username")
    @DeleteMapping("/users/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);  // Soft delete
        logger.info("User soft deleted successfully: {}", username);
        return ResponseEntity.ok("User deleted successfully");
    }
}
