//package com.sparta.spring_deep._delivery.domain.user;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sparta.spring_deep._delivery.domain.user.controller.UserController;
//import com.sparta.spring_deep._delivery.domain.user.dto.LoginRequestDto;
//import com.sparta.spring_deep._delivery.domain.user.dto.LoginResponseDto;
//import com.sparta.spring_deep._delivery.domain.user.dto.PasswordChangeDto;
//import com.sparta.spring_deep._delivery.domain.user.dto.UserDto;
//import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
//import com.sparta.spring_deep._delivery.domain.user.entity.User;
//import com.sparta.spring_deep._delivery.domain.user.service.UserService;
//import com.sparta.spring_deep._delivery.util.JwtUtil;
//import jakarta.validation.Valid;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.core.MethodParameter;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.bind.support.WebDataBinderFactory;
//import org.springframework.web.context.request.NativeWebRequest;
//import org.springframework.web.method.support.HandlerMethodArgumentResolver;
//import org.springframework.web.method.support.ModelAndViewContainer;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(MockitoExtension.class)
//public class UserControllerTest {
//
//    @InjectMocks
//    private UserController userController;
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private JwtUtil jwtUtil;
//
//    private MockMvc mockMvc;
//
//    private ObjectMapper objectMapper = new ObjectMapper();
//
//    // 간단한 커스텀 인자 해결자: @AuthenticationPrincipal로 주입되는 값을 더미 UserDetailsImpl로 설정
//    private static class FakeAuthenticationPrincipalResolver implements HandlerMethodArgumentResolver {
//
//        @Override
//        public boolean supportsParameter(MethodParameter parameter) {
//            return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
//        }
//
//        @Override
//        public Object resolveArgument(MethodParameter parameter,
//            ModelAndViewContainer mavContainer,
//            NativeWebRequest webRequest,
//            WebDataBinderFactory binderFactory) {
//            User dummyUser = new User();
//            dummyUser.setUsername("testuser");
//            // 필요한 경우 추가 속성을 설정합니다.
//            return new UserDetailsImpl(dummyUser);
//        }
//    }
//
//    @BeforeEach
//    void setup() {
//        mockMvc = MockMvcBuilders.standaloneSetup(userController)
//            .setCustomArgumentResolvers(new FakeAuthenticationPrincipalResolver())
//            .build();
//    }
//
//    @Test
//    void testSignupSuccess() throws Exception {
//        UserDto userDto = new UserDto();
//        userDto.setUsername("testuser");
//        userDto.setPassword("password");
//        userDto.setEmail("test@example.com");
//
//        User user = new User();
//        user.setUsername("testuser");
//        user.setEmail("test@example.com");
//
//        when(userService.registerUser(any(UserDto.class))).thenReturn(user);
//
//        mockMvc.perform(post("/api/users/signup")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(userDto)))
//            .andExpect(status().isCreated())
//            .andExpect(jsonPath("$.username").value("testuser"));
//    }
//
//    @Test
//    void testLoginSuccess() throws Exception {
//        LoginRequestDto loginRequestDto = new LoginRequestDto();
//        loginRequestDto.setUsername("testuser");
//        loginRequestDto.setPassword("password");
//
//        LoginResponseDto loginResponseDto = new LoginResponseDto("jwt-token", "testuser", "test@example.com", null, null);
//        when(userService.login(any(LoginRequestDto.class))).thenReturn(loginResponseDto);
//
//        mockMvc.perform(post("/api/users/login")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(loginRequestDto)))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.token").value("jwt-token"));
//    }
//
//    @Test
//    void testLogoutSuccess() throws Exception {
//        String token = "Bearer jwt-token";
//
//        mockMvc.perform(post("/api/users/logout")
//                .header("Authorization", token))
//            .andExpect(status().isOk())
//            .andExpect(content().string("You've been logged out successfully."));
//    }
//
//    @Test
//    void testGetCurrentUser() throws Exception {
//        // @AuthenticationPrincipal는 FakeAuthenticationPrincipalResolver를 통해 주입됨
//        mockMvc.perform(get("/api/users/me"))
//            .andExpect(status().isOk());
//    }
//
//    @Test
//    void testUpdateUserSuccess() throws Exception {
//        String username = "testuser";
//        UserDto userDto = new UserDto();
//        userDto.setEmail("newemail@example.com");
//
//        User updatedUser = new User();
//        updatedUser.setUsername(username);
//        updatedUser.setEmail("newemail@example.com");
//
//        when(userService.updateUser(eq(username), any(UserDto.class))).thenReturn(updatedUser);
//
//        mockMvc.perform(put("/api/users/" + username)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(userDto)))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.email").value("newemail@example.com"));
//    }
//
//    @Test
//    void testChangePasswordSuccess() throws Exception {
//        String username = "testuser";
//        PasswordChangeDto passwordChangeDto = new PasswordChangeDto();
//        passwordChangeDto.setOldPassword("oldPass");
//        passwordChangeDto.setNewPassword("newPass");
//
//        mockMvc.perform(put("/api/users/" + username + "/password")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(passwordChangeDto)))
//            .andExpect(status().isOk())
//            .andExpect(content().string("Password updated successfully"));
//    }
//
//    @Test
//    void testDeleteUserSuccess() throws Exception {
//        String username = "testuser";
//
//        mockMvc.perform(delete("/api/users/" + username))
//            .andExpect(status().isOk())
//            .andExpect(content().string("User deleted successfully"));
//    }
//}
