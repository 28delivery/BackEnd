package com.sparta.spring_deep._delivery.domain.restaurant;

import static com.sparta.spring_deep._delivery.testutil.TestEntityCreateTools.createRestaurant;
import static com.sparta.spring_deep._delivery.testutil.TestEntityCreateTools.createRestaurantAddress;
import static com.sparta.spring_deep._delivery.testutil.TestEntityCreateTools.createUser;
import static com.sparta.spring_deep._delivery.testutil.TestEntityCreateTools.getAuth;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.spring_deep._delivery.admin.restaurant.RestaurantAdminCreateRequestDto;
import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddress;
import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddressRepository;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import com.sparta.spring_deep._delivery.domain.user.repository.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RestaurantIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantAddressRepository restaurantAddressRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private User nonOwner;
    private RestaurantAddress address;

    @BeforeEach
    void setUp() {
        // 테스트용 소유자 생성
        owner = createUser("owner1", UserRole.OWNER);
        owner = userRepository.save(owner);

        // 소유자가 아닌 사용자 생성
        nonOwner = createUser("user2", UserRole.CUSTOMER);
        nonOwner = userRepository.save(nonOwner);

        // 테스트용 주소 생성
        address = createRestaurantAddress("owner1");
        address = restaurantAddressRepository.save(address);

    }

    // ===== GET /api/restaurants/{restaurantId} =====

    // 성공 케이스: 존재하는 음식점 조회
    @Test
    void testGetRestaurantSuccess() throws Exception {

        Restaurant restaurant = createRestaurant(owner, address, "꿀맛짬뽕", "010-2222-2222");
        restaurant = restaurantRepository.save(restaurant);

        mockMvc.perform(get("/api/restaurants/{restaurantId}", restaurant.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(restaurant.getId().toString()))
            .andExpect(jsonPath("$.name").value("꿀맛짬뽕"))
            .andExpect(jsonPath("$.phone").value("010-2222-2222"))
            .andExpect(jsonPath("$.roadAddr").value(address.getRoadAddr()));
    }

    // 실패 케이스: 존재하지 않는 음식점 조회 -> 404 Not Found
    @Test
    void testGetRestaurantNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        mockMvc.perform(get("/api/restaurants/{restaurantId}", randomId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    // ===== PUT /api/restaurants/{restaurantId} (음식점 수정) =====

    // 성공 케이스: 음식점 수정 (요청자와 소유자가 일치)
    @Test
    void testUpdateRestaurantSuccess() throws Exception {
        // 초기 음식점 생성
        Restaurant restaurant = createRestaurant(owner, address, "꿀맛짬뽕", "010-2222-2222");
        restaurant = restaurantRepository.save(restaurant);

        // 수정할 내용 (RestaurantRequestDto는 setter 제공)
        RestaurantRequestDto updateDto = new RestaurantRequestDto();
        updateDto.setName("Updated Restaurant");
        updateDto.setCategory(Restaurant.CategoryEnum.YANGSIK);
        updateDto.setRoadAddr("논현로 111길 21");
        updateDto.setDetailAddr("강남빌딩 200호");
        updateDto.setPhone("010-2222-3333");

        String updateJson = objectMapper.writeValueAsString(updateDto);

        mockMvc.perform(put("/api/restaurants/{restaurantId}", restaurant.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson)
                .with(authentication(getAuth(owner))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Updated Restaurant"))
            .andExpect(jsonPath("$.category").value("YANGSIK"))
            .andExpect(jsonPath("$.phone").value("010-2222-3333"));
    }

    // 실패 케이스: 수정 요청 시 요청자가 소유자가 아닌 경우 -> 403 Forbidden
    @Test
    void testUpdateRestaurantNotOwner() throws Exception {
        // 음식점 생성 (소유자는 owner)
        Restaurant restaurant = createRestaurant(owner, address, "꿀맛짬뽕", "010-2222-2222");
        restaurant = restaurantRepository.save(restaurant);

        RestaurantRequestDto updateDto = new RestaurantRequestDto();
        updateDto.setName("Updated Restaurant");
        updateDto.setCategory(Restaurant.CategoryEnum.YANGSIK);
        updateDto.setRoadAddr("논현로 111길 21");
        updateDto.setDetailAddr("강남빌딩 200호");
        updateDto.setPhone("010-2222-3333");

        String updateJson = objectMapper.writeValueAsString(updateDto);

        // 소유자가 아닌 nonOwner로 수정 요청
        mockMvc.perform(put("/api/restaurants/{restaurantId}", restaurant.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson)
                .with(authentication(getAuth(nonOwner))))
            .andExpect(status().isForbidden());
    }

    // 실패 케이스: 존재하지 않는 음식점 수정 -> 404 Not Found
    @Test
    void testUpdateRestaurantNotFound() throws Exception {
        RestaurantRequestDto updateDto = new RestaurantRequestDto();
        updateDto.setName("Updated Restaurant");
        updateDto.setCategory(Restaurant.CategoryEnum.YANGSIK);
        updateDto.setRoadAddr("논현로 111길 21");
        updateDto.setDetailAddr("Updated Detail");
        updateDto.setPhone("010-2222-3333");

        String updateJson = objectMapper.writeValueAsString(updateDto);
        UUID randomId = UUID.randomUUID();

        mockMvc.perform(put("/api/restaurants/{restaurantId}", randomId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson)
                .with(authentication(getAuth(owner))))
            .andExpect(status().isNotFound());
    }

    // ===== DELETE /api/restaurants/{restaurantId} (음식점 삭제) =====

    // 성공 케이스: 소유자에 의한 삭제
    @Test
    void testDeleteRestaurantSuccess() throws Exception {
        // 음식점 생성 (소유자는 owner)
        Restaurant restaurant = createRestaurant(owner, address, "꿀맛짬뽕", "010-2222-2222");
        restaurant = restaurantRepository.save(restaurant);

        mockMvc.perform(delete("/api/restaurants/{restaurantId}", restaurant.getId())
                .with(authentication(getAuth(owner))))
            .andExpect(status().isAccepted());
    }

    // 실패 케이스: 삭제 요청 시 소유자가 아닌 경우 -> 403 Forbidden
    @Test
    void testDeleteRestaurantNotOwner() throws Exception {
        RestaurantAdminCreateRequestDto createDto = new RestaurantAdminCreateRequestDto();
        ReflectionTestUtils.setField(createDto, "ownerId", owner.getUsername());
        ReflectionTestUtils.setField(createDto, "name", "Restaurant to Delete");
        ReflectionTestUtils.setField(createDto, "category", Restaurant.CategoryEnum.HANSIK);
        ReflectionTestUtils.setField(createDto, "roadAddr", address.getRoadAddr());
        ReflectionTestUtils.setField(createDto, "detailAddr", address.getDetailAddr());
        ReflectionTestUtils.setField(createDto, "phone", "010-1111-2222");

        Restaurant restaurant = new Restaurant(createDto, owner, address, owner.getUsername());
        restaurant = restaurantRepository.save(restaurant);

        mockMvc.perform(delete("/api/restaurants/{restaurantId}", restaurant.getId())
                .with(authentication(getAuth(nonOwner))))
            .andExpect(status().isForbidden());
    }

    // 실패 케이스: 존재하지 않는 음식점 삭제 -> 404 Not Found
    @Test
    void testDeleteRestaurantNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        mockMvc.perform(delete("/api/restaurants/{restaurantId}", randomId)
                .with(authentication(getAuth(owner))))
            .andExpect(status().isNotFound());
    }

    // ===== GET /api/restaurants/search (음식점 검색) =====

    // 성공 케이스: 검색 조건에 맞는 음식점이 있는 경우
    @Test
    void testSearchRestaurantSuccess() throws Exception {
        // 두 개의 음식점 생성
        // 음식점 생성 (소유자는 owner)
        Restaurant restaurant1 = createRestaurant(owner, address, "진짜짬뽕", "010-2222-2222");
        restaurant1 = restaurantRepository.save(restaurant1);

        // 음식점 생성 (소유자는 owner)
        Restaurant restaurant2 = createRestaurant(owner, address, "짜장진짜", "010-2222-2222");
        restaurant2 = restaurantRepository.save(restaurant2);

        // name 파라미터에 "진짜"가 포함된 경우 검색 (검색 결과가 2건 이상이어야 함)
        mockMvc.perform(get("/api/restaurants/search")
                .param("name", "진짜")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(2))));
    }

    // 실패 케이스: 검색 결과가 없을 경우 -> 404 Not Found (서비스에서 ResourceNotFoundException 발생)
    @Test
    void testSearchRestaurantNoResult() throws Exception {
        mockMvc.perform(get("/api/restaurants/search")
                .param("name", "NonExistentRestaurant")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
}
