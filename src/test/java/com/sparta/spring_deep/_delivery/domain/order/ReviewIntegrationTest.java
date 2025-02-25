package com.sparta.spring_deep._delivery.domain.order;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.spring_deep._delivery.domain.address.repository.AddressRepository;
import com.sparta.spring_deep._delivery.domain.restaurant.Restaurant;
import com.sparta.spring_deep._delivery.domain.restaurant.RestaurantRepository;
import com.sparta.spring_deep._delivery.domain.review.ReviewRequestDto;
import com.sparta.spring_deep._delivery.domain.review.ReviewResponseDto;
import com.sparta.spring_deep._delivery.domain.user.entity.IsPublic;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import com.sparta.spring_deep._delivery.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ReviewIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private AddressRepository addressRepository;


    // 소유자, 고객, 음식점, 주문 객체를 테스트 전 생성합니다.
    private User owner;
    private User customer;
    private Restaurant restaurant;
    private Order order;

    @BeforeEach
    void setUp() {
        // 가게 사장님
        owner = User.builder()
            .username("owner")
            .password("1234")
            .email("owner@test.com")
            .role(UserRole.OWNER)
            .isPublic(IsPublic.PUBLIC)
            .build();
        owner = userRepository.save(owner);

        // 고객
        customer = User.builder()
            .username("customer")
            .password("1234")
            .email("customer@test.com")
            .role(UserRole.CUSTOMER)
            .isPublic(IsPublic.PUBLIC)
            .build();
        customer = userRepository.save(customer);


    }

    // 인증 객체 생성: 테스트 시 API 호출에 사용합니다.
    private UsernamePasswordAuthenticationToken getAuth(User user) {
        return new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
    }

    // 리뷰 생성 API 호출 헬퍼 메서드
    private ReviewResponseDto createReview() throws Exception {
        // ReviewRequestDto 생성: 리뷰 작성에 필요한 orderId, rating, comment를 설정합니다.
        ReviewRequestDto requestDto = new ReviewRequestDto();
        ReflectionTestUtils.setField(requestDto, "orderId", order.getId().toString());
        ReflectionTestUtils.setField(requestDto, "rating", 5);
        ReflectionTestUtils.setField(requestDto, "comment", "Excellent service");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // POST /api/reviews 엔드포인트 호출 (고객 인증)
        MvcResult result = mockMvc.perform(post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                .with(authentication(getAuth(customer))))
            .andExpect(status().isCreated())
            .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        return objectMapper.readValue(responseContent, ReviewResponseDto.class);
    }

    // 1. 리뷰 생성 및 상세 조회 테스트
    @Test
    void testCreateAndGetReview() throws Exception {
        // 리뷰 생성
        ReviewResponseDto createdReview = createReview();
        String reviewId = createdReview.getId().toString();

        // GET /api/reviews/{reviewId} 엔드포인트를 호출하여 리뷰 상세 정보를 검증
        mockMvc.perform(get("/api/reviews/{reviewId}", reviewId)
                .with(authentication(getAuth(customer))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(reviewId))
            .andExpect(jsonPath("$.rating").value(5))
            .andExpect(jsonPath("$.comment").value("Excellent service"));
    }

    // 2. 리뷰 수정 테스트
    @Test
    void testUpdateReview() throws Exception {
        // 리뷰 생성
        ReviewResponseDto createdReview = createReview();
        String reviewId = createdReview.getId().toString();

        // 수정할 내용이 담긴 ReviewRequestDto 생성 (리뷰 수정 시 orderId는 필요 없을 수 있음)
        ReviewRequestDto updateDto = new ReviewRequestDto();
        ReflectionTestUtils.setField(updateDto, "rating", 4);
        ReflectionTestUtils.setField(updateDto, "comment", "Good service");

        String updateJson = objectMapper.writeValueAsString(updateDto);

        // PUT /api/reviews/{reviewId} 엔드포인트 호출하여 리뷰 수정 요청 (고객 인증)
        mockMvc.perform(put("/api/reviews/{reviewId}", reviewId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson)
                .with(authentication(getAuth(customer))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(reviewId))
            .andExpect(jsonPath("$.rating").value(4))
            .andExpect(jsonPath("$.comment").value("Good service"));
    }

    // 3. 리뷰 삭제 테스트
    @Test
    void testDeleteReview() throws Exception {
        // 리뷰 생성
        ReviewResponseDto createdReview = createReview();
        String reviewId = createdReview.getId().toString();

        // DELETE /api/reviews/{reviewId} 호출 (고객 인증)
        mockMvc.perform(delete("/api/reviews/{reviewId}", reviewId)
                .with(authentication(getAuth(customer))))
            .andExpect(status().isOk());
    }

    // 4. 특정 음식점의 리뷰 검색 테스트
    @Test
    void testSearchReviewsForRestaurant() throws Exception {
        // 리뷰 2건 생성
        createReview();
        createReview();

        // GET /api/reviews/{restaurantId}/search 호출하여 해당 음식점의 리뷰 목록을 조회
        mockMvc.perform(get("/api/reviews/{restaurantId}/search", restaurant.getId().toString())
                .with(authentication(getAuth(customer))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());
    }
}
