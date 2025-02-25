package com.sparta.spring_deep._delivery.domain.review;

import static com.sparta.spring_deep._delivery.testutil.TestEntityCreateTools.createAddress;
import static com.sparta.spring_deep._delivery.testutil.TestEntityCreateTools.createOrder;
import static com.sparta.spring_deep._delivery.testutil.TestEntityCreateTools.createRestaurant;
import static com.sparta.spring_deep._delivery.testutil.TestEntityCreateTools.createRestaurantAddress;
import static com.sparta.spring_deep._delivery.testutil.TestEntityCreateTools.createUser;
import static com.sparta.spring_deep._delivery.testutil.TestEntityCreateTools.getAuth;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.spring_deep._delivery.domain.address.entity.Address;
import com.sparta.spring_deep._delivery.domain.address.repository.AddressRepository;
import com.sparta.spring_deep._delivery.domain.order.Order;
import com.sparta.spring_deep._delivery.domain.order.OrderRepository;
import com.sparta.spring_deep._delivery.domain.order.OrderStatusEnum;
import com.sparta.spring_deep._delivery.domain.restaurant.Restaurant;
import com.sparta.spring_deep._delivery.domain.restaurant.RestaurantRepository;
import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddress;
import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddressRepository;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import com.sparta.spring_deep._delivery.domain.user.repository.UserRepository;
import com.sparta.spring_deep._delivery.testutil.TestEntityCreateTools;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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

    @Autowired
    private ReviewRepository reviewRepository;


    // 소유자, 고객, 음식점, 주문 객체를 테스트 전 생성합니다.
    private User owner;
    private User customer;
    private User admin;
    private Restaurant restaurant;
    private Address address;
    private RestaurantAddress restaurantAddress;
    private Order order;
    @Autowired
    private RestaurantAddressRepository restaurantAddressRepository;


    @BeforeEach
    void setUp() {
        // 어드민
        admin = createUser("admin", UserRole.ADMIN);
        admin = userRepository.save(admin);

        // 가게 사장님
        owner = createUser("owner", UserRole.OWNER);
        owner = userRepository.save(owner);

        // 고객
        customer = createUser("customer", UserRole.CUSTOMER);
        customer = userRepository.save(customer);

        // 가게 주소
        restaurantAddress = createRestaurantAddress(owner.getUsername());
        restaurantAddress = restaurantAddressRepository.save(restaurantAddress);

        // 가게
        restaurant = createRestaurant(owner, restaurantAddress, "불맛짜장", "010-2222-3333");
        restaurant = restaurantRepository.save(restaurant);

        // 주소
        address = createAddress(customer, "충남 천안시", "천안 집");
        address = addressRepository.save(address);

        // 주문
        order = createOrder(customer, restaurant, address, 20000.0, "밥 적게 주세요");
        order.updateOrderStatus(admin, OrderStatusEnum.DELIVERED);
        order = orderRepository.save(order);


    }

    // 1. 리뷰 생성 테스트
    @Test
    void testCreateReview() throws Exception {
        // 리뷰 생성
        ReviewRequestDto requestDto = new ReviewRequestDto(order.getId().toString(), "testComment1",
            5);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                .with(authentication(getAuth(customer))))
            .andExpect(status().isCreated())
            .andReturn();

    }

    // 2. 리뷰 수정 테스트
    @Test
    void testUpdateReview() throws Exception {
        // 리뷰 생성
        Review createdReview = TestEntityCreateTools.createReview(order, customer, 5,
            "testComment1");
        reviewRepository.save(createdReview);
        String reviewId = createdReview.getId().toString();

        // 수정할 내용이 담긴 ReviewRequestDto 생성
        ReviewRequestDto updateDto = new ReviewRequestDto();
        ReflectionTestUtils.setField(updateDto, "orderId", order.getId().toString());
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
        Review createdReview = TestEntityCreateTools.createReview(order, customer, 5,
            "testComment1");
        reviewRepository.save(createdReview);
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
        Review review1 = TestEntityCreateTools.createReview(order, customer, 5, "testComment1");
        Review review2 = TestEntityCreateTools.createReview(order, customer, 4, "testComment2");
        reviewRepository.save(review1);
        reviewRepository.save(review2);

        // GET /api/reviews/{restaurantId}/search 호출하여 해당 음식점의 리뷰 목록을 조회
        mockMvc.perform(get("/api/reviews/{restaurantId}/search", restaurant.getId().toString())
                .with(authentication(getAuth(customer))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());
    }
}
