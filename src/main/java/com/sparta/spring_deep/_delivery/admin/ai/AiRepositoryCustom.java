package com.sparta.spring_deep._delivery.admin.ai;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AiRepositoryCustom {

    Page<AiLogResponseDto> searchByOption(AiLogSearchDto aiLogSearchDto, Pageable pageable);
}
