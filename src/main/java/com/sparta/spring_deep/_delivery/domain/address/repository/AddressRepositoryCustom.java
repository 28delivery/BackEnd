package com.sparta.spring_deep._delivery.domain.address.repository;

import com.sparta.spring_deep._delivery.domain.address.dto.AddressResponseDto;
import com.sparta.spring_deep._delivery.domain.address.dto.AddressSearchDto;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AddressRepositoryCustom {

    Page<AddressResponseDto> searchByOptionAndIsDeletedFalse(AddressSearchDto searchDto,
        User loggedInUser, Pageable pageable);
}
