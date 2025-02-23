package com.sparta.spring_deep._delivery.admin.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewAdminRepositoryCustom {

    Page<ReviewAdminResponseDto> searchByOption(ReviewAdminSearchDto searchDto, Pageable pageable);
}
