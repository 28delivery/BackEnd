package com.sparta.spring_deep._delivery.admin.ai;

import com.sparta.spring_deep._delivery.domain.ai.Ai;
import com.sparta.spring_deep._delivery.exception.ResourceNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiLogService {

    private final AiRepository aiRepository;

    public Page<AiLogResponseDto> getAiLogsByRestaurant(UUID menuId, UUID restaurantId,
        Pageable pageable) {

        Page<Ai> aiLogs;

        if (menuId != null) {
            aiLogs = aiRepository.findByMenuId(menuId, pageable);
        } else if (restaurantId != null) {
            aiLogs = aiRepository.findByMenu_RestaurantId_Id(restaurantId, pageable);
        } else {
            aiLogs = aiRepository.findAll(pageable);
        }

        return aiLogs.map(AiLogResponseDto::new);
    }

    public Page<AiLogResponseDto> searchAiLogs(AiLogSearchDto aiLogSearchDto, Pageable pageable) {

        Page<AiLogResponseDto> aiLogs = aiRepository.searchByOption(aiLogSearchDto, pageable);

        // aiLog 검색 결과가 비어있다면 Exception 출력
        if (aiLogs.isEmpty()) {
            throw new ResourceNotFoundException();
        }

        return aiLogs;
    }
}
