package com.sparta.spring_deep._delivery.domain.ai;

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

    public Page<AiLogResponseDto> getAiLogsByRestaurant(UUID restaurantId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Ai> aiLogs = aiRepository.findByRestaurantId(restaurantId, pageable);

        return aiLogs.map(AiLogResponseDto::new);
    }
}
