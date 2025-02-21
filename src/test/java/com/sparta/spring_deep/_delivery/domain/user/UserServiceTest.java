package com.sparta.spring_deep._delivery.domain.user;

import com.sparta.spring_deep._delivery.domain.user.dto.LoginRequestDto;
import com.sparta.spring_deep._delivery.domain.user.dto.LoginResponseDto;
import com.sparta.spring_deep._delivery.domain.user.dto.PasswordChangeDto;
import com.sparta.spring_deep._delivery.domain.user.dto.UserDto;
import com.sparta.spring_deep._delivery.domain.user.entity.IsPublic;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import com.sparta.spring_deep._delivery.domain.user.repository.UserRepository;
import com.sparta.spring_deep._delivery.domain.user.service.UserService;
import com.sparta.spring_deep._delivery.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup(TestInfo testInfo) {
        // testLoginSuccess 테스트만 셋팅 제외
        if(testInfo.getTestMethod().isPresent() && !testInfo.getTestMethod().get().getName().equals("testLoginSuccess")){
            MockitoAnnotations.openMocks(this);
        }
    }

    @Test
    public void testRegisterUserSuccess(){
        MockitoAnnotations.openMocks(this);

        UserDto userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setPassword("password");
        userDto.setEmail("test@example.com");
        userDto.setRole(UserRole.CUSTOMER);
        userDto.setIsPublic(IsPublic.PUBLIC);

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        when(userRepository.save(any(User.class))).thenReturn(user);

        User registeredUser = userService.registerUser(userDto);
        assertNotNull(registeredUser);
        assertEquals("testuser", registeredUser.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testLoginSuccess(){
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("testuser");
        loginRequestDto.setPassword("password123");

        // 모의 Authentication 객체 생성
        Authentication authentication = mock(Authentication.class);
        // any()를 사용하여 전달되는 인자와 관계없이 항상 모의 객체를 반환하도록 설정
        when(authenticationManager.authenticate(argThat(token ->
            token.getPrincipal().equals(loginRequestDto.getUsername()) &&
                token.getCredentials().equals(loginRequestDto.getPassword())
        ))).thenReturn(authentication);

        // 모의 UserDetailsImpl 객체 생성 및 설정
        com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl userDetails =
            mock(com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl.class);

        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setIsPublic(IsPublic.PUBLIC);
        user.setRole(UserRole.CUSTOMER);

        when(userDetails.getUser()).thenReturn(user);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(jwtUtil.createJwt("testuser", UserRole.CUSTOMER)).thenReturn("jwt-token");

        LoginResponseDto loginResponseDto = userService.login(loginRequestDto);
        assertNotNull(loginResponseDto);
        assertEquals("jwt-token", loginResponseDto.getToken());
        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    public void testUpdateUserSuccess(){
        String username = "testuser";
        UserDto userDto = new UserDto();
        userDto.setEmail("newemail@example.com");
        userDto.setRole(UserRole.CUSTOMER);
        userDto.setIsPublic(IsPublic.PRIVATE);

        User user = new User();
        user.setUsername("testuser");
        user.setEmail("oldemail@example.com");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        User updatedUser = userService.updateUser(username, userDto);
        assertEquals("newemail@example.com", updatedUser.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testChangePasswordSuccess(){
        String username = "testuser";
        PasswordChangeDto passwordChangeDto = new PasswordChangeDto();
        passwordChangeDto.setOldPassword("oldPass");
        passwordChangeDto.setNewPassword("newPass");

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("oldEncodedPassword");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPass")).thenReturn("newEncodedPass");
        when(userRepository.save(user)).thenReturn(user);

        userService.changePassword(username, passwordChangeDto);
        assertEquals("newEncodedPass", user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testDeleteUserSuccess(){
        String username = "testuser";

        User user = new User();
        user.setUsername("testuser");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.deleteUser(username);
        // soft delete의 경우, 삭제 플래그가 설정되었는지 검증 가능 (User.delete() 내부 로직에 따라)
        verify(userRepository, times(1)).save(user);
    }
}
