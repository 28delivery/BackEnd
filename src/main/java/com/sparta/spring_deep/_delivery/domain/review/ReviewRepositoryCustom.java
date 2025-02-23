package com.sparta.spring_deep._delivery.domain.review;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryCustom {

    Page<ReviewResponseDto> searchByOptionAndIsDeletedFalse(UUID restaurantId,
        ReviewSearchDto searchDto, Pageable pageable);

}
