package com.sparta.spring_deep._delivery.domain.user;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class UserApiTest {

    @Autowired
    private MockMvc mockMvc;

    // 테스트 케이스별 JSON 요청과 예상 HTTP 상태 코드를 제공하는 메서드
    static Stream<Arguments> signupArguments() {
        return Stream.of(
            // 유효한 요청: 사용자 등록 성공 -> 201 CREATED
            arguments(
                "{ \"username\": \"user1\", \"password\": \"User1234!\", \"email\": \"user1@test.com\", \"role\": \"CUSTOMER\", \"isPublic\": \"PUBLIC\" }",
                201),
            arguments(
                "{ \"username\": \"user2\", \"password\": \"User1234!\", \"email\": \"user2@test.com\", \"role\": \"CUSTOMER\", \"isPublic\": \"PUBLIC\" }",
                201),
            arguments(
                "{ \"username\": \"admin\", \"password\": \"User1234!\", \"email\": \"admin@test.com\", \"role\": \"ADMIN\", \"isPublic\": \"PUBLIC\" }",
                201),
            arguments(
                "{ \"username\": \"owner1\", \"password\": \"User1234!\", \"email\": \"owner1@test.com\", \"role\": \"OWNER\", \"isPublic\": \"PUBLIC\" }",
                201),
            arguments(
                "{ \"username\": \"owner2\", \"password\": \"User1234!\", \"email\": \"owner2@test.com\", \"role\": \"OWNER\", \"isPublic\": \"PUBLIC\" }",
                201),
            // 잘못된 요청: 패스워드 형식 오류 (특수문자 누락) -> 400 BAD_REQUEST
            arguments(
                "{ \"username\": \"testuser\", \"password\": \"User1234\", \"email\": \"testuser@test.com\", \"role\": \"CUSTOMER\", \"isPublic\": \"PUBLIC\" }",
                400),
            // 잘못된 요청: 이메일 형식 오류 -> 400 BAD_REQUEST
            arguments(
                "{ \"username\": \"testuser\", \"password\": \"User1234!\", \"email\": \"testusertest.com\", \"role\": \"CUSTOMER\", \"isPublic\": \"PUBLIC\" }",
                400),
            // 유효한 요청: 사용자 등록 성공 -> 201 CREATED
            arguments(
                "{ \"username\": \"testuser\", \"password\": \"User1234!\", \"email\": \"testuser@test.com\", \"role\": \"CUSTOMER\", \"isPublic\": \"PUBLIC\" }",
                201),
            // 잘못된 요청: 중복 등록 (이미 등록된 사용자) -> 400 BAD_REQUEST
            arguments(
                "{ \"username\": \"testuser\", \"password\": \"User1234!\", \"email\": \"testuser@test.com\", \"role\": \"CUSTOMER\", \"isPublic\": \"PUBLIC\" }",
                409)
        );
    }

    @ParameterizedTest
    @MethodSource("signupArguments")
    void testUserSignup(String requestBody, int expectedStatus) throws Exception {
        mockMvc.perform(post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().is(expectedStatus));
    }

}
