package com.sparta.spring_deep._delivery.domain.restaurant;

import static com.sparta.spring_deep._delivery.testutil.TestEntityCreateTools.createRestaurantAddress;
import static com.sparta.spring_deep._delivery.testutil.TestEntityCreateTools.createUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddress;
import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddressCreateRequestDto;
import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddressRepository;
import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import com.sparta.spring_deep._delivery.domain.user.repository.UserRepository;
import com.sparta.spring_deep._delivery.util.RestaurantAddressTools;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RestaurantAddressIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantAddressRepository restaurantAddressRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = createUser("testuser", UserRole.OWNER);
        testUser = userRepository.save(testUser);


    }

    private UserDetailsImpl getUserDetails() {
        return new UserDetailsImpl(testUser);
    }

    private Authentication getAuth() {
        return new UsernamePasswordAuthenticationToken(
            getUserDetails(), null, getUserDetails().getAuthorities());
    }

    // ----- 생성 API (POST /api/restaurantAddresses) -----
    @Test
    void testCreateRestaurantAddressSuccess() throws Exception {
        // static mocking: 외부 주소 검색 호출을 가짜 값으로 반환
        try (MockedStatic<RestaurantAddressTools> mockedTools = Mockito.mockStatic(
            RestaurantAddressTools.class)) {
            // 가짜 응답 데이터 구성
            Map<String, Object> fakeResponse = new HashMap<>();
            Map<String, Object> common = new HashMap<>();
            common.put("totalCount", "1");
            Map<String, Object> juso = new HashMap<>();
            juso.put("roadAddr", "Fake RoadAddr");
            juso.put("roadAddrPart1", "Fake Part1");
            juso.put("roadAddrPart2", "Fake Part2");
            juso.put("jibunAddr", "Fake Jibun");
            juso.put("engAddr", "Fake Eng");
            juso.put("zipNo", "FakeZip");
            juso.put("siNm", "FakeSi");
            juso.put("sggNm", "FakeSgg");
            juso.put("emdNm", "FakeEmd");
            juso.put("liNm", "FakeLi");
            juso.put("rn", "FakeRn");
            juso.put("udrtYn", "N");
            juso.put("buldMnnm", "FakeMnnm");
            juso.put("buldSlno", "FakeSlno");
            List<Map<String, Object>> jusoList = new ArrayList<>();
            jusoList.add(juso);
            Map<String, Object> results = new HashMap<>();
            results.put("common", common);
            results.put("juso", jusoList);
            fakeResponse.put("results", results);

            mockedTools.when(() -> RestaurantAddressTools.searchAddress(Mockito.anyString()))
                .thenReturn(fakeResponse);
            mockedTools.when(() -> RestaurantAddressTools.validateTotalCount(fakeResponse))
                .thenReturn(juso);

            // 요청 DTO: roadAddr와 detailAddr만 전달
            RestaurantAddressCreateRequestDto createDto = new RestaurantAddressCreateRequestDto(
                "Fake RoadAddr", "Detail Address");
            String jsonRequest = objectMapper.writeValueAsString(createDto);

            mockMvc.perform(post("/api/restaurantAddresses")
                    .with(authentication(getAuth()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.roadAddr").value("Fake RoadAddr"))
                .andExpect(jsonPath("$.detailAddr").value("Detail Address"))
                .andExpect(jsonPath("$.jibunAddr").value("Fake Jibun"))
                .andExpect(jsonPath("$.engAddr").value("Fake Eng"));
        }
    }

    private Authentication getAuth(User user) {
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        return new UsernamePasswordAuthenticationToken(userDetails, null,
            userDetails.getAuthorities());
    }

    // ----- 단건 조회 API (GET /api/restaurantAddresses/{id}) -----
    @Test
    void testGetRestaurantAddressSuccess() throws Exception {
        // 테스트용 RestaurantAddress 직접 생성 (static 메서드 호출 없이 DB에 값 세팅)
        RestaurantAddress address = createRestaurantAddress("testuser");
        address = restaurantAddressRepository.save(address);

        mockMvc.perform(get("/api/restaurantAddresses/{id}", address.getId())
                .with(authentication(getAuth()))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(address.getId().toString()))
            .andExpect(jsonPath("$.roadAddr").value("서울특별시 강남구 논현로111길 21 (논현동)"))
            .andExpect(jsonPath("$.detailAddr").value("강남빌딩 202호"))
            .andExpect(jsonPath("$.jibunAddr").value("서울특별시 강남구 논현동 206-4"))
            .andExpect(jsonPath("$.engAddr").value("21 Nonhyeon-ro 111-gil, Gangnam-gu, Seoul"));
    }

    // ----- 수정 API (PUT /api/restaurantAddresses/{id}) -----
    @Test
    void testUpdateRestaurantAddressSuccess() throws Exception {
        // 먼저 기존 주소 생성
        RestaurantAddress address = createRestaurantAddress("testuser");
        address = restaurantAddressRepository.save(address);

        // static mocking: update 시에도 외부 주소 검색 호출을 대체
        try (MockedStatic<RestaurantAddressTools> mockedTools = Mockito.mockStatic(
            RestaurantAddressTools.class)) {
            Map<String, Object> fakeResponse = new HashMap<>();
            Map<String, Object> common = new HashMap<>();
            common.put("totalCount", "1");
            Map<String, Object> juso = new HashMap<>();
            juso.put("roadAddr", "Updated RoadAddr");
            juso.put("roadAddrPart1", "Updated Part1");
            juso.put("roadAddrPart2", "Updated Part2");
            juso.put("jibunAddr", "Updated Jibun");
            juso.put("engAddr", "Updated Eng");
            juso.put("zipNo", "99999");
            juso.put("siNm", "Updated Si");
            juso.put("sggNm", "Updated Sgg");
            juso.put("emdNm", "Updated Em");
            juso.put("liNm", "Updated Li");
            juso.put("rn", "Updated Rn");
            juso.put("udrtYn", "N");
            juso.put("buldMnnm", "Updated Mnnm");
            juso.put("buldSlno", "Updated Slno");
            List<Map<String, Object>> jusoList = new ArrayList<>();
            jusoList.add(juso);
            Map<String, Object> results = new HashMap<>();
            results.put("common", common);
            results.put("juso", jusoList);
            fakeResponse.put("results", results);

            mockedTools.when(() -> RestaurantAddressTools.searchAddress(Mockito.anyString()))
                .thenReturn(fakeResponse);
            mockedTools.when(() -> RestaurantAddressTools.validateTotalCount(fakeResponse))
                .thenReturn(juso);

            // 수정 요청 DTO: roadAddr와 detailAddr만 변경 요청 (나머지는 외부 API 결과로 채워짐)
            RestaurantAddressCreateRequestDto updateDto = new RestaurantAddressCreateRequestDto(
                "Updated RoadAddr", "Updated Detail");
            String updateJson = objectMapper.writeValueAsString(updateDto);

            mockMvc.perform(put("/api/restaurantAddresses/{id}", address.getId())
                    .with(authentication(getAuth()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roadAddr").value("Updated RoadAddr"))
                .andExpect(jsonPath("$.detailAddr").value("Updated Detail"))
                .andExpect(jsonPath("$.jibunAddr").value("Updated Jibun"))
                .andExpect(jsonPath("$.engAddr").value("Updated Eng"));
        }
    }

    // ----- 삭제 API (DELETE /api/restaurantAddresses/{id}) -----
    @Test
    void testDeleteRestaurantAddressSuccess() throws Exception {
        // 먼저 주소 생성
        RestaurantAddress address = createRestaurantAddress("testuser");
        address = restaurantAddressRepository.save(address);

        mockMvc.perform(delete("/api/restaurantAddresses/{id}", address.getId())
                .with(authentication(getAuth())))
            .andExpect(status().isOk());
    }
}
