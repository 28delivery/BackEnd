package com.sparta.spring_deep._delivery.domain.address.service;

import static com.sparta.spring_deep._delivery.util.AuthTools.ownerCheck;

import com.sparta.spring_deep._delivery.domain.address.dto.AddressRequestDto;
import com.sparta.spring_deep._delivery.domain.address.dto.AddressResponseDto;
import com.sparta.spring_deep._delivery.domain.address.dto.AddressSearchDto;
import com.sparta.spring_deep._delivery.domain.address.entity.Address;
import com.sparta.spring_deep._delivery.domain.address.repository.AddressRepository;
import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.exception.ResourceNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Address Service")
public class AddressService {

    private final AddressRepository addressRepository;

    // 배송지 추가
    public AddressResponseDto createAddress(AddressRequestDto requestDto,
        UserDetailsImpl userDetails) {
        log.info("배송지 추가");

        // 사용자 조회
        User loggedInUser = userDetails.getUser();

        Address address = new Address(requestDto, loggedInUser);
        Address savedAddress = addressRepository.save(address);

        return new AddressResponseDto(savedAddress);
    }

//    // 전체 배송지 조회
//    public List<AddressResponseDto> getAllAddresses(UserDetailsImpl userDetails) {
//        log.info("전체 배송지 조회");
//
//        // 사용자 조회
//        log.info("전체 배송지 조회 : 사용자 조회");
//        User loggedInUser = userDetails.getUser();
//
//        List<Address> addresses = addressRepository.findAllByUserUsernameAndIsDeletedFalse(
//            loggedInUser.getUsername());
//
//        if (addresses.isEmpty()) {
//            throw new ResourceNotFoundException();
//        }
//
//        return addresses.stream()
//            .map(AddressResponseDto::new)
//            .collect(Collectors.toList());
//    }

    // 배송지 검색 및 조회
    public Page<AddressResponseDto> searchMyAddresses(AddressSearchDto searchDto,
        UserDetailsImpl userDetails, Pageable pageable) {
        log.info("배송지 검색 및 조회");

        // 사용자 조회
        log.info("전체 배송지 조회 : 사용자 조회");
        User loggedInUser = userDetails.getUser();

        Page<AddressResponseDto> responseDtos = addressRepository.searchByOptionAndIsDeletedFalse(
            searchDto, loggedInUser, pageable);

        if (responseDtos.getContent().isEmpty()) {
            throw new ResourceNotFoundException();
        }

        return responseDtos;
    }


    // 특정 배송지 조회
    public AddressResponseDto getAddress(UUID id, UserDetailsImpl userDetails) {
        log.info("특정 배송지 조회");

        // 사용자 조회
        User loggedInUser = userDetails.getUser();

        // id로 배송지 조회
        Address address = addressRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(ResourceNotFoundException::new);

        // 사용자와 배송지 소유자 일치 여부 확인
        ownerCheck(loggedInUser, address.getUser());

        return new AddressResponseDto(address);
    }

    // 배송지 수정
    @Transactional
    public AddressResponseDto updateAddress(UUID id, AddressRequestDto requestDto,
        UserDetailsImpl userDetails) {
        log.info("배송지 수정");

        // 사용자 조회
        User loggedInUser = userDetails.getUser();

        // id로 배송지 조회
        Address address = addressRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(ResourceNotFoundException::new);

        // 사용자와 배송지 소유자 일치 여부 확인
        ownerCheck(loggedInUser, address.getUser());

        address.updateAddress(requestDto);
        return new AddressResponseDto(address);
    }

    // 배송지 삭제
    @Transactional
    public void deleteAddress(UUID id, UserDetailsImpl userDetails) {
        log.info("배송지 삭제");

        // 사용자 조회
        User loggedInUser = userDetails.getUser();

        // id로 배송지 조회
        Address address = addressRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(ResourceNotFoundException::new);

        // 사용자와 배송지 소유자 일치 여부 확인
        ownerCheck(loggedInUser, address.getUser());

        address.delete(loggedInUser.getUsername());
    }

}