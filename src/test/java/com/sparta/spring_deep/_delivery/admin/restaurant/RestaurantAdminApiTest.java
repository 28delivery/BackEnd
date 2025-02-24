package com.sparta.spring_deep._delivery.admin.restaurant;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class RestaurantAdminApiTest {

    @Autowired
    private MockMvc mockMvc;

    // ======================
    // GET /admin/restaurants/{restaurantId}
    // ======================
    static Stream<Arguments> getRestaurantArguments() {
        return Stream.of(
            // 유효한 UUID -> 200 OK
            arguments("123e4567-e89b-12d3-a456-426614174000", 200),
            arguments("111e4567-e89b-12d3-a456-426614174111", 200),
            // UUID 형식 오류 -> 400 Bad Request
            arguments("not-a-uuid", 400),
            arguments("", 400),
            arguments("222e4567-e89b-12d3-a456-426614174222", 200)
        );
    }

    // ======================
    // POST /admin/restaurants (음식점 생성)
    // ======================
    static Stream<Arguments> addRestaurantArguments() {
        return Stream.of(
            // 올바른 요청 -> 201 CREATED
            arguments(
                "{ \"ownerId\": \"owner1\", \"name\": \"Restaurant A\", \"category\": \"KOREAN\", \"roadAddr\": \"Seoul\", \"detailAddr\": \"Gangnam\", \"phone\": \"010-1234-5678\" }",
                201),
            arguments(
                "{ \"ownerId\": \"owner2\", \"name\": \"Restaurant B\", \"category\": \"JAPANESE\", \"roadAddr\": \"Tokyo\", \"detailAddr\": \"Shibuya\", \"phone\": \"03-1234-5678\" }",
                201),
            // 필수 필드 누락(예: ownerId 없음) -> 400 Bad Request
            arguments(
                "{ \"name\": \"Restaurant C\", \"category\": \"CHINESE\", \"roadAddr\": \"Beijing\", \"detailAddr\": \"Haidian\", \"phone\": \"010-1111-2222\" }",
                400),
            // 필수 필드 누락(예: name 없음) -> 400 Bad Request
            arguments(
                "{ \"ownerId\": \"owner1\", \"category\": \"KOREAN\", \"roadAddr\": \"Seoul\", \"detailAddr\": \"Gangnam\", \"phone\": \"010-1234-5678\" }",
                400),
            // 잘못된 enum 값 -> 400 Bad Request
            arguments(
                "{ \"ownerId\": \"owner1\", \"name\": \"Restaurant D\", \"category\": \"INVALID\", \"roadAddr\": \"Seoul\", \"detailAddr\": \"Gangnam\", \"phone\": \"010-1234-5678\" }",
                400)
        );
    }

    // ======================
    // PUT /admin/restaurants/{restaurantId} (음식점 정보 수정)
    // ======================
    static Stream<Arguments> updateRestaurantArguments() {
        return Stream.of(
            // 올바른 수정 요청 -> 200 OK
            arguments("123e4567-e89b-12d3-a456-426614174000",
                "{ \"name\": \"Updated Restaurant A\", \"category\": \"KOREAN\", \"roadAddr\": \"Seoul\", \"detailAddr\": \"Gangnam Updated\", \"phone\": \"010-9876-5432\" }",
                200),
            arguments("111e4567-e89b-12d3-a456-426614174111",
                "{ \"name\": \"Updated Restaurant B\", \"category\": \"JAPANESE\", \"roadAddr\": \"Tokyo\", \"detailAddr\": \"Shibuya Updated\", \"phone\": \"03-9876-5432\" }",
                200),
            // 필수 필드 누락(예: name 누락) -> 400 Bad Request
            arguments("123e4567-e89b-12d3-a456-426614174000",
                "{ \"category\": \"KOREAN\", \"roadAddr\": \"Seoul\", \"detailAddr\": \"Gangnam\", \"phone\": \"010-9876-5432\" }",
                400),
            // 잘못된 enum 값 -> 400 Bad Request
            arguments("222e4567-e89b-12d3-a456-426614174222",
                "{ \"name\": \"Updated Restaurant D\", \"category\": \"INVALID\", \"roadAddr\": \"Seoul\", \"detailAddr\": \"Gangnam\", \"phone\": \"010-9876-5432\" }",
                400),
            // 잘못된 restaurantId (UUID 형식 오류) -> 400 Bad Request
            arguments("not-a-uuid",
                "{ \"name\": \"Updated Restaurant E\", \"category\": \"CHINESE\", \"roadAddr\": \"Beijing\", \"detailAddr\": \"Chaoyang\", \"phone\": \"010-2222-3333\" }",
                400)
        );
    }

    // ======================
    // DELETE /admin/restaurants/{restaurantId} (음식점 삭제)
    // ======================
    static Stream<Arguments> deleteRestaurantArguments() {
        return Stream.of(
            arguments("123e4567-e89b-12d3-a456-426614174000", 200),
            arguments("111e4567-e89b-12d3-a456-426614174111", 200),
            arguments("not-a-uuid", 400),
            arguments("", 400),
            arguments("222e4567-e89b-12d3-a456-426614174222", 200)
        );
    }

    // ======================
    // GET /admin/restaurants/search (음식점 검색)
    // ======================
    static Stream<Arguments> searchRestaurantArguments() {
        return Stream.of(
            arguments("/admin/restaurants/search?name=Restaurant", 200),
            arguments("/admin/restaurants/search?category=KOREAN", 200),
            arguments("/admin/restaurants/search?roadAddr=Seoul", 200),
            // 유효하지 않은 검색 조건이더라도 검색 API는 빈 결과(Page)와 함께 200 OK 반환
            arguments("/admin/restaurants/search?category=INVALID", 200),
            arguments("/admin/restaurants/search", 200)
        );
    }

    @ParameterizedTest
    @MethodSource("getRestaurantArguments")
    void testGetRestaurant(String restaurantId, int expectedStatus) throws Exception {
        mockMvc.perform(get("/admin/restaurants/{restaurantId}", restaurantId))
            .andExpect(status().is(expectedStatus));
    }

    @ParameterizedTest
    @MethodSource("addRestaurantArguments")
    void testAddRestaurant(String requestBody, int expectedStatus) throws Exception {
        mockMvc.perform(post("/admin/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
            .andExpect(status().is(expectedStatus));
    }

    @ParameterizedTest
    @MethodSource("updateRestaurantArguments")
    void testUpdateRestaurant(String restaurantId, String requestBody, int expectedStatus)
        throws Exception {
        mockMvc.perform(put("/admin/restaurants/{restaurantId}", restaurantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
            .andExpect(status().is(expectedStatus));
    }

    @ParameterizedTest
    @MethodSource("deleteRestaurantArguments")
    void testDeleteRestaurant(String restaurantId, int expectedStatus) throws Exception {
        mockMvc.perform(delete("/admin/restaurants/{restaurantId}", restaurantId)
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
            .andExpect(status().is(expectedStatus));
    }

    @ParameterizedTest
    @MethodSource("searchRestaurantArguments")
    void testSearchRestaurant(String url, int expectedStatus) throws Exception {
        mockMvc.perform(get(url))
            .andExpect(status().is(expectedStatus));
    }
}
