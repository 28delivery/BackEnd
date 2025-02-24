package com.sparta.spring_deep._delivery.domain.user.repository;

import com.sparta.spring_deep._delivery.admin.user.UserAdminResponseDto;
import com.sparta.spring_deep._delivery.admin.user.UserAdminSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {

    Page<UserAdminResponseDto> searchByOption(UserAdminSearchDto searchDto, Pageable pageable);
}
