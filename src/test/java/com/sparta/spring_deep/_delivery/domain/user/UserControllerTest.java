package com.sparta.spring_deep._delivery.domain.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import com.sparta.spring_deep._delivery.domain.user.dto.LoginRequestDto;
import com.sparta.spring_deep._delivery.domain.user.dto.LoginResponseDto;
import com.sparta.spring_deep._delivery.domain.user.controller.UserController;
import com.sparta.spring_deep._delivery.domain.user.dto.UserDto;
import com.sparta.spring_deep._delivery.domain.user.entity.IsPublic;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import com.sparta.spring_deep._delivery.domain.user.repository.UserRepository;
import com.sparta.spring_deep._delivery.domain.user.service.UserService;
import com.sparta.spring_deep._delivery.util.JwtUtil;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this); // Mock 객체 초기화
//        mockMvc = MockMvcBuilders
//            .standaloneSetup(userController)
//            .apply(SecurityMockMvcConfigurers.springSecurity()) // Spring Security 설정 적용
//            .build();
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    @DisplayName("회원가입 테스트")
    void registerUserTest() throws Exception {
        // Given
        UserDto userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setPassword("password123");
        userDto.setEmail("test@example.com");
        userDto.setRole(UserRole.CUSTOMER);
        userDto.setIsPublic(IsPublic.PUBLIC);

        User mockUser = User.builder()
            .username(userDto.getUsername())
            .password(userDto.getPassword())
            .email(userDto.getEmail())
            .role(userDto.getRole())
            .isPublic(userDto.getIsPublic())
            .build();

        when(userService.registerUser(any(UserDto.class))).thenReturn(mockUser);

        System.out.println("======================================");
        System.out.println(objectMapper.writeValueAsString(mockUser));

        System.out.println("======================================");
        System.out.println(objectMapper.writeValueAsString(userDto));

        System.out.println("======================================");
        System.out.println(userDto);

        // When & Then
        mockMvc.perform(post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(userDto)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("로그인 테스트")
    void loginTest() throws Exception {
        // Given
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        String mockToken = "mock-jwt-token";
        LoginResponseDto responseDto = new LoginResponseDto(mockToken, "testuser", Collections.singletonList("CUSTOMERS"));

        // 가짜 사용자 정보 생성
        UserDetailsImpl mockUserDetails = new UserDetailsImpl(
            new User(
                "testuser", "encodedPassword", "test@example.com",
                UserRole.CUSTOMER,
                IsPublic.PUBLIC
            )
        );

        Authentication mockAuth = new UsernamePasswordAuthenticationToken(mockUserDetails, null, mockUserDetails.getAuthorities());

        // Mock 설정
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuth);
        when(jwtUtil.createJwt(mockUserDetails.getUsername(), mockUserDetails.getUser().getRole())).thenReturn(mockToken);

        when(userService.login(any(LoginRequestDto.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value(mockToken))
            .andExpect(jsonPath("$.user").value("testuser"));
    }

    @Test
    @DisplayName("사용자 정보 조회 테스트")
    void getCurrentUserTest() throws Exception {
        // Given
        User mockUser = User.builder()
            .username("testuser")
            .email("test@example.com")
            .role(UserRole.CUSTOMER)
            .isPublic(IsPublic.PUBLIC)
            .build();

        UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);

        // When & Then
        mockMvc.perform(get("/api/users/me")
                .with(user(userDetails))) // 가짜 사용자 추가
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.email").value("test@example.com"));
    }
}
