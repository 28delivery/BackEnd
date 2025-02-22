package com.sparta.spring_deep._delivery.admin.ai;

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
            aiLogs = aiRepository.findByMenuId(menuId, pageable);
        } else if (restaurantId != null) {
            aiLogs = aiRepository.findByMenu_RestaurantId_Id(restaurantId, pageable);
        } else {
            aiLogs = aiRepository.findAll(pageable);
        }

        return aiLogs.map(AiLogResponseDto::new);
    }
}
