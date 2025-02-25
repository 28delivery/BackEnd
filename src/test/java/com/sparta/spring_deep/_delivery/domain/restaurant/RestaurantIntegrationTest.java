package com.sparta.spring_deep._delivery.domain.restaurant;

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
import com.sparta.spring_deep._delivery.domain.user.repository.UserRepository;
import java.util.Collections;
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
        owner = new User();
        ReflectionTestUtils.setField(owner, "username", "owner1");
        owner = userRepository.save(owner);

        // 소유자가 아닌 사용자 생성
        nonOwner = new User();
        ReflectionTestUtils.setField(nonOwner, "username", "user2");
        nonOwner = userRepository.save(nonOwner);

        // 테스트용 주소 생성
        address = new RestaurantAddress();
        ReflectionTestUtils.setField(address, "roadAddr", "Test Road");
        ReflectionTestUtils.setField(address, "jibunAddr", "Test Jibun");
        ReflectionTestUtils.setField(address, "detailAddr", "Test Detail");
        ReflectionTestUtils.setField(address, "engAddr", "Test Eng");
        address = restaurantAddressRepository.save(address);


    }

    // 인증 객체 생성 (소유자/비소유자 구분)
    private UsernamePasswordAuthenticationToken getAuth(User user) {
        return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
    }

    // ===== GET /api/restaurants/{restaurantId} =====

    // 성공 케이스: 존재하는 음식점 조회
    @Test
    void testGetRestaurantSuccess() throws Exception {
        // 테스트 음식점 생성 (생성 시 ReflectionTestUtils로 DTO 필드 주입)
        RestaurantAdminCreateRequestDto createDto = new RestaurantAdminCreateRequestDto();
        ReflectionTestUtils.setField(createDto, "ownerId", owner.getUsername());
        ReflectionTestUtils.setField(createDto, "name", "Test Restaurant");
        ReflectionTestUtils.setField(createDto, "category", Restaurant.CategoryEnum.HANSIK);
        ReflectionTestUtils.setField(createDto, "roadAddr", address.getRoadAddr());
        ReflectionTestUtils.setField(createDto, "detailAddr", address.getDetailAddr());
        ReflectionTestUtils.setField(createDto, "phone", "010-1111-2222");

        Restaurant restaurant = new Restaurant(createDto, owner, address, owner.getUsername());
        restaurant = restaurantRepository.save(restaurant);

        RestaurantAdminCreateRequestDto createRequestDto = new RestaurantAdminCreateRequestDto();
        createRequestDto.setOwnerId(owner.getUsername()); // 굿
        createRequestDto.setName("Test Restaurant");
        createRequestDto.setCategory(Restaurant.CategoryEnum.HANSIK);
        createRequestDto.setRoadAddr(address.getRoadAddr());
        createRequestDto.setDetailAddr(address.getDetailAddr());
        createRequestDto.setPhone("010-1111-2222");

        Restaurant restaurant1 = new Restaurant(createDto, owner, address, owner.getUsername());

        mockMvc.perform(get("/api/restaurants/{restaurantId}", restaurant.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(restaurant.getId().toString()))
            .andExpect(jsonPath("$.name").value("Test Restaurant"))
            .andExpect(jsonPath("$.phone").value("010-1111-2222"))
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
        RestaurantAdminCreateRequestDto createDto = new RestaurantAdminCreateRequestDto();
        ReflectionTestUtils.setField(createDto, "ownerId", owner.getUsername());
        ReflectionTestUtils.setField(createDto, "name", "Original Restaurant");
        ReflectionTestUtils.setField(createDto, "category", Restaurant.CategoryEnum.HANSIK);
        ReflectionTestUtils.setField(createDto, "roadAddr", address.getRoadAddr());
        ReflectionTestUtils.setField(createDto, "detailAddr", address.getDetailAddr());
        ReflectionTestUtils.setField(createDto, "phone", "010-1111-2222");

        Restaurant restaurant = new Restaurant(createDto, owner, address, owner.getUsername());
        restaurant = restaurantRepository.save(restaurant);

        // 수정할 내용 (RestaurantRequestDto는 setter 제공)
        RestaurantRequestDto updateDto = new RestaurantRequestDto();
        updateDto.setName("Updated Restaurant");
        updateDto.setCategory(Restaurant.CategoryEnum.YANGSIK);
        updateDto.setRoadAddr("Updated Road");
        updateDto.setDetailAddr("Updated Detail");
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
        RestaurantAdminCreateRequestDto createDto = new RestaurantAdminCreateRequestDto();
        ReflectionTestUtils.setField(createDto, "ownerId", owner.getUsername());
        ReflectionTestUtils.setField(createDto, "name", "Original Restaurant");
        ReflectionTestUtils.setField(createDto, "category", Restaurant.CategoryEnum.HANSIK);
        ReflectionTestUtils.setField(createDto, "roadAddr", address.getRoadAddr());
        ReflectionTestUtils.setField(createDto, "detailAddr", address.getDetailAddr());
        ReflectionTestUtils.setField(createDto, "phone", "010-1111-2222");

        Restaurant restaurant = new Restaurant(createDto, owner, address, owner.getUsername());
        restaurant = restaurantRepository.save(restaurant);

        RestaurantRequestDto updateDto = new RestaurantRequestDto();
        updateDto.setName("Updated Restaurant");
        updateDto.setCategory(Restaurant.CategoryEnum.YANGSIK);
        updateDto.setRoadAddr("Updated Road");
        updateDto.setDetailAddr("Updated Detail");
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
        updateDto.setRoadAddr("Updated Road");
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
        RestaurantAdminCreateRequestDto createDto1 = new RestaurantAdminCreateRequestDto();
        ReflectionTestUtils.setField(createDto1, "ownerId", owner.getUsername());
        ReflectionTestUtils.setField(createDto1, "name", "Alpha Restaurant");
        ReflectionTestUtils.setField(createDto1, "category", Restaurant.CategoryEnum.HANSIK);
        ReflectionTestUtils.setField(createDto1, "roadAddr", "Road A");
        ReflectionTestUtils.setField(createDto1, "detailAddr", "Detail A");
        ReflectionTestUtils.setField(createDto1, "phone", "010-0000-0001");
        Restaurant restaurant1 = new Restaurant(createDto1, owner, address, owner.getUsername());
        restaurantRepository.save(restaurant1);

        RestaurantAdminCreateRequestDto createDto2 = new RestaurantAdminCreateRequestDto();
        ReflectionTestUtils.setField(createDto2, "ownerId", owner.getUsername());
        ReflectionTestUtils.setField(createDto2, "name", "Beta Restaurant");
        ReflectionTestUtils.setField(createDto2, "category", Restaurant.CategoryEnum.YANGSIK);
        ReflectionTestUtils.setField(createDto2, "roadAddr", "Road B");
        ReflectionTestUtils.setField(createDto2, "detailAddr", "Detail B");
        ReflectionTestUtils.setField(createDto2, "phone", "010-0000-0002");
        Restaurant restaurant2 = new Restaurant(createDto2, owner, address, owner.getUsername());
        restaurantRepository.save(restaurant2);

        // name 파라미터에 "Restaurant"가 포함된 경우 검색 (검색 결과가 2건 이상이어야 함)
        mockMvc.perform(get("/api/restaurants/search")
                .param("name", "Restaurant")
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
