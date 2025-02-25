package com.sparta.spring_deep._delivery.domain.order;

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
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
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
    private Menu menu;

    @BeforeEach
    void setUp() {
        // 1. 소유자와 고객 생성
        owner = new User();
        ReflectionTestUtils.setField(owner, "username", "Owner");
        owner = userRepository.save(owner);

        customer = new User();
        ReflectionTestUtils.setField(customer, "username", "Customer");
        customer = userRepository.save(customer);

        // 2. 주소 생성
        address = new Address();
        // Address 엔티티에 존재하는 필드명을 사용 (예: address, addressName)
        ReflectionTestUtils.setField(address, "address", "test address");
        ReflectionTestUtils.setField(address, "addressName", "test");
        ReflectionTestUtils.setField(address, "user", customer);
        address = addressRepository.save(address);

        // 3. 음식점 생성
        restaurant = new Restaurant();
        ReflectionTestUtils.setField(restaurant, "owner", owner);
        ReflectionTestUtils.setField(restaurant, "name", "레스토랑");
        // category는 enum 타입이므로 enum 상수를 직접 설정
        ReflectionTestUtils.setField(restaurant, "category", Restaurant.CategoryEnum.HANSIK);
        // restaurantAddress 필드가 RestaurantAddress 타입이라면 별도의 객체를 만들어 주입해야 함.
        // 만약 단순 문자열이라면 아래처럼 설정:
        ReflectionTestUtils.setField(restaurant, "restaurantAddress", "서울시");
        ReflectionTestUtils.setField(restaurant, "phone", "02-111-1234");
        restaurant = restaurantRepository.save(restaurant);

        // 4. 메뉴 생성
        menu = new Menu();
        // 메뉴의 경우 음식점 ID를 설정 (문자열이 아니라 UUID 혹은 해당 타입)
        ReflectionTestUtils.setField(menu, "restaurantId", restaurant.getId());
        ReflectionTestUtils.setField(menu, "name", "test menu");
        ReflectionTestUtils.setField(menu, "description", "test description");
        ReflectionTestUtils.setField(menu, "price", BigDecimal.valueOf(5000));
        ReflectionTestUtils.setField(menu, "isHidden", false);
        ReflectionTestUtils.setField(menu, "isDeleted", false);
        menu = menuRepository.save(menu);
    }

    // 인증 객체 생성
    private UsernamePasswordAuthenticationToken getAuth(User user) {
        return new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
    }

    // 주문 생성 요청을 위한 헬퍼 메서드
    private OrderDetailsResponseDto createOrder() throws Exception {
        OrderDetailsRequestDto requestDto = new OrderDetailsRequestDto();
        // DTO에 문자열로 ID를 설정 (getter 내부에서 UUID로 변환)
        ReflectionTestUtils.setField(requestDto, "restaurantId", restaurant.getId().toString());
        ReflectionTestUtils.setField(requestDto, "addressId", address.getId().toString());
        ReflectionTestUtils.setField(requestDto, "request", "testRequest");

        // 주문 항목 생성 (menuId는 문자열)
        OrderItemDto orderItemDto = new OrderItemDto();
        ReflectionTestUtils.setField(orderItemDto, "menuId", menu.getId().toString());
        ReflectionTestUtils.setField(orderItemDto, "quantity", 1);
        ReflectionTestUtils.setField(requestDto, "orderItemDtos", List.of(orderItemDto));

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
        OrderDetailsResponseDto createdOrder = createOrder();
        UUID orderId = UUID.fromString(createdOrder.getId());

        mockMvc.perform(get("/api/orders/{orderId}", orderId)
                .with(authentication(getAuth(customer))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(orderId.toString()))
            .andExpect(jsonPath("$.customerId").value(customer.getUsername()))
            .andExpect(jsonPath("$.restaurantId").value(restaurant.getId().toString()))
            .andExpect(jsonPath("$.addressId").value(address.getId().toString()))
            .andExpect(jsonPath("$.request").value("testRequest"));
    }

    // 주문 상태 변경 테스트 (예: PENDING → PREPARING)
    @Test
    void testUpdateOrderStatus() throws Exception {
        OrderDetailsResponseDto createdOrder = createOrder();
        UUID orderId = UUID.fromString(createdOrder.getId());

        mockMvc.perform(put("/api/orders/{orderId}/status", orderId)
                .param("status", "PREPARING")
                .with(authentication(getAuth(owner))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderId").value(orderId.toString()))
            .andExpect(jsonPath("$.status").value("PREPARING"));
    }

    // 주문 취소 테스트
    @Test
    void testCancelOrder() throws Exception {
        OrderDetailsResponseDto createdOrder = createOrder();
        UUID orderId = UUID.fromString(createdOrder.getId());

        mockMvc.perform(put("/api/orders/{orderId}/cancel", orderId)
                .with(authentication(getAuth(customer))))
            .andExpect(status().isOk());
    }

    // 주문 삭제 테스트
    @Test
    void testDeleteOrder() throws Exception {
        OrderDetailsResponseDto createdOrder = createOrder();
        UUID orderId = UUID.fromString(createdOrder.getId());

        mockMvc.perform(delete("/api/orders/{orderId}", orderId)
                .with(authentication(getAuth(customer))))
            .andExpect(status().isOk());
    }

    // 내 주문 내역 검색 테스트
    @Test
    void testSearchMyOrders() throws Exception {
        // 고객 주문 2건 생성
        createOrder();
        createOrder();

        mockMvc.perform(get("/api/orders/me/search")
                .with(authentication(getAuth(customer))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());
    }

    // 실시간 주문 확인(폴링) 테스트
    @Test
    void testPollingOrders() throws Exception {
        // 주문이 존재하도록 하나 생성
        createOrder();

        mockMvc.perform(get("/api/orders/polling")
                .with(authentication(getAuth(customer))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());
    }
}
