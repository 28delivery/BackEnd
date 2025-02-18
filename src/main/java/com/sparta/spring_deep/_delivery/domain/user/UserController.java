package com.sparta.spring_deep._delivery.domain.user;

import com.sparta.spring_deep._delivery.domain.auth.LoginRequestDto;
import com.sparta.spring_deep._delivery.domain.auth.LoginResponseDto;
import com.sparta.spring_deep._delivery.util.JwtUtil;
import java.nio.file.attribute.UserPrincipal;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody UserDto userDto) {
        User newUser = userService.registerUser(userDto);
        System.out.println("created user: " + newUser);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtil.createJwt(userDetails.getUsername(), userDetails.getUser().getRole());

        List<String> roles = Collections.singletonList(userDetails.getUser().getRole().name());

        return ResponseEntity.ok(new LoginResponseDto(jwt, userDetails.getUsername(), roles));
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{username}")
    public ResponseEntity<?> updateUser(@PathVariable String username, @RequestBody UserDto userDto) {
        User updatedUser = userService.updateUser(username, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{username}/password")
    public ResponseEntity<?> changePassword(@PathVariable String username, @RequestBody PasswordChangeDto passwordChangeDto) {
        userService.changePassword(username, passwordChangeDto);
        return ResponseEntity.ok("Password updated successfully");
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);  // Soft delete
        return ResponseEntity.ok("User deleted successfully");
    }
}
