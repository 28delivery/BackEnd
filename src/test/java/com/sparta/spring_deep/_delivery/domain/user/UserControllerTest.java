package com.sparta.spring_deep._delivery.domain.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.spring_deep._delivery.domain.user.controller.UserController;
import com.sparta.spring_deep._delivery.domain.user.dto.LoginRequestDto;
import com.sparta.spring_deep._delivery.domain.user.dto.PasswordChangeDto;
import com.sparta.spring_deep._delivery.domain.user.dto.UserDto;
import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import com.sparta.spring_deep._delivery.domain.user.entity.IsPublic;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import com.sparta.spring_deep._delivery.domain.user.jwt.JwtAuthenticationFilter;
import com.sparta.spring_deep._delivery.domain.user.service.UserService;
import com.sparta.spring_deep._delivery.domain.user.jwt.JwtUtil;
import com.sparta.spring_deep._delivery.exception.DuplicateResourceException;
import com.sparta.spring_deep._delivery.exception.ResourceNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    // 간단한 커스텀 인자 해결자: @AuthenticationPrincipal로 주입되는 값을 더미 UserDetailsImpl로 설정
    private static class FakeAuthenticationPrincipalResolver implements HandlerMethodArgumentResolver {

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
        }

        @Override
        public Object resolveArgument(MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {
            User dummyUser = new User();
            dummyUser.setUsername("testuser");
            // 필요한 경우 추가 속성을 설정합니다.
            return new UserDetailsImpl(dummyUser);
        }
    }

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
            .setCustomArgumentResolvers(new FakeAuthenticationPrincipalResolver())
            .build();
    }

    @Nested
    @DisplayName("회원 가입 테스트")
    class SignupTests {
        // 테스트에 사용할 UserDto 생성 헬퍼 메서드
        private UserDto createUserDto(String username, String password, String email) {
            UserDto userDto = new UserDto();
            userDto.setUsername(username);
            userDto.setPassword(password);
            userDto.setEmail(email);
            return userDto;
        }

        // userService의 registerUser 스텁 설정 헬퍼 메서드
        private void stubUserService(String username, String email) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            lenient().when(userService.registerUser(any(UserDto.class))).thenReturn(user);
        }

        // 공통 POST 요청 헬퍼 메서드
        private ResultActions performSignup(UserDto userDto) throws Exception {
            return mockMvc.perform(post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)));
        }

        @Test
        @DisplayName("회원 가입 성공")
        void testSignupSuccess() throws Exception {
            UserDto userDto = createUserDto("testuser", "Password!@3", "test@example.com");
            stubUserService("testuser", "test@example.com");

            performSignup(userDto)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"));
        }

        @Test
        @DisplayName("회원 가입 실패 - 유효하지 않은 사용자명")
        void testSignupInvalidUsernameFailure() throws Exception {
            UserDto userDto = createUserDto("US!A", "Password!@3", "test@example.com");
            stubUserService("US!A", "test@example.com");

            performSignup(userDto)
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("회원 가입 실패 - 유효하지 않은 비밀번호")
        void testSignupInvalidPasswordFailure() throws Exception {
            UserDto userDto = createUserDto("testuser", "ps", "test@example.com");
            stubUserService("testuser", "test@example.com");

            performSignup(userDto)
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("회원 가입 실패 - 유효하지 않은 이메일")
        void testSignupInvalidEmailFailure() throws Exception {
            UserDto userDto = createUserDto("testuser", "Password!@3", "testexample.com");
            stubUserService("testuser", "testexample.com");

            performSignup(userDto)
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("로그인 테스트")
    class LoginTests {
        // LoginRequestDto를 기반으로 MockHttpServletRequest 생성
        private MockHttpServletRequest createRequest(LoginRequestDto loginRequestDto) throws Exception {
            String loginJson = objectMapper.writeValueAsString(loginRequestDto);
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setContentType("application/json");
            request.setContent(loginJson.getBytes(StandardCharsets.UTF_8));
            return request;
        }

        // 모의 AuthenticationManager를 주입한 JwtAuthenticationFilter 생성
        private JwtAuthenticationFilter createFilter(AuthenticationManager authenticationManager) {
            JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);
            filter.setAuthenticationManager(authenticationManager);
            return filter;
        }

        @Test
        @DisplayName("로그인 성공")
        void testLoginAuthentication() throws Exception {
            // given: 올바른 로그인 요청 데이터 생성
            LoginRequestDto loginRequestDto = new LoginRequestDto();
            loginRequestDto.setUsername("testuser");
            loginRequestDto.setPassword("Password!@3");
            MockHttpServletRequest request = createRequest(loginRequestDto);
            MockHttpServletResponse response = new MockHttpServletResponse();

            // AuthenticationManager 모의 설정: 성공적인 인증 결과 반환
            AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
            UsernamePasswordAuthenticationToken expectedToken =
                new UsernamePasswordAuthenticationToken("testuser", "Password!@3", new ArrayList<>());
            when(authenticationManager.authenticate(any())).thenReturn(expectedToken);

            JwtAuthenticationFilter filter = createFilter(authenticationManager);

            // when: attemptAuthentication 호출
            Authentication authResult = filter.attemptAuthentication(request, response);

            // then: 반환된 Authentication 객체 검증
            assertNotNull(authResult);
            assertEquals("testuser", authResult.getPrincipal());

            // authenticate 메서드가 올바른 토큰으로 호출되었는지 검증
            verify(authenticationManager).authenticate(argThat(authentication -> {
                if (authentication instanceof UsernamePasswordAuthenticationToken) {
                    UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
                    return "testuser".equals(token.getPrincipal()) &&
                        "Password!@3".equals(token.getCredentials());
                }
                return false;
            }));
        }

        @Test
        @DisplayName("로그인 실패 - 잘못된 자격 증명")
        void testLoginAuthenticationFail() throws Exception {
            // given: 잘못된 비밀번호를 사용하는 로그인 요청 데이터 생성
            LoginRequestDto loginRequestDto = new LoginRequestDto();
            loginRequestDto.setUsername("testuser");
            loginRequestDto.setPassword("WrongPassword");
            MockHttpServletRequest request = createRequest(loginRequestDto);
            MockHttpServletResponse response = new MockHttpServletResponse();

            // AuthenticationManager 모의 설정: 인증 시 BadCredentialsException 발생
            AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
            when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

            JwtAuthenticationFilter filter = createFilter(authenticationManager);

            // when & then: attemptAuthentication 호출 시 예외 발생 검증
            Exception exception = assertThrows(BadCredentialsException.class, () -> {
                filter.attemptAuthentication(request, response);
            });
            assertEquals("Bad credentials", exception.getMessage());

            // authenticate 메서드 호출 여부 검증
            verify(authenticationManager).authenticate(any());
        }
    }

    @Nested
    @DisplayName("사용자 정보 가져오기 테스트")
    class GetUserInfoTests {
        // 성공 케이스: 인증된 사용자의 정보를 반환
        @Test
        @DisplayName("현재 사용자 정보 조회 - 성공")
        void testGetCurrentUserSuccess() throws Exception {
            // 테스트용 User 객체 생성
            User user = new User();
            user.setUsername("testuser");
            user.setPassword("Password!@3");
            user.setEmail("test@example.com");
            user.setRole(UserRole.CUSTOMER);
            user.setIsPublic(IsPublic.PUBLIC);

            // 커스텀 UserDetails 객체 생성 (UserDetailsImpl가 User 정보를 포함하도록 구현)
            UserDetailsImpl userDetails = new UserDetailsImpl(user);

            // Authentication 객체 생성: principal에 userDetails를 담음
            UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            mockMvc.perform(get("/api/users/me")
                    .with(authentication(authToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
        }

        // 실패 케이스: 인증정보가 없는 경우 (보통 Security 설정에 따라 401 Unauthorized 반환)
        @Test
        @DisplayName("현재 사용자 정보 조회 - 인증 실패")
        void testGetCurrentUserFailure() throws Exception {
            // FakeAuthenticationPrincipalResolver를 제거한 별도의 MockMvc 인스턴스 생성
            MockMvc mockMvcNoAuth = MockMvcBuilders.standaloneSetup(userController).build();

            // 인증정보가 없으므로, @AuthenticationPrincipal이 null이 됩니다.
            // 컨트롤러에서는 userDetails.getUser() 호출 시 NullPointerException이 발생할 수 있는데,
            // 실제 애플리케이션에서는 Spring Security가 미인증 요청을 가로채고 401을 반환하도록 설정되어야 합니다.
            // 여기서는 단순히 예외 발생을 검증할 수 있습니다.
            assertThrows(Exception.class, () -> {
                mockMvcNoAuth.perform(get("api/users/me")).andReturn();
            });
        }
    }

    @Nested
    @DisplayName("회원 정보 수정 테스트")
    class UpdateUserTests {

        // UserDto 객체 생성 헬퍼 메서드
        private UserDto createUserDto(String username, String password, String email) {
            UserDto userDto = new UserDto();
            userDto.setUsername(username);
            userDto.setPassword(password);
            userDto.setEmail(email);
            return userDto;
        }

        // 업데이트된 User 객체 생성 헬퍼 메서드
        private User createUpdatedUser(String username, String email) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            return user;
        }

        // PUT 요청 실행 헬퍼 메서드
        private ResultActions performUpdate(String username, UserDto userDto) throws Exception {
            return mockMvc.perform(put("/api/users/" + username)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)));
        }

        @Test
        @DisplayName("회원 정보 수정 성공")
        void testUpdateUserSuccess() throws Exception {
            String username = "testuser";
            String password = "Password!@3";
            String email = "newemail@example.com";

            UserDto userDto = createUserDto(username, password, email);
            User updatedUser = createUpdatedUser(username, email);
            lenient().when(userService.updateUser(eq(username), any(UserDto.class))).thenReturn(updatedUser);

            performUpdate(username, userDto)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
        }

        @Test
        @DisplayName("회원 정보 수정 실패 - 유효하지 않은 비밀번호")
        void testUpdateUserFail() throws Exception {
            String username = "testuser";
            String password = "ps";
            String email = "newemail@example.com";

            UserDto userDto = createUserDto(username, password, email);
            User updatedUser = createUpdatedUser(username, email);
            lenient().when(userService.updateUser(eq(username), any(UserDto.class))).thenReturn(updatedUser);

            performUpdate(username, userDto)
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("회원 비밀번호 수정 테스트")
    class ChangePasswordTests {

        // UserDto 객체 생성 헬퍼 메서드
        private PasswordChangeDto createPasswordChangeDto(String oldPassword, String newPassword) {
            PasswordChangeDto passwordChangeDto = new PasswordChangeDto();
            passwordChangeDto.setOldPassword(oldPassword);
            passwordChangeDto.setNewPassword(newPassword);
            return passwordChangeDto;
        }

        // 업데이트된 User 객체 생성 헬퍼 메서드
        private User createUpdatedUser(String username, String password) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            return user;
        }

        @Test
        @DisplayName("회원 비밀번호 변경 성공")
        void testChangePasswordSuccess() throws Exception {
            String username = "testuser";
            PasswordChangeDto passwordChangeDto = createPasswordChangeDto("Password!@3",
                "Password!@4");

            User updatedUser = createUpdatedUser(username, "Password!@4");
            lenient().when(userService.changePassword(eq(username), any(PasswordChangeDto.class))).thenReturn(updatedUser);

            mockMvc.perform(put("/api/users/" + username + "/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(passwordChangeDto)))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("회원 비밀번호 변경 실패 - 같은 비번")
        void testChangePasswordFail() throws Exception {
            String username = "testuser";
            PasswordChangeDto passwordChangeDto = createPasswordChangeDto("Password!@3",
                "Password!@3");

            doThrow(new DuplicateResourceException("새 비밀번호는 이전 비밀번호와 달라야 합니다.")).when(userService)
                .changePassword(eq(username), any(PasswordChangeDto.class));

            // when & then = 예외처리 검출
            Exception exception = assertThrows(Exception.class, () -> {
                mockMvc.perform(put("/api/users/" + username + "/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordChangeDto)))
                    .andReturn();
            });

            // 발생한 예외는 보통 NestedServletException으로 wrapping 되어 있으므로 unwrap하여 root cause를 확인
            Throwable rootCause = exception;
            while(rootCause.getCause() != null && !(rootCause instanceof DuplicateResourceException)) {
                rootCause = rootCause.getCause();
            }

            assertTrue(rootCause instanceof DuplicateResourceException);
        }
    }

    @Nested
    @DisplayName("로그아웃 테스트")
    class LogoutTests {
        @Test
        @DisplayName("로그아웃 성공")
        void testLogoutSuccess() throws Exception {
            String token = "Bearer jwt-token";

            mockMvc.perform(post("/api/users/logout")
                    .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().string("You've been logged out successfully."));
        }

        @Test
        @DisplayName("로그아웃 실패 - Authorization 헤더 누락")
        void testLogoutFailureMissingToken() throws Exception {
            // Authorization 헤더 없이 요청을 보내면 인증 실패(401) 상태 코드가 반환되는지 검증
            mockMvc.perform(post("/api/users/logout"))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("로그아웃 실패 - 잘못된 토큰 형식")
        void testLogoutFailureInvalidToken() throws Exception {
            // "Bearer " 형식을 갖추지 않거나 유효하지 않은 토큰으로 요청을 보내면 실패하도록 가정
            String invalidToken = "InvalidTokenFormat";

            mockMvc.perform(post("/api/users/logout")
                    .header("Authorization", invalidToken))
                .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("사용자 삭제 테스트")
    class DeleteUserTests {
        @Test
        @DisplayName("사용자 삭제 성공")
        void testDeleteUserSuccess() throws Exception {
            String username = "testuser";

            mockMvc.perform(delete("/api/users/" + username))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));
        }

        @Test
        @DisplayName("유저 삭제 실패 - 존재하지 않는 사용자")
        void testDeleteUserFailure() throws Exception {
            String username = "nonexistentUser";

            // userService.deleteUser(username) 호출 시 ResourceNotFoundException 발생하도록 Stub 설정
            doThrow(new ResourceNotFoundException("User not found"))
                .when(userService).deleteUser(eq(username));

            // when & then = 예외처리 검출
            Exception exception = assertThrows(Exception.class, () -> {
                mockMvc.perform(delete("/api/users/" + username))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("User not found"));
            });

            // 발생한 예외는 보통 NestedServletException으로 wrapping 되어 있으므로 unwrap하여 root cause를 확인
            Throwable rootCause = exception;
            while(rootCause.getCause() != null && !(rootCause instanceof ResourceNotFoundException)) {
                rootCause = rootCause.getCause();
            }
            assertTrue(rootCause instanceof ResourceNotFoundException);
        }
    }



}
