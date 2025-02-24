package com.sparta.spring_deep._delivery.admin.user;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import com.sparta.spring_deep._delivery.domain.user.entity.IsPublic;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import com.sparta.spring_deep._delivery.domain.user.repository.UserRepository;
import com.sparta.spring_deep._delivery.testutil.TestEntityCreateTools;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserAdminIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAdminService userAdminService;

    private User customer;
    private User admin;

    private Authentication getAuth(User user) {
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        return new UsernamePasswordAuthenticationToken(userDetails, null,
            userDetails.getAuthorities());
    }

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성
        customer = User.builder()
            .username("userTest")
            .password("userTest1234*")
            .email("testuser@test.com")
            .role(UserRole.CUSTOMER)
            .isPublic(IsPublic.PUBLIC)
            .build();

        // 감사 정보 설정 (실제 환경에서는 AuditorAware에 의해 자동 설정됨)
        customer.setCreatedBy("system");

        // 사용자 저장 및 초기화
        customer = userRepository.save(customer);

        // 감사 정보 설정 (실제 환경에서는 AuditorAware에 의해 자동 설정됨)
        customer.setCreatedBy("system");

        // 사용자 저장 및 초기화
        customer = userRepository.save(customer);

        // 테스트용 사용자 생성
        admin = TestEntityCreateTools.createUser("admin", UserRole.ADMIN);
        admin = userRepository.save(admin);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("사용자 검색")
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void searchUsers() throws Exception {

        ResultActions result = mockMvc.perform(get("/admin/users/search")
            .with(authentication(getAuth(admin)))
            .contentType(MediaType.APPLICATION_JSON));

        result.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    void createUser() throws Exception {
        UserCreateRequestDto requestDto = new UserCreateRequestDto();
        requestDto.setUsername("test");
        requestDto.setPassword("test1234*");
        requestDto.setEmail("test@test.com");
        requestDto.setRole(UserRole.CUSTOMER);
        requestDto.setIsPublic(IsPublic.PUBLIC);

        // ObjectMapper 복사 및 설정
        ObjectMapper testMapper = objectMapper.copy();
        testMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        MvcResult mvcResult = mockMvc.perform(
                post("/admin/users")
                    .with(authentication(getAuth(admin)))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto))
            )
            .andExpect(status().isOk())
            .andReturn();

        // 응답 본문 검증
        String responseBody = mvcResult.getResponse().getContentAsString();
        System.out.println("ResponseBody: " + responseBody);
        UserAdminResponseDto responseDto = objectMapper
            .readValue(responseBody, UserAdminResponseDto.class);

        // JSON을 Map으로 변환하여 확인
        Map<String, Object> responseMap = objectMapper
            .readValue(responseBody, new TypeReference<>() {
            });
        System.out.println("🔍 ResponseMap: " + responseMap);

        // 생성된 사용자 검증
        User savedUser = userRepository.findByUsername("test")
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        assertEquals("test", savedUser.getUsername());
        assertEquals("test@test.com", savedUser.getEmail());
        assertEquals(UserRole.CUSTOMER, savedUser.getRole());
        assertEquals(IsPublic.PUBLIC, savedUser.getIsPublic());
        // 추가 필드 검증
        assertFalse(responseDto.getIsDeleted());
        assertNotNull(responseDto.getCreatedAt());
        assertEquals("admin", responseDto.getCreatedBy());  // 생성자 확인
        assertNull(responseDto.getDeletedAt());
        assertNull(responseDto.getDeletedBy());
    }

}
