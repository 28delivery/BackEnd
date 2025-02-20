package com.sparta.spring_deep._delivery.admin.service;

import com.sparta.spring_deep._delivery.admin.dto.AiLogResponseDto;
import com.sparta.spring_deep._delivery.admin.repository.AiRepository;
import com.sparta.spring_deep._delivery.domain.ai.Ai;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiLogService {

    private final AiRepository aiRepository;

    public Page<AiLogResponseDto> getAiLogsByRestaurant(UUID menuId, UUID restaurantId, int page,
        int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Ai> aiLogs;

        if (menuId != null) {
            // 메뉴 아이디가 제공되면, 해당 메뉴의 AI 로그 조회
            aiLogs = aiRepository.findByMenuId(menuId, pageable);
        } else if (restaurantId != null) {
            // 메뉴 아이디가 없고, 레스토랑 아이디가 제공되면, 해당 레스토랑의 모든 AI 로그 조회
            aiLogs = aiRepository.findByMenu_RestaurantId_Id(restaurantId, pageable);
        } else {
            // 둘 다 제공되지 않으면 전체 AI 로그 조회
            aiLogs = aiRepository.findAll(pageable);
        }

        return aiLogs.map(AiLogResponseDto::new);
    }
}
