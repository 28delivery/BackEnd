package com.sparta.spring_deep._delivery.domain.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.spring_deep._delivery.admin.ai.AiRepository;
import com.sparta.spring_deep._delivery.domain.restaurant.Restaurant;
import com.sparta.spring_deep._delivery.domain.restaurant.RestaurantRepository;
import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddress;
import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddressRepository;
import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import com.sparta.spring_deep._delivery.domain.user.entity.IsPublic;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import com.sparta.spring_deep._delivery.domain.user.repository.UserRepository;
import com.sparta.spring_deep._delivery.testutil.TestEntityCreateTools;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class MenuIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantAddressRepository restaurantAddressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private AiRepository aiRepository;

    private User owner;
    private User customer;
    private User otherOwner;
    private Restaurant testRestaurant;
    private RestaurantAddress testRestaurantAddress;
    private Menu testMenu;


    @BeforeEach
    void setUp() {
        // 가게 진짜 오너
        owner = User.builder()
            .username("owner")
            .password("1234")
            .email("owner@test.com")
            .role(UserRole.OWNER)
            .isPublic(IsPublic.PUBLIC)
            .build();
        owner = userRepository.save(owner);

        // 다른 가게 오너
        otherOwner = User.builder()
            .username("otherOwner")
            .password("1234")
            .email("otherOwner@test.com")
            .role(UserRole.OWNER)
            .isPublic(IsPublic.PUBLIC)
            .build();
        otherOwner = userRepository.save(otherOwner);

        // 고객
        customer = User.builder()
            .username("customer")
            .password("1234")
            .email("customer@test.com")
            .role(UserRole.CUSTOMER)
            .isPublic(IsPublic.PUBLIC)
            .build();
        customer = userRepository.save(customer);

        // 테스트용 가게 주소 생성
        testRestaurantAddress = TestEntityCreateTools.createRestaurantAddress(owner.getUsername());
        restaurantAddressRepository.save(testRestaurantAddress);

        // 테스트용 가게 생성
        testRestaurant = TestEntityCreateTools.createRestaurant(
            owner,
            testRestaurantAddress,
            owner.getUsername(),
            "010-1111-1111");
        restaurantRepository.save(testRestaurant);

        // AI 설명 생성용 testMenu 생성
        testMenu = TestEntityCreateTools.createMenu(
            owner,
            testRestaurant,
            "감자볶음",
            "감자를 식용유에 두르고 볶았습니다.",
            20000.00
        );
        menuRepository.save(testMenu);

    }

    private Authentication getAuth(User user) {
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        return new UsernamePasswordAuthenticationToken(userDetails, null,
            userDetails.getAuthorities());
    }

    // 메뉴 추가 성공 테스트.
    @Test
    void addMenuTest() throws Exception {
        MenuRequestDto requestDto = new MenuRequestDto();
        requestDto.setName("왕감자");
        requestDto.setDescription("진짜 큰 왕감자");
        requestDto.setPrice(BigDecimal.valueOf(15000));
        requestDto.setIsHidden(false);

        MvcResult mvcResult = mockMvc.perform(
                post("/api/menus/{restaurantId}", testRestaurant.getId())
                    .with(authentication(getAuth(owner)))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto))
            )
            .andExpect(status().isCreated())
            .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        MenuResponseDto responseDto = objectMapper.readValue(responseBody, MenuResponseDto.class);

        Menu savedMenu = menuRepository.findById(responseDto.getId())
            .orElseThrow(() -> new RuntimeException("DB에 저장된 메뉴를 찾을 수 없습니다."));

        assertEquals("왕감자", savedMenu.getName());
        assertEquals("진짜 큰 왕감자", savedMenu.getDescription());
        assertEquals(BigDecimal.valueOf(15000), savedMenu.getPrice());
        assertFalse(savedMenu.getIsHidden());
        assertEquals(testRestaurant.getId(), savedMenu.getRestaurant().getId());
    }

    // 메뉴 추가 실패 테스트 : 다른 레스토랑 오너
    @Test
    void addMenuOtherOwner() throws Exception {
        MenuRequestDto requestDto = new MenuRequestDto();
        requestDto.setName("다른 오너에요");
        requestDto.setDescription("다른오너가 추가한 메뉴");
        requestDto.setPrice(BigDecimal.valueOf(15000));
        requestDto.setIsHidden(false);

        MvcResult mvcResult = mockMvc.perform(
                post("/api/menus/{restaurantId}", testRestaurant.getId())
                    .with(authentication(getAuth(otherOwner)))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto))
            )
            .andExpect(status().isForbidden())
            .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        System.out.println("responseBody = " + responseBody);

        boolean exists = menuRepository.existsByRestaurantAndName(testRestaurant,
            requestDto.getName());
        assertFalse(exists, "실패 : 다른 오너가 추가한 메뉴가 DB에 생성됨");

    }

    // 메뉴 추가 실패 테스트 : 고객
    @Test
    void addMenuCustomer() throws Exception {
        MenuRequestDto requestDto = new MenuRequestDto();
        requestDto.setName("손님이에요");
        requestDto.setDescription("손님이 추가한 메뉴");
        requestDto.setPrice(BigDecimal.valueOf(15000));
        requestDto.setIsHidden(false);

        MvcResult mvcResult = mockMvc.perform(
                post("/api/menus/{restaurantId}", testRestaurant.getId())
                    .with(authentication(getAuth(customer)))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto))
            )
            .andExpect(status().isForbidden())
            .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        System.out.println("responseBody = " + responseBody);

        boolean exists = menuRepository.existsByRestaurantAndName(testRestaurant,
            requestDto.getName());
        assertFalse(exists, "실패 : 손님이 추가한 메뉴가 DB에 생성됨");

    }

    // AI 로그 생성 성공 테스트
    @Test
    void createAiDescription() throws Exception {

        mockMvc.perform(
                post("/api/menus/{menuId}/aiDescription", testMenu.getId())
                    .with(authentication(getAuth(owner)))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isCreated())
            .andReturn();

        boolean exists = aiRepository.existsByMenuId(testMenu.getId());
        assertTrue(exists, "실패 : Ai 로그가 생성되지 않았습니다.");

    }

    // AI 로그 생성 실패 테스트 : 다른 레스토랑 오너
    @Test
    void createAiDescriptionOtherOwner() throws Exception {

        mockMvc.perform(
                post("/api/menus/{menuId}/aiDescription", testMenu.getId())
                    .with(authentication(getAuth(otherOwner)))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isForbidden())
            .andReturn();

        boolean exists = aiRepository.existsByMenuId(testMenu.getId());
        assertFalse(exists, "실패 : 다른 레스토랑 주인으로부터 Ai 로그가 생성되었습니다.");

    }

    // AI 로그 생성 실패 테스트 : 고객
    @Test
    void createAiDescriptionCustomer() throws Exception {

        mockMvc.perform(
                post("/api/menus/{menuId}/aiDescription", testMenu.getId())
                    .with(authentication(getAuth(customer)))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isForbidden())
            .andReturn();

        boolean exists = aiRepository.existsByMenuId(testMenu.getId());
        assertFalse(exists, "실패 : 고객으로부터 Ai 로그가 생성되었습니다.");

    }

}
