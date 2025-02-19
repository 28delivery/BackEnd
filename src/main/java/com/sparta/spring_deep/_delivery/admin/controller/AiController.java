package com.sparta.spring_deep._delivery.admin.controller;

import com.sparta.spring_deep._delivery.admin.dto.AiLogResponseDto;
import com.sparta.spring_deep._delivery.admin.service.AiLogService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AiController {

    private final AiLogService aiLogService;

    @GetMapping("/admin/logs/ai/search")
    public ResponseEntity<Page<AiLogResponseDto>> searchAiLogs(
        @RequestParam UUID restaurantId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Page<AiLogResponseDto> logs = aiLogService.getAiLogsByRestaurant(restaurantId, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(logs);
    }

}
