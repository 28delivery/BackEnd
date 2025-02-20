package com.sparta.spring_deep._delivery.domain.address.service;

import com.sparta.spring_deep._delivery.domain.address.dto.AddressRequestDto;
import com.sparta.spring_deep._delivery.domain.address.dto.AddressResponseDto;
import com.sparta.spring_deep._delivery.domain.address.entity.Address;
import com.sparta.spring_deep._delivery.domain.address.repository.AddressRepository;
import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import com.sparta.spring_deep._delivery.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    // 현재 사용자 정보 가져오기
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();

        if (!UserRole.CUSTOMER.equals(user.getRole())) {
            throw new IllegalArgumentException("Customer privileges required");
        }

        return userRepository.findByUsername(user.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found : " + user.getUsername()));
    }

    // 배송지 추가
    public AddressResponseDto createAddress(AddressRequestDto requestDto) {

        // 사용자 조회
        User user = getCurrentUser();

        Address address = new Address(requestDto, user);
        Address savedAddress = addressRepository.save(address);

        return new AddressResponseDto(savedAddress);
    }

    // 전체 배송지 조회
    public List<AddressResponseDto> getAllAddresses() {

        // 사용자 조회
        User user = getCurrentUser();

        List<Address> addresses = addressRepository.findAllByUserUsername(user.getUsername());

        return addresses.stream()
            .map(AddressResponseDto::new)
            .collect(Collectors.toList());
    }


    // 특정 배송지 조회
    public AddressResponseDto getAddress(UUID id) {

        // 사용자 조회
        getCurrentUser();

        // id로 배송지 조회
        Address address = addressRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Address not found: " + id));
        return new AddressResponseDto(address);
    }

    // 배송지 수정
    @Transactional
    public AddressResponseDto updateAddress(UUID id, AddressRequestDto requestDto) {

        // 사용자 조회
        getCurrentUser();

        // id로 배송지 조회
        Address address = addressRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Address not found: " + id));

        address.updateAddress(requestDto);
        return new AddressResponseDto(address);
    }

    // 배송지 삭제
    @Transactional
    public boolean deleteAddress(UUID id) {

        // 사용자 조회
        User user = getCurrentUser();

        // id로 배송지 조회
        Address address = addressRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Address not found: " + id));

        address.delete(user.getUsername());

        return true;
    }

}