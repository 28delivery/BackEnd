package com.sparta.spring_deep._delivery.domain.address;

import com.sparta.spring_deep._delivery.domain.user.User;
import com.sparta.spring_deep._delivery.domain.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    // 배송지 추가
    public AddressResponseDto createAddress(AddressRequestDto requestDto) {

        // 사용자 정보 조회
        // 테스트용
        User user = userRepository.findById("user1")
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Address address = new Address(requestDto, user);
        Address savedAddress = addressRepository.save(address);

        return new AddressResponseDto(savedAddress);
    }

    // 전체 배송지 조회
    public List<AddressResponseDto> getAllAddresses() {

        // 사용자 정보 조회
        // 테스트용
        User user = userRepository.findByUsername("user1")
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Address> addresses = addressRepository.findAllByUserUsername(user.getUsername());

        return addresses.stream()
            .map(AddressResponseDto::new)
            .collect(Collectors.toList());
    }


    // 특정 배송지 조회
    public AddressResponseDto getAddress(UUID id) {

        // 사용자 정보 조회
        // 테스트용
        User user = userRepository.findById("user1")
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // id로 배송지 조회
        Address address = addressRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Address not found: " + id));
        return new AddressResponseDto(address);
    }

    // 배송지 수정
    @Transactional
    public AddressResponseDto updateAddress(UUID id, AddressRequestDto requestDto) {

        // 사용자 정보 조회(토큰)
        // 추후 추가

        // id로 배송지 조회
        Address address = addressRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Address not found: " + id));

        address.updateAddress(requestDto);
        return new AddressResponseDto(address);
    }

    // 배송지 삭제
    @Transactional
    public boolean deleteAddress(UUID id) {

        // 사용자 정보 조회
        // 테스트용
        User user = userRepository.findById("user1")
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // id로 배송지 조회
        Address address = addressRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Address not found: " + id));

        address.delete(user.getUsername());

        return true;
    }

}
