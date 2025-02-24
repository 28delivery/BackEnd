package com.sparta.spring_deep._delivery.domain.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.spring_deep._delivery.domain.address.dto.AddressRequestDto;
import com.sparta.spring_deep._delivery.domain.address.dto.AddressResponseDto;
import com.sparta.spring_deep._delivery.domain.address.entity.Address;
import com.sparta.spring_deep._delivery.domain.address.repository.AddressRepository;
import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import com.sparta.spring_deep._delivery.domain.user.entity.IsPublic;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import com.sparta.spring_deep._delivery.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AddressIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    private User customer;
    private User owner;
    private Address address;

    @BeforeEach
    void setUp() {
        customer = User.builder()
            .username("userTest")
            .password("userTest1234*")
            .email("testuser@test.com")
            .role(UserRole.CUSTOMER)
            .isPublic(IsPublic.PUBLIC)
            .build();
        customer = userRepository.save(customer);

        owner = User.builder()
            .username("owner")
            .password("owner")
            .email("owner@test.com")
            .role(UserRole.OWNER)
            .isPublic(IsPublic.PUBLIC)
            .build();
        owner = userRepository.save(owner);

    }

    // 인증 객체 생성
    private Authentication getAuth(User user) {
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        return new UsernamePasswordAuthenticationToken(userDetails, null,
            userDetails.getAuthorities());
    }

    @Test
    @DisplayName("배송지 추가 테스트 : 성공")
    void createAddressTest() throws Exception {
        String testAddress = "서울시 강남구 테헤란로 123";
        String testAddressName = "회사";

        AddressRequestDto requestDto = new AddressRequestDto();
        requestDto.setAddress(testAddress);
        requestDto.setAddressName(testAddressName);

        MvcResult mvcResult = mockMvc.perform(
                post("/api/addresses")
                    .with(authentication(getAuth(customer)))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto))
            )
            .andExpect(status().isCreated())
            .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        AddressResponseDto responseDto = objectMapper.readValue(responseBody,
            AddressResponseDto.class);

        Address createdAddress = addressRepository.findById(responseDto.getId())
            .orElseThrow(() -> new RuntimeException("해당하는 배송지가 없습니다"));

        assertEquals(testAddressName, createdAddress.getAddressName());
        assertEquals(testAddress, createdAddress.getAddress());

    }

    @Test
    @DisplayName("배송지 추가 테스트 : 실패")
    void createAddressOwnerTest() throws Exception {
        String testAddress = "안산시 상록구 부곡동 123";
        String testAddressName = "추가되면안되는집";

        AddressRequestDto requestDto = new AddressRequestDto();
        requestDto.setAddress(testAddress);
        requestDto.setAddressName(testAddressName);

        MvcResult mvcResult = mockMvc.perform(
                post("/api/addresses")
                    .with(authentication(getAuth(owner)))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto))
            )
            .andExpect(status().isForbidden())
            .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        System.out.println("responseBody = " + responseBody);

        boolean exists = addressRepository.existsByAddressName(requestDto.getAddressName());
        assertFalse(exists, "실패 : 오너가 추가한 배송지가 DB에 생성됨");

    }

}
