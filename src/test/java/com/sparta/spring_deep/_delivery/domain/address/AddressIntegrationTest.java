package com.sparta.spring_deep._delivery.domain.address;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.spring_deep._delivery.domain.address.dto.AddressRequestDto;
import com.sparta.spring_deep._delivery.domain.address.entity.Address;
import com.sparta.spring_deep._delivery.domain.address.repository.AddressRepository;
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
import org.springframework.test.web.servlet.MockMvc;
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

        /*
        // 2. 주소 생성
        address = new Address();
        // Address 엔티티에 존재하는 필드명을 사용 (예: address, addressName)
        ReflectionTestUtils.setField(address, "address", "test address");
        ReflectionTestUtils.setField(address, "addressName", "test");
        ReflectionTestUtils.setField(address, "user", customer);
        address = addressRepository.save(address);
        */

    }

    // 인증 객체 생성
    private UsernamePasswordAuthenticationToken getAuth(User user) {
        return new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
    }

    @Test
    void createAddressTest() throws Exception {
        String testAddress = "서울시 강남구 테헤란로 123";
        String testAddressName = "회사";

        AddressRequestDto addressRequestDto = new AddressRequestDto(null, testAddress,
            testAddressName);
//        ReflectionTestUtils.setField(addressRequestDto, "addressName", testAddressName);
//        ReflectionTestUtils.setField(addressRequestDto, "address", testAddress);

        mockMvc.perform(post("/api/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressRequestDto))
                .with(authentication(getAuth(customer))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.addressName").value(testAddressName))
            .andExpect(jsonPath("$.address").value(testAddress))
            .andExpect(jsonPath("$.user.username").value(customer.getUsername()));

    }

}
