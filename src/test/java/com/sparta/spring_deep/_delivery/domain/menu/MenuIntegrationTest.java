package com.sparta.spring_deep._delivery.domain.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import org.junit.jupiter.api.DisplayName;
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
    private Menu testMenu1;
    private Menu testMenu2;
    private Menu testMenu3;


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
            "감자에 식용유를 두르고 볶았습니다.",
            20000.00
        );
        menuRepository.save(testMenu);

        testMenu1 = TestEntityCreateTools.createMenu(
            owner,
            testRestaurant,
            "당근볶음",
            "당근에 식용유를 두르고 볶았습니다.",
            20000.00
        );
        menuRepository.save(testMenu1);

        testMenu2 = TestEntityCreateTools.createMenu(
            owner,
            testRestaurant,
            "가지볶음",
            "가지에 식용유를 두르고 볶았습니다.",
            20000.00
        );
        menuRepository.save(testMenu2);

        testMenu3 = TestEntityCreateTools.createMenu(
            owner,
            testRestaurant,
            "제육볶음",
            "제육에 식용유를 두르고 볶았습니다.",
            20000.00
        );
        menuRepository.save(testMenu3);

    }

    private Authentication getAuth(User user) {
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        return new UsernamePasswordAuthenticationToken(userDetails, null,
            userDetails.getAuthorities());
    }

    // 메뉴 추가 테스트 : 성공
    @Test
    @DisplayName("메뉴 추가 테스트 : 성공")
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

    // 메뉴 추가 다른 레스토랑 오너 테스트 : 실패
    @Test
    @DisplayName("메뉴 추가 다른 레스토랑 오너 테스트 : 실패")
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

    // 메뉴 추가 고객 테스트 : 실패
    @Test
    @DisplayName("메뉴 추가 고객 테스트 : 실패")
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

    // 메뉴 단건 조회 테스트 : 성공
    @Test
    @DisplayName("메뉴 단건 조회 테스트 : 성공")
    void searchMenu() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                get("/api/menus/{restaurantId}/search", testRestaurant.getId())
                    .with(authentication(getAuth(owner)))
                    .param("name", "감자볶음") // 예: MenuSearchDto의 필드
                    .param("page", "0")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        assertTrue(responseBody.contains("감자볶음"), "검색 결과에 '감자볶음'이 없음");
    }

    // 메뉴 단건 조회 테스트 : 실패
    @Test
    @DisplayName("메뉴 단건 조회 테스트 : 실패")
    void searchMenus_NoResult() throws Exception {
        mockMvc.perform(
                get("/api/menus/{restaurantId}/search", testRestaurant.getId())
                    .with(authentication(getAuth(customer)))
                    .param("name", "탕수육")
                    .param("page", "0")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound());
    }

    // 메뉴 다건 조회 테스트 : 성공
    @Test
    @DisplayName("메뉴 다건 조회 테스트 : 성공")
    void searchAllMenus() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                get("/api/menus/{restaurantId}/search", testRestaurant.getId())
                    .with(authentication(getAuth(customer)))
                    .param("page", "0")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        System.out.println("responseBody = " + responseBody);

        assertTrue(responseBody.contains(testMenu.getName()), "전체 조회 결과에 기존 메뉴가 없음");
    }

    // 메뉴 수정 테스트 : 성공
    @Test
    @DisplayName("메뉴 수정 테스트 : 성공")
    void updateMenu_Success() throws Exception {
        MenuRequestDto requestDto = new MenuRequestDto("감자튀김", "튀긴 감자", BigDecimal.valueOf(9999),
            true);

        MvcResult mvcResult = mockMvc.perform(
                put("/api/menus/{menuId}", testMenu.getId())
                    .with(authentication(getAuth(owner)))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto))
            )
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        MenuResponseDto responseDto = objectMapper.readValue(responseBody, MenuResponseDto.class);

        Menu updatedMenu = menuRepository.findById(responseDto.getId())
            .orElseThrow(() -> new RuntimeException("업데이트된 메뉴가 DB에 없음"));

        assertEquals("감자튀김", updatedMenu.getName());
        assertEquals("튀긴 감자", updatedMenu.getDescription());
        assertEquals(BigDecimal.valueOf(9999), updatedMenu.getPrice());
        assertTrue(updatedMenu.getIsHidden());
    }

    // 메뉴 수정 다른 사장님 테스트 : 실패
    @Test
    @DisplayName("메뉴 수정 다른 사장님 테스트 : 실패")
    void updateMenu_OtherOwner() throws Exception {
        MenuRequestDto requestDto = new MenuRequestDto("다른 오너 메뉴", "다른오너 수정",
            BigDecimal.valueOf(20000), false);

        mockMvc.perform(
                put("/api/menus/{menuId}", testMenu.getId())
                    .with(authentication(getAuth(otherOwner)))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto))
            )
            .andExpect(status().isForbidden());

        // DB에 변경이 없는지 확인
        Menu unchanged = menuRepository.findById(testMenu.getId())
            .orElseThrow(() -> new RuntimeException("메뉴가 없어졌음?"));
        assertEquals(testMenu.getName(), unchanged.getName());
        assertEquals(testMenu.getDescription(), unchanged.getDescription());
    }

    // 메뉴 삭제 테스트 : 성공
    @Test
    @DisplayName("메뉴 삭제 테스트 : 성공")
    void deleteMenu_Success() throws Exception {
        mockMvc.perform(
                delete("/api/menus/{menuId}", testMenu.getId())
                    .with(authentication(getAuth(owner)))
            )
            .andExpect(status().isOk());

        // 소프트 삭제인지 여부 확인
        boolean exists = menuRepository.findByIdAndIsDeletedFalse(testMenu.getId()).isPresent();
        assertFalse(exists, "삭제했는데도 isDeleted=false 상태로 남아있음");
    }

    // 메뉴 삭제 다른 사장님 테스트 : 실패
    @Test
    @DisplayName("메뉴 삭제 다른 사장님 테스트 : 실패")
    void deleteMenu_OtherOwner() throws Exception {
        mockMvc.perform(
                delete("/api/menus/{menuId}", testMenu.getId())
                    .with(authentication(getAuth(otherOwner)))
            )
            .andExpect(status().isForbidden());

        // DB에 여전히 isDeleted=false인지
        boolean stillExists = menuRepository.findByIdAndIsDeletedFalse(testMenu.getId())
            .isPresent();
        assertTrue(stillExists, "다른 사장님이 삭제했는데 메뉴가 삭제됨");
    }

    // AI 로그 생성 테스트 : 성공
    @Test
    @DisplayName("AI 로그 생성 테스트 : 성공")
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

    // AI 로그 생성 다른 사장님 테스트 : 실패
    @Test
    @DisplayName("AI 로그 생성 다른 사장님 테스트 : 실패")
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

    // AI 로그 생성 고객 테스트 : 실패
    @Test
    @DisplayName("AI 로그 생성 고객 테스트 : 실패")
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
