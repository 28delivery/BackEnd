package com.sparta.spring_deep._delivery.domain.order;

import static com.sparta.spring_deep._delivery.testutil.TestEntityCreateTools.createAddress;
import static com.sparta.spring_deep._delivery.testutil.TestEntityCreateTools.createMenu;
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
import com.sparta.spring_deep._delivery.domain.menu.Menu;
import com.sparta.spring_deep._delivery.domain.menu.MenuRepository;
import com.sparta.spring_deep._delivery.domain.order.orderDetails.OrderDetailsRequestDto;
import com.sparta.spring_deep._delivery.domain.order.orderDetails.OrderDetailsResponseDto;
import com.sparta.spring_deep._delivery.domain.order.orderItem.OrderItemDto;
import com.sparta.spring_deep._delivery.domain.restaurant.Restaurant;
import com.sparta.spring_deep._delivery.domain.restaurant.RestaurantRepository;
import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddress;
import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddressRepository;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import com.sparta.spring_deep._delivery.domain.user.repository.UserRepository;
import com.sparta.spring_deep._delivery.testutil.TestEntityCreateTools;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderIntegrationTest {

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
    private MenuRepository menuRepository;

    private User owner;
    private User customer;
    private Address address;
    private Restaurant restaurant;
    private RestaurantAddress restaurantAddress;
    private Menu menu;
    @Autowired
    private RestaurantAddressRepository restaurantAddressRepository;

    @BeforeEach
    void setUp() {
        // 1. 소유자와 고객 생성
        owner = createUser("owner", UserRole.OWNER);
        owner = userRepository.save(owner);

        customer = createUser("customer", UserRole.CUSTOMER);
        customer = userRepository.save(customer);

        // 2. 주소 생성
        address = createAddress(customer, "test address", "test");
        address = addressRepository.save(address);

        // 가게 주소
        restaurantAddress = createRestaurantAddress(owner.getUsername());
        restaurantAddress = restaurantAddressRepository.save(restaurantAddress);

        // 가게
        restaurant = createRestaurant(owner, restaurantAddress, "불맛짜장", "010-2222-3333");
        restaurant = restaurantRepository.save(restaurant);

        // 4. 메뉴 생성
        menu = createMenu(customer, restaurant, "test menu", "test description", 5000.0);
        menu = menuRepository.save(menu);
    }

    // 주문 생성 요청을 위한 헬퍼 메서드
    private OrderDetailsResponseDto createOrder() throws Exception {
        // 주문 요청 Dto

        // 주문 항목 생성 (menuId는 문자열)
        OrderItemDto orderItemDto = new OrderItemDto(menu.getId().toString(), 1);
        OrderDetailsRequestDto requestDto = new OrderDetailsRequestDto(
            restaurant.getId().toString(), address.getId().toString(), "testRequest",
            List.of(orderItemDto));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                .with(authentication(getAuth(customer))))
            .andExpect(status().isCreated())
            .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        return objectMapper.readValue(responseContent, OrderDetailsResponseDto.class);
    }

    // 주문 생성 및 상세 조회 테스트
    @Test
    void testCreateAndGetOrderDetails() throws Exception {

        OrderDetailsResponseDto responseDto = createOrder();

        mockMvc.perform(get("/api/orders/{orderId}", responseDto.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(authentication(getAuth(customer))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerId").value(customer.getUsername()))
            .andExpect(jsonPath("$.restaurantId").value(restaurant.getId().toString()))
            .andExpect(jsonPath("$.addressId").value(address.getId().toString()))
            .andExpect(jsonPath("$.request").value("testRequest"));
    }

    // 주문 상태 변경 테스트 (예: PENDING → PREPARING)
    @Test
    void testUpdateOrderStatus() throws Exception {
        OrderDetailsResponseDto responseDto = createOrder();

        UUID orderId = UUID.fromString(responseDto.getId());

        mockMvc.perform(put("/api/orders/{orderId}/status", orderId)
                .param("status", OrderStatusEnum.CONFIRMED.toString())
                .with(authentication(getAuth(owner))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(orderId.toString()))
            .andExpect(jsonPath("$.status").value(OrderStatusEnum.CONFIRMED.toString()));

    }

    // 주문 취소 테스트
    @Test
    void testCancelOrder() throws Exception {
        Order createOrder = TestEntityCreateTools.createOrder(customer, restaurant, address,
            20000.0, "testRequest");
        orderRepository.save(createOrder); // 왐마 슈우우웃 와우우우우 아래도 똑같이 save 복붙 성공! ㅋㅋㅋㅋㅋㅋㅋㅋ아싸 최고십니다...
        String orderId = createOrder.getId().toString();

        mockMvc.perform(put("/api/orders/{orderId}/cancel", orderId)
                .with(authentication(getAuth(customer))))
            .andExpect(status().isOk());
    }

    // 주문 삭제 테스트
    @Test
    void testDeleteOrder() throws Exception {
        Order createOrder = TestEntityCreateTools.createOrder(customer, restaurant, address,
            20000.0, "testRequest");
        orderRepository.save(createOrder);
        String orderId = createOrder.getId().toString();

        mockMvc.perform(delete("/api/orders/{orderId}", orderId)
                .with(authentication(getAuth(customer))))
            .andExpect(status().isOk());
    }

    // 내 주문 내역 검색 테스트
    @Test
    void testSearchMyOrders() throws Exception {
        // 고객 주문 2건 생성
        Order createOrder1 = TestEntityCreateTools.createOrder(customer, restaurant, address,
            20000.0, "testRequest");
        Order createOrder2 = TestEntityCreateTools.createOrder(customer, restaurant, address,
            10000.0, "testRequest");
        orderRepository.save(createOrder1);
        orderRepository.save(createOrder2);

        mockMvc.perform(get("/api/orders/me/search")
                .with(authentication(getAuth(customer))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());
    }

    // 실시간 주문 확인(폴링) 테스트
    @Test
    void testPollingOrders() throws Exception {
        // 주문이 존재하도록 하나 생성
        Order createOrder = TestEntityCreateTools.createOrder(customer, restaurant, address,
            10000.0, "testRequest");
        orderRepository.save(createOrder);

        mockMvc.perform(get("/api/orders/polling")
                .with(authentication(getAuth(customer))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());
    }
}
